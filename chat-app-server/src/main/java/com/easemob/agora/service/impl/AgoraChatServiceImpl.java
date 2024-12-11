package com.easemob.agora.service.impl;

import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AgoraChatService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.agora.chat.ChatTokenBuilder2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AgoraChatServiceImpl implements AgoraChatService {

    @Value("${application.agoraAppId}")
    private String appId;

    @Value("${application.agoraAppCert}")
    private String appCert;

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    private Cache<String, String> agoraChatAppTokenCache;

    @PostConstruct
    public void init() {
        agoraChatAppTokenCache = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(1, TimeUnit.DAYS).build();
    }

    @Override
    public TokenInfo getAgoraChatUserTokenWithAccount(AppUserInfo appUserInfo) {
        // 1.Get Agora Chat user uuid from db
        String chatUsername = appUserInfo.getAgoraChatUserName();

        // 2.Use an Agora App ID,  App Cert and Chat Username to get the Agora Chat user token
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        String userToken = builder.buildUserToken(appId, appCert, chatUsername, expirePeriod);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(userToken);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setChatUserName(appUserInfo.getAgoraChatUserName());
        return tokenInfo;
    }

    /**
     * Get the Agora app token
     * @return Agora app token
     */
    private String getAgoraAppToken() {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(appCert)) {
            throw new IllegalArgumentException("AppId or AppCert is not empty.");
        }

        // Use agora App Id、App Cert to generate agora app token
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        return builder.buildAppToken(appId, appCert, expirePeriod);
    }

    /**
     * Get Agora Chat App Token From Cache
     * @return Agora Chat App Token
     */
    private String getAgoraChatAppTokenFromCache() {
        try {
            return agoraChatAppTokenCache.get("agora-chat-app-token", () -> {
                return getAgoraAppToken();
            });
        } catch (Exception e) {
            log.error("get Agora Chat app token from cache. error : {}", e.getMessage());
            throw new IllegalArgumentException("Get Agora Chat app token from cache error.");
        }
    }

}
