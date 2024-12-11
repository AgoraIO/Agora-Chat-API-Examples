package com.easemob.agora.service;

import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;

public interface AgoraChatService {

    /**
     * Generate agoraChat user token
     *
     * @param appUserInfo appUserInfo
     * @return TokenInfo
     */
    TokenInfo getAgoraChatUserTokenWithAccount(AppUserInfo appUserInfo);
}
