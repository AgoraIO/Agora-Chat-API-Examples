package com.easemob.agora.service;

import com.easemob.agora.model.AppUserInfo;

public interface AssemblyService {

    /**
     * Register a user on app server
     *
     * @param userAccount User account
     * @param userPassword User password
     */
    void registerUserAccount(String userAccount, String userPassword);

    /**
     * Randomly generated Agora Uid
     *
     * @return agoraUid
     */
    String generateUniqueAgoraUid();

    /**
     * Get user information from db
     *
     * @param userAccount User account
     * @return AppUserInfo
     */
    AppUserInfo getAppUserInfoFromDB(String userAccount);

    /**
     * Check if user account exists in db
     *
     * @param userAccount User account
     * @return boolean
     */
    boolean checkIfUserAccountExistsDB(String userAccount);

    /**
     * Check if agora uid exists in db
     *
     * @param agoraUid agora uid
     * @return boolean
     */
    boolean checkIfAgoraUidExistsDB(String agoraUid);

    /**
     * Save user info to db
     *
     * @param userAccount User account
     * @param userPassword User password
     * @param chatUserName agoraChat user name
     * @param agoraUid agora uid
     */
    void saveAppUserToDB(String userAccount, String userPassword, String chatUserName, String agoraUid);
}
