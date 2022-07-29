package com.easemob.agora.service;

import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;

public interface AgoraChatService {
    /**
     * Register agoraChat user for user account
     * In actual development, it is not recommended that the agoraChat username be the same as your own user account name
     *
     * @param userAccount userAccount
     * @return chatUserName
     */
    String registerAgoraChatUser(String userAccount);

    /**
     * Generate agoraChat user token
     *
     * @param appUserInfo appUserInfo
     * @return TokenInfo
     */
    TokenInfo getAgoraChatUserTokenWithAccount(AppUserInfo appUserInfo);
}
