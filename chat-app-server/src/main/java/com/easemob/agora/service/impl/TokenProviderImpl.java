package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASException;
import com.easemob.agora.exception.ASNotFoundException;
import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.AppUserInfoRepository;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.TokenProvider;
import com.easemob.agora.utils.RandomUidUtil;
import io.agora.media.AccessToken2;
import io.agora.media.RtcTokenBuilder2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenProviderImpl implements TokenProvider {

    @Value("${application.agoraAppId}")
    private String appId;

    @Value("${application.agoraAppCert}")
    private String appCert;

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Autowired
    private AppUserInfoRepository appUserInfoRepository;

    @Override public TokenInfo includeUserAndRtcPrivileges(String userAccount, String channelName,
            Boolean publisherRole) {
        AppUserInfo appUserInfo = appUserInfoRepository.findByUserAccount(userAccount);

        if (appUserInfo == null) {
            throw new ASNotFoundException(userAccount + " does not exists.");
        } else {
            String chatUserUsername = appUserInfo.getAgoraChatUserName();

            // The random number is used as the agoraUid here, but the uniqueness cannot be guaranteed. In actual development, please use the agoraUid that guarantees the uniqueness.
            String agoraUid = RandomUidUtil.getUid();

            RtcTokenBuilder2.Role role = null;
            if (publisherRole == Boolean.TRUE) {
                role = RtcTokenBuilder2.Role.ROLE_PUBLISHER;
            }

            AccessToken2 accessToken = new AccessToken2(appId, appCert, expirePeriod);

            AccessToken2.Service serviceChat = new AccessToken2.ServiceChat(chatUserUsername);
            serviceChat
                    .addPrivilegeChat(AccessToken2.PrivilegeChat.PRIVILEGE_CHAT_USER, expirePeriod);
            accessToken.addService(serviceChat);

            AccessToken2.Service serviceRtc = new AccessToken2.ServiceRtc(channelName, agoraUid);
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL,
                    expirePeriod);
            if (role == RtcTokenBuilder2.Role.ROLE_PUBLISHER) {
                serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_AUDIO_STREAM,
                        expirePeriod);
                serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_VIDEO_STREAM,
                        expirePeriod);
                serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_PUBLISH_DATA_STREAM,
                        expirePeriod);
            }
            accessToken.addService(serviceRtc);

            try {
                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.setToken(accessToken.build());
                tokenInfo.setExpireTimestamp(System.currentTimeMillis() + expirePeriod * 1000);
                tokenInfo.setChatUserName(appUserInfo.getAgoraChatUserName());
                tokenInfo.setAgoraUid(agoraUid);
                return tokenInfo;
            } catch (Exception e) {
                throw new ASException("token build error | " + e.getMessage());
            }
        }
    }
}
