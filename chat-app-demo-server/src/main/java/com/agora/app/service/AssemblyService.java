package com.agora.app.service;

import com.agora.app.model.AppUserInfo;

public interface AssemblyService {

    /**
     * 随机生成声网用户id，保证唯一
     * @param appkey appkey
     * @return agoraUid
     */
    String generateUniqueAgoraUid(String appkey);

    /**
     * 根据用户账号从DB获取对象
     * @param appkey appkey
     * @param userAccount 用户的账号
     * @return AppUserInfo
     */
    AppUserInfo getAppUserInfoFromDB(String appkey, String userAccount);

    /**
     * 检查用户账号是否存在于DB
     * @param appkey appkey
     * @param userAccount 用户的账号
     * @return boolean
     */
    boolean checkIfUserAccountExistsDB(String appkey, String userAccount);

    /**
     * 检查声网用户id是否存在于DB
     * @param appkey appkey
     * @param agoraUid 声网uid
     * @return boolean
     */
    boolean checkIfAgoraUidExistsDB(String appkey, String agoraUid);

    /**
     * 将用户信息存入DB
     * @param appkey appkey
     * @param userAccount 用户的账号
     * @param userNickname 用户的昵称
     * @param userPassword 用户的密码
     * @param chatUserName chat用户名
     * @param agoraUid 声网uid
     * @param avatarUrl 头像URL
     */
    void saveAppUserToDB(String appkey, String userAccount, String userNickname,
            String userPassword, String chatUserName, String agoraUid, String avatarUrl);

    /**
     * 将用户db信息更新
     * @param appUserInfo 头像url
     */
    void updateAppUserInfoToDB(AppUserInfo appUserInfo);
}
