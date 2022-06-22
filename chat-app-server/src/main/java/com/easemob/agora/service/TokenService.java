package com.easemob.agora.service;

import com.easemob.agora.model.TokenInfo;

public interface TokenService {

    /**
     * 获取 app 权限的 token
     * @return TokenInfo
     */
    TokenInfo getAppToken();

    /**
     * 根据用户账号获取 app 权限的 token
     * @param userAccount 用户账号
     * @return TokenInfo
     */
    TokenInfo getUserTokenWithAccount(String userAccount);

    /**
     * 根据频道名称与 agoraUid 获取声网 rtc token
     * @param channelName 频道名称
     * @param agoraUid 声网uid
     * @return TokenInfo
     */
    TokenInfo getRtcToken(String channelName, Integer agoraUid);
}
