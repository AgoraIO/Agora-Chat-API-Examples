package com.easemob.agora.service.impl;

import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AgoraChatService;
import com.easemob.agora.utils.agoratools.chat.ChatTokenBuilder2;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AgoraChatServiceImpl implements AgoraChatService {

    @Value("${application.appkey}")
    private String appkey;

    @Value("${application.base.uri}")
    private String domain;

    @Value("${application.agoraAppId}")
    private String appid;

    @Value("${application.agoraCert}")
    private String appcert;

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    private Cache<String, String> agoraChatAppTokenCache;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        agoraChatAppTokenCache = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(1, TimeUnit.DAYS).build();
    }

    /**
     * Register a agoraChat user for user account and password is 123
     * @param chatUserName chatUserName
     * @return chatUserName
     */
    @Override
    public String registerAgoraChatUser(String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = "http://" + domain + "/" + orgName + "/" + appName + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(getAgoraChatAppTokenFromCache());

        Map<String, String> body = new HashMap<>();
        body.put("username", chatUserName);
        body.put("password", "123");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("register chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("register chat user error ");
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("entities");
        return (String) results.get(0).get("uuid");
    }

    @Override
    public TokenInfo getAgoraChatUserTokenWithAccount(AppUserInfo appUserInfo) {
        // 1.Get Agora Chat user uuid from db
        String chatUserUuid = appUserInfo.getAgoraChatUserUuid();

        // 2.Use an Agora App ID,  App Cert and UUID to get the Agora Chat user token
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        String userToken = builder.buildUserToken(appid, appcert, chatUserUuid, expirePeriod);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(userToken);
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setChatUserName(appUserInfo.getAgoraChatUserName());
        return tokenInfo;
    }

    /**
     * Use an Agora Chat app token and a user ID to get the UUID
     * @param chatUserName chatUserName
     * @return AgoraChat user uuid
     */
    public String getChatUserUuid(String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = "http://" + domain + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // exchangeToken() get Agora Chat app token
        headers.setBearerAuth(getAgoraChatAppTokenFromCache());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("get chat user error ");
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) responseEntity.getBody().get("entities");
        return (String) results.get(0).get("uuid");
    }

    /**
     * Get the Agora app token
     * @return Agora app token
     */
    private String getAgoraAppToken() {
        if (!StringUtils.hasText(appid) || !StringUtils.hasText(appcert)) {
            throw new IllegalArgumentException("appid or appcert is not empty");
        }

        // Use agora App Id、App Cert to generate agora app token
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        return builder.buildAppToken(appid, appcert, expirePeriod);
    }

    /**
     * Get Agora Chat App Token From Cache
     * @return Agora Chat App Token
     */
    private String getAgoraChatAppTokenFromCache() {
        try {
            return agoraChatAppTokenCache.get("agora-chat-app-token", () -> {
                return exchangeToken();
            });
        } catch (Exception e) {
            log.error("get Agora Chat app token from cache. error : {}", e.getMessage());
            throw new IllegalArgumentException("Get Agora Chat app token from cache error");
        }
    }

    /**
     * Convert the Agora app token to Agora Chat app token
     * @return Agora Chat app token
     */
    private String exchangeToken() {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = "http://" + domain + "/" + orgName + "/" + appName + "/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(getAgoraAppToken());

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "agora");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("exchange token. error : {}", e.getMessage());
            throw new RestClientException("exchange token error ");
        }
        return (String) Objects.requireNonNull(response.getBody()).get("access_token");
    }

}
