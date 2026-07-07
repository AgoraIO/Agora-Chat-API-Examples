package com.agora.app.service;

public interface ChatGptService {

    String sendChatMessage(String appKey, String username, String messageContent);

    String sendChatGroupMessage(String appKey, String username, String groupId, String messageContent);

    String sendTextGenerateImageMessage(String appKey, String username, String messageContent);

}
