package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASNotFoundException;
import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AssemblyService;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.agora.service.TokenService;
import com.easemob.im.server.api.token.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Autowired
    private ServerSDKService serverSDKService;

    @Autowired
    private AssemblyService assemblyService;

    @Override public TokenInfo getAppToken() {
        TokenInfo tokenInfo = new TokenInfo();
        Token appToken = this.serverSDKService.generateAppToken();
        tokenInfo.setToken(appToken.getValue());
        tokenInfo.setExpireTimestamp(appToken.getExpireAt().toEpochMilli() / 1000);
        return tokenInfo;
    }

    @Override
    public TokenInfo getUserTokenWithAccount(String userAccount) {
        AppUserInfo appUserInfo = this.assemblyService.getAppUserInfoFromDB(userAccount);

        if(appUserInfo != null) {
            String chatUserName = appUserInfo.getChatUserName();
            String chatUserId = this.serverSDKService.getChatUserUuid(chatUserName);

            return getTokenInfo(chatUserName, chatUserId, appUserInfo.getAgoraUid());
        } else {
            throw new ASNotFoundException(String.format("%s not exists", userAccount));
        }
    }

    @Override
    public TokenInfo getRtcToken(String channelName, Integer agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();

        tokenInfo.setToken(this.serverSDKService.generateAgoraRtcToken(channelName, agoraUid));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        return tokenInfo;
    }

    private TokenInfo getTokenInfo(String chatUserName, String chatUserId, String agoraUid) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(this.serverSDKService.generateAgoraChatUserToken(chatUserName, chatUserId));
        tokenInfo.setExpireTimestamp(System.currentTimeMillis() + this.expirePeriod * 1000);
        tokenInfo.setChatUserName(chatUserName);
        tokenInfo.setAgoraUid(agoraUid);

        return tokenInfo;
    }
}
