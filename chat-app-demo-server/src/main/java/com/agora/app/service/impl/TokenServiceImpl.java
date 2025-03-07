package com.agora.app.service.impl;

import com.agora.app.exception.ASNotFoundException;
import com.agora.app.model.AppUserInfo;
import com.agora.app.model.TokenInfo;
import com.agora.app.service.AssemblyService;
import com.agora.app.service.RestService;
import com.agora.app.service.TokenService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.agora.chat.ChatTokenBuilder2;
import io.agora.media.AccessToken2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Value("${application.appkey}")
    private String defaultAppkey;

    @Value("${application.agoraAppId}")
    private String agoraAppId;

    @Value("${application.agoraAppCert}")
    private String agoraAppCert;

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    private Cache<String, String> agoraChatAppTokenCache;

    @PostConstruct
    public void init() {
        agoraChatAppTokenCache = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    @Override
    public TokenInfo getUserTokenWithAccount(String appkey, String userAccount) {
        log.info("userAccount get user token :{}", userAccount);
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(appkey, userAccount);
        if(appUserInfo != null) {
            String chatUserName = appUserInfo.getChatUserName();
            String chatUserId = this.restService.getChatUserUuid(appkey, chatUserName);
            return getTokenInfo(chatUserId, appUserInfo.getAgoraUid());
        } else {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        }
    }

    @Override public String getAppToken() {
        try {
            return agoraChatAppTokenCache.get("agora-chat-app-token", () -> {
                ChatTokenBuilder2 builder = new ChatTokenBuilder2();
                return builder.buildAppToken(agoraAppId, agoraAppCert, 86400);
            });
        } catch (Exception e) {
            log.error("get Agora Chat app token from cache. error : {}", e.getMessage());
            throw new IllegalArgumentException("Get Agora Chat app token from cache error.");
        }
    }

    @Override
    public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        AccessToken2 accessToken2 = new AccessToken2(agoraAppId, agoraAppCert, 86400);
        AccessToken2.Service serviceRtc = new AccessToken2.ServiceRtc(channelName, String.valueOf(agoraUid));

        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, 86400);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_AUDIO_STREAM, 86400);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_VIDEO_STREAM, 86400);
        serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_DATA_STREAM, 86400);
        accessToken2.addService(serviceRtc);

        String token;
        try {
            token = accessToken2.build();
        } catch (Exception e) {
            token = "";
            log.error("generate agora rtc token error. e : {}", e.getMessage());
        }

        tokenInfo.setToken(token);
        tokenInfo.setAgoraUid(String.valueOf(agoraUid));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + 86400 * 1000);
        return tokenInfo;
    }

    @Override public TokenInfo getRtcToken(String channelName, String userAccount) {
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(defaultAppkey, userAccount);
        if(appUserInfo == null) {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        } else {
            String agoraUid = appUserInfo.getAgoraUid();

            return getRtcToken(channelName, Integer.valueOf(agoraUid));
        }
    }

    private TokenInfo getTokenInfo(String chatUserUuid, String agoraUid) {
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        String userToken = builder.buildUserToken(agoraAppId, agoraAppCert, chatUserUuid, expirePeriod);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(userToken);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setAgoraUid(agoraUid);
        return tokenInfo;
    }
}
