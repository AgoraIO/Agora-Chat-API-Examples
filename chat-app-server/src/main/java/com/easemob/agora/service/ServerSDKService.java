package com.easemob.agora.service;

import com.easemob.im.server.api.token.Token;

public interface ServerSDKService {
    /**
     * Register agoraChat user for user account
     * In actual development, it is not recommended that the agoraChat username be the same as your own user account name
     *
     * @param chatUserName agoraChat username
     */
    void registerChatUserName(String chatUserName, String password);

    /**
     * Check if agoraChat user exists
     *
     * @param chatUserName agoraChat user name
     * @return boolean
     */
    boolean checkIfChatUserNameExists(String chatUserName);

    /**
     * Get the uuid of agoraChat user
     *
     * @param chatUserName agoraChat username
     * @return uuid
     */
    String getChatUserUuid(String chatUserName);

    /**
     * Generate agoraChat app token
     *
     * @return Token
     */
    Token generateAppToken();

    /**
     * Generate agoraChat user token
     *
     * @param chatUserName agoraChat username
     * @param chatUserId uuid of agoraChat user
     * @return User token
     */
    String generateAgoraChatUserToken(String chatUserName, String chatUserId);

    /**
     * Generate agora rtc token
     *
     * @param channelName agora channel name
     * @param agorauid agora uid
     * @return rtc token
     */
    String generateAgoraRtcToken(String channelName, Integer agorauid);
}
