package com.agora.app.service.impl;

import com.agora.app.model.ChatGptMessage;
import com.agora.app.model.ChatGptRequest;
import com.agora.app.model.ChatGptTextGenerateImageRequest;
import com.agora.app.service.ChatGptService;
import com.agora.app.service.RedisService;
import com.agora.app.utils.JsonUtil;
import com.agora.app.utils.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatGptServiceImpl implements ChatGptService {

    private static final String CHOICES = "choices";
    private static final String MESSAGE = "message";
    private static final String CONTENT = "content";
    private static final String USER = "user";
    private static final String SYSTEM = "system";
    private static final String SYSTEM_CONTENT = "You are a helpful assistant.";
    private final static String TEXT_TYPE = "text";
    private final static String IMAGE_TYPE = "image_url";

    private RestTemplate restTemplate;

    private RedisService redisService;

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.url}")
    private String url;

    @Value("${chatgpt.image.url}")
    private String imageUrl;

    @Value("${chatgpt.model}")
    private String model;

    @Value("${chatgpt.image.model}")
    private String imageModel;

    @Value("${chatgpt.max.tokens}")
    private Integer maxTokens;

    @Value("${chatgpt.temperature}")
    private Float temperature;

    @Value("${chatgpt.image.size}")
    private String imageSize;

    @Value("${chatgpt.image.n}")
    private Integer imageN;

    @Value("${split.chatgpt.answer.message.length}")
    private Integer splitLength;

    @Value("${send.message.to.chatgpt.count.limit.switch}")
    private Boolean countLimitSwitch;

    public ChatGptServiceImpl(RestTemplate restTemplate,
            RedisService redisService) {
        this.restTemplate = restTemplate;
        this.redisService = redisService;
    }

    @Override public String sendChatMessage(String appKey, String username, String messageContent) {
        log.info("start send chat message to chatGPT. appKey : {}, username : {}", appKey, username);

        List<ChatGptMessage> messageList = new ArrayList<>();
        ChatGptMessage messages = new ChatGptMessage();
        messages.setRole(USER);
        messages.setContent(buildContentEntityList(null, messageContent));

        messageList.add(messages);

        List<Map<String, Object>> chatGptMessages = new ArrayList<>();
        messageList.forEach(message -> {
            Map<String, Object> map = JsonUtil.convert(message);
            chatGptMessages.add(map);
        });

        String result = null;
        int retry = 3;

        while (true) {
            try {
                result = sendChatMessageToChatGpt(appKey, username, chatGptMessages);
                break;
            } catch (Exception e) {
                log.error(
                        "chatGPT answer chat message error. appKey : {}, username : {}, e : {}",
                        appKey, username, e.getMessage());
                retry--;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.warn(
                        "chatGPT answer chat message | thread sleep fail. appKey : {}, username : {}, e : {}",
                        appKey, username, e.getMessage());
            }

            if (retry == 0) {
                break;
            }
        }

        return result;
    }

    @Override
    public String sendChatGroupMessage(String appKey, String username, String groupId, String messageContent) {
        log.info("start send chat group message to chatGPT. appKey : {}, groupId : {}, username : {}",
                appKey, groupId, username);

        List<ChatGptMessage> messageList = new ArrayList<>();

        redisService.getContextMessages(appKey, groupId).forEach(message -> {
            messageList.add(JsonUtil.parse(message, ChatGptMessage.class));
        });

        ChatGptMessage systemMessages = new ChatGptMessage();
        systemMessages.setRole(SYSTEM);
        systemMessages.setContent(buildContentEntityList(null, SYSTEM_CONTENT));

        messageList.add(systemMessages);

        Collections.reverse(messageList);

        ChatGptMessage userMessage = new ChatGptMessage();
        userMessage.setRole(USER);
        userMessage.setContent(buildContentEntityList(username, messageContent));

        messageList.add(userMessage);

        redisService.addMessageToRedis(appKey, groupId, userMessage);

        log.info("redis context messages. appkey : {}, groupId : {}, messages :{}", appKey, groupId, messageList);

        List<Map<String, Object>> chatGptMessages = new ArrayList<>();
        messageList.forEach(message -> {
            Map<String, Object> map = JsonUtil.convert(message);
            chatGptMessages.add(map);
        });

        String result = null;
        int retry = 3;

        while (true) {
            try {
                result = sendGroupMessageToChatGpt(appKey, username, groupId, chatGptMessages);
                break;
            } catch (Exception e) {
                log.error(
                        "chatGPT answer chat group message error. appKey : {}, groupId : {}, username : {}, e : {}",
                        appKey, groupId, username, e.getMessage());
                retry--;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.warn(
                        "chatGPT answer chat group message | thread sleep fail. appKey : {}, groupId : {}, username : {}, e : {}",
                        appKey, groupId, username, e.getMessage());
            }

            if (retry == 0) {
                break;
            }
        }

        return result;
    }

    @Override public String sendTextGenerateImageMessage(String appKey, String username,
            String messageContent) {
        ChatGptTextGenerateImageRequest
                chatGptImageRequest = new ChatGptTextGenerateImageRequest(imageModel, messageContent, imageN, imageSize);

        ResponseEntity<Map> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, getHeaderAndBody(chatGptImageRequest), Map.class);

        if (responseEntity.getBody() == null) {
            log.error("chatGPT answer message body is null.");
            return null;
        }
        return null;
    }

    private String sendChatMessageToChatGpt(String appKey, String username, List<Map<String, Object>> messageList) {
        ChatGptRequest
                chatGptRequest = new ChatGptRequest(model, messageList, temperature, maxTokens);

        ResponseEntity<Map> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, getHeaderAndBody(chatGptRequest), Map.class);

        if (responseEntity.getBody() == null) {
            log.error("chatGPT answer message body is null.");
            return null;
        }

        List<Map> choices = (List<Map>) responseEntity.getBody().get(CHOICES);
        if (choices != null && choices.size() > 0) {
            Map<String, String> message = (Map<String, String>) choices.get(0).get(MESSAGE);
            if (message != null) {
                String content = message.get(CONTENT);
                if (countLimitSwitch) {
                    redisService.decrNumberOfSendMessageToChatGpt(appKey, username);
                }
                String tempText = removeLeadingNewlines(content);
                if (tempText.length() > splitLength) {
                    return StringUtils.substring(content,0, splitLength);
                } else {
                    return tempText;
                }
            } else {
                return null;
            }
        } else {
            log.error("chatGPT answer text is null.");
            return null;
        }
    }

    private String sendGroupMessageToChatGpt(String appKey, String username, String groupId, List<Map<String, Object>> messageList) {
        ChatGptRequest chatGptRequest = new ChatGptRequest(model, messageList, temperature, maxTokens);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, getHeaderAndBody(chatGptRequest), Map.class);

        if (responseEntity.getBody() == null) {
            log.error("chat group | chatGPT answer message body is null. groupId : {}", groupId);
            return null;
        }

        List<Map> choices = (List<Map>) responseEntity.getBody().get(CHOICES);
        if (choices != null && choices.size() > 0) {
            Map<String, String> message = (Map<String, String>) choices.get(0).get(MESSAGE);
            if (message != null) {
                String content = message.get(CONTENT);
                if (countLimitSwitch) {
                    redisService.decrNumberOfSendMessageToChatGpt(appKey, username);
                }
                return removeLeadingNewlines(content);
            } else {
                return null;
            }
        } else {
            log.error("chat group | chatGPT answer text is null. groupId : {}", groupId);
            return null;
        }
    }

    private HttpEntity<Object> getHeaderAndBody(Object body) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + apiKey);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, requestHeaders);
    }

    private static String removeLeadingNewlines(String input) {
        String pattern = "^(\\r?\\n|\\?\\r?\\n)*";
        return input.replaceAll(pattern, "");
    }

    private List<ChatGptMessage.ContentEntity> buildContentEntityList(String from, String message) {
        List<ChatGptMessage.ContentEntity> contentEntityList = new ArrayList<>();

        if (from == null) {
            ChatGptMessage.ContentEntity textContentEntity = new ChatGptMessage.ContentEntity();
            textContentEntity.setText(message);
            textContentEntity.setType(TEXT_TYPE);
            contentEntityList.add(textContentEntity);
        } else {
            boolean result = UrlUtil.containsUrl(message);
            if (result) {
                List<String> urls = UrlUtil.extractUrls(message);
                for (String url : urls) {
                    ChatGptMessage.ContentEntity imageContentEntity = new ChatGptMessage.ContentEntity();
                    ChatGptMessage.ImageUrl ci = new ChatGptMessage.ImageUrl();
                    ci.setUrl(url);
                    imageContentEntity.setImageUrl(ci);
                    imageContentEntity.setType(IMAGE_TYPE);
                    contentEntityList.add(imageContentEntity);

                    ChatGptMessage.ContentEntity textContentEntity = new ChatGptMessage.ContentEntity();
                    textContentEntity.setText(String.format("%s : I sent this image.", from));
                    textContentEntity.setType(TEXT_TYPE);
                    contentEntityList.add(textContentEntity);
                }
            }

            ChatGptMessage.ContentEntity textContentEntity = new ChatGptMessage.ContentEntity();
            textContentEntity.setText(String.format("%s said : %s", from, message));
            textContentEntity.setType(TEXT_TYPE);
            contentEntityList.add(textContentEntity);
        }

        return contentEntityList;
    }

}
