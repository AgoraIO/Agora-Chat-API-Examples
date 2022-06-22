package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASGetChatUserIdException;
import com.easemob.agora.exception.ASGetChatUserNameException;
import com.easemob.agora.exception.ASRegisterChatUserNameException;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.im.server.EMException;
import com.easemob.im.server.EMService;
import com.easemob.im.server.api.token.Token;
import com.easemob.im.server.api.token.agora.AccessToken2;
import com.easemob.im.server.exception.EMNotFoundException;
import com.easemob.im.server.model.EMUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServerSDKServiceImpl implements ServerSDKService {
    private static final String EASEMOB_USER_PASSWORD = "123456";

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Autowired
    private EMService serverSdk;

    @Override
    public void registerChatUserName(String chatUserName, String password) {
        try {
            this.serverSdk.user().create(chatUserName, password).block();
            log.info("register chatUserName success : {}", chatUserName);
        } catch (EMException e) {
            log.error("register chatUserName fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
        }
    }

    @Override
    public boolean checkIfChatUserNameExists(String chatUserName) {
        try {
            this.serverSdk.user().get(chatUserName).block();
        } catch (EMException e) {
            if (e.getClass() == EMNotFoundException.class) {
                log.info("chatUserName not exists :{}", chatUserName);
                return false;
            }

            throw new ASGetChatUserNameException(String.format("get chatUserName %s fail. Message : %s", chatUserName ,e.getMessage()));
        }

        return true;
    }

    @Override
    public String getChatUserUuid(String chatUserName) {
        EMUser user;

        try {
            user = this.serverSdk.user().get(chatUserName).block();
        } catch (EMException e) {
            throw new ASGetChatUserIdException(String.format("get chatUserId %s fail. Message : %s", chatUserName ,e.getMessage()));
        }

        return user.getUuid();
    }

    @Override
    public Token generateAppToken() {
        return this.serverSdk.token().getAppToken().block();
    }

    @Override
    public String generateAgoraChatUserToken(String chatUserName, String chatUserId) {
        EMUser user = new EMUser(chatUserName, chatUserId, true);
        return this.serverSdk.token().getUserToken(user, this.expirePeriod, null, null);
    }

    @Override
    public String generateAgoraRtcToken(String channelName, Integer agorauid) {
        EMUser bob = new EMUser("bob", "da921111-ecf9-11eb-9af3-296ff79acb67", true);

        String bobAgoraChatRtcToken = this.serverSdk.token().getUserToken(bob, this.expirePeriod, token -> {
            AccessToken2.ServiceRtc serviceRtc = new AccessToken2.ServiceRtc(channelName, String.valueOf(agorauid));
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, this.expirePeriod);
            token.addService(serviceRtc);
        }, null);

        return bobAgoraChatRtcToken;
    }
}
