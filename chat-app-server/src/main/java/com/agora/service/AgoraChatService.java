package com.agora.service;

import com.agora.model.AppUserInfo;
import com.agora.model.TokenInfo;

public interface AgoraChatService {

    /**
     * Generate agoraChat user token
     *
     * @param appUserInfo appUserInfo
     * @return TokenInfo
     */
    TokenInfo getAgoraChatUserTokenWithAccount(AppUserInfo appUserInfo);
}
