package com.agora.app.service.impl;

import com.agora.app.exception.ASDuplicateUniquePropertyExistsException;
import com.agora.app.exception.ASNotFoundException;
import com.agora.app.exception.ASPasswordErrorException;
import com.agora.app.model.*;
import com.agora.app.service.*;
import com.agora.app.utils.FileCovert;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService, InitializingBean {

    private final static String TEXT_TYPE = "text";

    private final static String USER = "user";
    private static final String ASSISTANT = "assistant";
    private static final String DEFAULT_CHAT_GROUP_MEMBER_NAME_BELLA = "Bella";

    private static final String DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES = "Miles";
    private static final String AI_CHAT_BOT_USER_PROMPT =
            "Hello, I'm the Agora AI chatbot. I can understand human language and generate content, serving as your intelligent assistant for both life and work.";
    private static final String AI_CHAT_BOT_GROUP_PROMPT =
            "Welcome to this group powered by Agora Chat! I am an AI companion and your assistant. Feel free to @AgoraChatAI to start a conversation with me within this group.";
    private static final String MILES_GROUP_MESSAGE_A = "Dinner party on Sunday？";
    private static final String BELLA_GROUP_MESSAGE_A = "I'm in，may be vegetarian?";
    private static final String MILES_GROUP_MESSAGE_B = "Err, I might need some ideas...";
    private static final String MILES_GROUP_MESSAGE_C = "@AI Chatbot Vegetarian recipe ideas";
    private static final String AI_CHAT_BOT_GROUP_ANSWER_MESSAGE = "Certainly! Here are some vegetarian recipe ideas for your dinner party on Sunday:\n"
            + "1. Roasted Veggie Tacos: Include a variety of roasted vegetables like bell peppers, zucchini, and onions, and serve with your favorite toppings like avocado, salsa, and lime.\n"
            + "2. Stuffed Bell Peppers: Fill bell peppers with a mixture of quinoa, black beans, corn, and spices, and bake until tender.\n"
            + "3. Lentil Shepherd's Pie: A comforting dish made with lentils, vegetables, and topped with mashed potatoes.\n"
            + "4. Caprese Salad Skewers: Skewer cherry tomatoes, fresh basil leaves, and mini mozzarella balls, then drizzle with balsamic glaze.\n"
            + "5. Spinach and Feta Stuffed Mushrooms: Stuff mushrooms with a mixture of sautéed spinach, feta cheese, breadcrumbs, and herbs, then bake until golden brown.\n"
            + "I hope these ideas inspire your vegetarian dinner party menu! Let me know if you need more suggestions.";
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Autowired
    private RedisService redisService;

    @Value("${application.appkey}")
    private String defaultAppkey;

    @Value("${agora.chat.robot.name}")
    private String chatRobotName;

    @Value("${application.baseUri}")
    private String baseUri;

    @Value("${agora.thread.pool.core.size}")
    private Integer coreSize;

    @Value("${agora.thread.pool.max.size}")
    private Integer maxSize;

    @Value("${agora.thread.pool.keepAlive.seconds}")
    private Integer keepAlive;

    @Value("${agora.thread.pool.queue.capacity}")
    private Integer queueCapacity;

    @Value("${agora.call.rest.api.time.interval:200}")
    private Long callRestApiInterval;

    @Value("${agora.chat.robot.enable.switch:false}")
    private Boolean isUseAIChatBot;

    private ThreadPoolExecutor threadPool;

    @Override public void afterPropertiesSet() throws Exception {
        threadPool = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAlive,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadFactoryBuilder().setNameFormat("chat-gpt").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerWithChatUser(AppUser appUser) {
        String chatUserName = appUser.getUserAccount().toLowerCase();
        String chatUserPassword = appUser.getUserPassword();

        if (this.assemblyService.checkIfUserAccountExistsDB(defaultAppkey, chatUserName)) {
            throw new ASDuplicateUniquePropertyExistsException(
                    "userAccount " + chatUserName + " already exists");
        } else {
            this.assemblyService.saveAppUserToDB(defaultAppkey, chatUserName, null,
                    chatUserPassword, chatUserName,
                    this.assemblyService.generateUniqueAgoraUid(defaultAppkey), null);

            if (!this.restService.checkIfChatUserNameExists(defaultAppkey, chatUserName)) {
                this.restService.registerChatUserName(defaultAppkey, chatUserName);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserLoginResponse loginWithChatUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount().toLowerCase();
        String userPassword = appUser.getUserPassword();

        AppUserInfo userInfo = this.assemblyService.getAppUserInfoFromDB(defaultAppkey, userAccount);
        String avatarUrl = null;
        if (userInfo == null) {
            this.assemblyService.saveAppUserToDB(defaultAppkey, userAccount, null,
                    userPassword, userAccount,
                    this.assemblyService.generateUniqueAgoraUid(defaultAppkey), null);

            if (!this.restService.checkIfChatUserNameExists(defaultAppkey, userAccount)) {
                this.restService.registerChatUserName(defaultAppkey, userAccount);
            }

            if (isUseAIChatBot) {
                threadPool.execute(() -> {
                    try {
                        handleBusinessData(userAccount);
                    } catch (InterruptedException e) {
                        log.error("handle business data error. e : {}", e.getMessage());
                    }
                });
            }
        } else {
            if (!userInfo.getUserPassword().equals(appUser.getUserPassword())) {
                throw new ASPasswordErrorException("userAccount password error");
            }

            if (!this.restService.checkIfChatUserNameExists(defaultAppkey, userAccount)) {
                this.restService.registerChatUserName(defaultAppkey, userAccount);
            }

            avatarUrl = userInfo.getAvatarUrl();
        }

        TokenInfo tokenInfo = this.tokenService.getUserTokenWithAccount(defaultAppkey, userAccount);

        UserLoginResponse response = new UserLoginResponse();
        response.setToken(tokenInfo.getToken());
        response.setExpireTimestamp(tokenInfo.getExpireTimestamp());
        response.setAgoraUid(tokenInfo.getAgoraUid());
        response.setUserName(userAccount);
        response.setAvatarUrl(avatarUrl);

        return response;
    }

    @Override public String uploadAvatar(String appkey, String userAccount, MultipartFile file) {
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
        if (appUserInfo == null) {
            throw new ASNotFoundException("The user account not found.");
        }

        File chatFile = FileCovert.convertMultipartFileToFile(appkey, userAccount, file);
        String avatarUrl = restService.uploadFile(appkey, userAccount, chatFile);
        chatFile.delete();
        appUserInfo.setAvatarUrl(avatarUrl);
        assemblyService.updateAppUserInfoToDB(appUserInfo);
        return avatarUrl;
    }

    private void handleBusinessData(String chatUserName) throws InterruptedException {
        this.restService.addContact(defaultAppkey, chatUserName, chatRobotName);
        this.restService.sendTextMessageToUser(defaultAppkey, chatRobotName, chatUserName,
                AI_CHAT_BOT_USER_PROMPT, null);

        String groupId = this.restService.createChatGroup(defaultAppkey, chatUserName);
        Thread.sleep(callRestApiInterval);

        this.restService.sendTextMessageToGroup(defaultAppkey, chatRobotName, groupId,
                AI_CHAT_BOT_GROUP_PROMPT, null);
        Thread.sleep(callRestApiInterval);

        this.restService.sendTextMessageToGroup(defaultAppkey, DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES, groupId,
                MILES_GROUP_MESSAGE_A, null);
        this.redisService.addMessageToRedis(defaultAppkey, groupId, buildChatGptMsg(USER, DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES, MILES_GROUP_MESSAGE_A));
        Thread.sleep(callRestApiInterval);

        this.restService.sendTextMessageToGroup(defaultAppkey, DEFAULT_CHAT_GROUP_MEMBER_NAME_BELLA, groupId,
                BELLA_GROUP_MESSAGE_A, null);
        this.redisService.addMessageToRedis(defaultAppkey, groupId, buildChatGptMsg(USER, DEFAULT_CHAT_GROUP_MEMBER_NAME_BELLA, BELLA_GROUP_MESSAGE_A));
        Thread.sleep(callRestApiInterval);

        this.restService.sendTextMessageToGroup(defaultAppkey, DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES, groupId,
                MILES_GROUP_MESSAGE_B, null);
        this.redisService.addMessageToRedis(defaultAppkey, groupId, buildChatGptMsg(USER, DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES, MILES_GROUP_MESSAGE_B));
        Thread.sleep(callRestApiInterval);

        this.restService.sendTextMessageToGroup(defaultAppkey, DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES, groupId,
                MILES_GROUP_MESSAGE_C, null);
        this.redisService.addMessageToRedis(defaultAppkey, groupId, buildChatGptMsg(USER, DEFAULT_CHAT_GROUP_MEMBER_NAME_MILES, MILES_GROUP_MESSAGE_C));
        Thread.sleep(callRestApiInterval);

        this.restService.sendTextMessageToGroup(defaultAppkey, chatRobotName, groupId,
                AI_CHAT_BOT_GROUP_ANSWER_MESSAGE, null);

        this.redisService.addMessageToRedis(defaultAppkey, groupId, buildChatGptMsg(ASSISTANT, null, AI_CHAT_BOT_GROUP_ANSWER_MESSAGE));
    }

    private ChatGptMessage buildChatGptMsg(String role, String from, String message) {
        ChatGptMessage chatGptMessage = new ChatGptMessage();
        chatGptMessage.setRole(role);
        chatGptMessage.setContent(buildContentEntityList(from, message));

        return chatGptMessage;
    }

    private List<ChatGptMessage.ContentEntity> buildContentEntityList(String from, String message) {
        List<ChatGptMessage.ContentEntity> contentEntityList = new ArrayList<>();

        ChatGptMessage.ContentEntity textContentEntity = new ChatGptMessage.ContentEntity();
        textContentEntity.setType(TEXT_TYPE);

        if (from == null) {
            textContentEntity.setText(message);
        } else {
            textContentEntity.setText(String.format("%s said : %s", from, message));
        }

        contentEntityList.add(textContentEntity);

        return contentEntityList;
    }

}
