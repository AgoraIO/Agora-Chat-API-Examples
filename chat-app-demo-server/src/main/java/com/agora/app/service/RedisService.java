package com.agora.app.service;


import com.agora.app.model.ChatGptMessage;

import java.util.List;
import java.util.Set;

public interface RedisService {
    /**
     * 保存声网频道信息
     * @param isRandomUid 是否为随机生成的uid
     * @param channelName 频道名称
     * @param uid uid是纯数字的声网用户id，即请求中携带的agoraUserId，
     *            如果agoraUserId值为0或为null，由服务端随机生成用于申请声网token
     */
    void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid);

    /**
     * 保存uid与agora user id的映射
     * @param uid 声网用户id
     * @param agoraUserId 声网用户id
     */
    void saveUidMapper(String uid, String agoraUserId);

    /**
     * 获取声网频道信息
     * @param channelName 频道名称
     * @return uid列表
     */
    Set<String> getAgoraChannelInfo(String channelName);

    /**
     * 获取存uid与agora user id的映射
     * @param uid 声网用户uid
     * @return agora user id
     */
    String getUidMapper(String uid);

    void decrNumberOfSendMessageToChatGpt(String appKey, String username);

    boolean checkIfSendMessageToChatGptLimit(String appKey, String username);

    void addMessageToRedis(String appkey, String groupId, ChatGptMessage message);

    List<String> getContextMessages(String appkey, String groupId);

    Long getContextMessagesCount(String appkey, String groupId);
}
