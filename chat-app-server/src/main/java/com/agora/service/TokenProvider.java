package com.agora.service;

import com.agora.model.TokenInfo;

public interface TokenProvider {

    /**
     * Generate a token including user and rtc Privileges
     *
     * @param userAccount   userAccount
     * @param channelName   channelName
     * @param publisherRole publisherRole
     * @return TokenInfo
     */
    TokenInfo includeUserAndRtcPrivileges(String userAccount, String channelName,
            Boolean publisherRole);

}
