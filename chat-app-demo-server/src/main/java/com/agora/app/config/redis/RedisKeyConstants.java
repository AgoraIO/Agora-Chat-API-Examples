package com.agora.app.config.redis;

public final class RedisKeyConstants {
    public static final String AGORA_CHANNEL_INFO = "agora:channel:info:%s:%s";

    public static final String AGORA_UID = "agora:uid:%s:%s";

    public static final String SEND_MESSAGE_TO_CHATGPT_COUNT_LIMIT_DAY = "send:message:to:chatgpt:count:limit:day:%s:%s";

    public static final String CHAT_GROUP_MESSAGES_CONTENT_RECORD = "chat:group:messages:content:record:%s:%s";

    private RedisKeyConstants() {}
}
