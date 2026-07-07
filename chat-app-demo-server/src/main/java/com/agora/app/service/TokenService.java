package com.agora.app.service;

import com.agora.app.model.TokenInfo;

public interface TokenService {

    /**
     * 根据用户账号获取USER权限的token
     * @param appkey appkey
     * @param userAccount 用户账号
     * @return TokenInfo
     */
    TokenInfo getUserTokenWithAccount(String appkey, String userAccount);

    /**
     * 获取APP权限的token
     * @return app token
     */
    String getAppToken();

    /**
     * 根据频道名称与agoraUid获取声网rtc token
     * @param channelName 频道名称
     * @param agoraUid 声网uid
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, Integer agoraUid);

    /**
     * 根据频道名称与用户账号获取声网rtc token
     * @param channelName 频道名称
     * @param userAccount 用户账号
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, String userAccount);
}
