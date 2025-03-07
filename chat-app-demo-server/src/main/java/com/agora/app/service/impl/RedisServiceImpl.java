package com.agora.app.service.impl;

import com.agora.app.config.redis.RedisKeyConstants;
import com.agora.app.model.ChatGptMessage;
import com.agora.app.service.RedisService;
import com.agora.app.utils.JsonUtil;
import com.agora.app.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Qualifier("channelRedis")
    private StringRedisTemplate redisTemplate;

    @Value("${application.agoraAppId}")
    private String agoraAppId;

    @Value("${spring.redis.channel.expireTime}")
    private long expireTime;

    @Value("${send.message.to.chatgpt.day.count.limit}")
    private long sendMessageToChatGptDayCount;

    @Value("${chat.group.messages.context.count.limit:5}")
    private Long contextCountLimit;

    @Override
    public void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid) {
        Long result;
        String redisKey = String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, agoraAppId, channelName);

        while (true) {
            if (isRandomUid) {
                try {

                    result = redisTemplate.opsForSet().add(redisKey, uid);

                    if (result != null) {
                        if (result == 1) {
                            redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);
                            break;
                        } else {
                            uid = RandomUidUtil.getUid();
                        }
                    } else {
                        log.error("result is empty. channelName : {}, uid : {}", channelName, uid);
                    }
                } catch (Exception e) {
                    log.error("save agora channel info failed - isRandomUid. Message - {}", e.getMessage(), e);
                }
            } else {
                try {
                    redisTemplate.opsForSet().add(redisKey, uid);
                    redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("save agora channel info failed. Message - {}", e.getMessage(), e);
                }
                break;
            }
        }
    }

    @Override
    public void saveUidMapper(String uid, String agoraUserId) {
        try {
            String redisKey = String.format(RedisKeyConstants.AGORA_UID, agoraAppId, uid);
            redisTemplate.opsForValue().set(redisKey, agoraUserId, Duration.ofSeconds(expireTime));
        } catch (Exception e) {
            log.error("save uid mapper failed. Message - {}", e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getAgoraChannelInfo(String channelName) {
        Set<String> channelInfo = null;

        try {
            String rediskey = String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, agoraAppId, channelName);

            channelInfo = redisTemplate.opsForSet().members(rediskey);
        } catch (Exception e) {
            log.error("get agora channel info failed. Message - {}", e.getMessage());
        }

        if (channelInfo == null) {
            return Collections.emptySet();
        }

        return channelInfo;
    }

    @Override
    public String getUidMapper(String uid) {
        String agoraUserId = null;

        try {
            String redisKey = String.format(RedisKeyConstants.AGORA_UID, agoraAppId, uid);
            agoraUserId = redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.error("get uid mapper failed. Message - {}", e.getMessage(), e);
        }

        if (StringUtils.isBlank(agoraUserId)) {
            return "";
        }

        return agoraUserId;
    }

    @Override public void decrNumberOfSendMessageToChatGpt(String appKey, String username) {
        String redisKey = String.format(RedisKeyConstants.SEND_MESSAGE_TO_CHATGPT_COUNT_LIMIT_DAY,
                appKey.toLowerCase(), username.toLowerCase());

        try {
            redisTemplate.opsForValue().decrement(redisKey);
        } catch (Exception e) {
            log.error(
                    "redis | decr send message to chatGpt fail. appKey : {}, username: {}, error : {}",
                    appKey, username, e.getMessage());
        }
    }

    @Override public boolean checkIfSendMessageToChatGptLimit(String appKey, String username) {
        String redisKey = String.format(RedisKeyConstants.SEND_MESSAGE_TO_CHATGPT_COUNT_LIMIT_DAY,
                appKey.toLowerCase(), username.toLowerCase());

        try {
            String dayLimit = redisTemplate.opsForValue().get(redisKey);
            if (dayLimit == null) {
                redisTemplate.opsForValue().set(redisKey, String.valueOf(sendMessageToChatGptDayCount));
                redisTemplate.expire(redisKey, expireTime, TimeUnit.SECONDS);
                return false;
            } else {
                return Integer.parseInt(dayLimit) == 0;
            }
        } catch (Exception e) {
            log.error(
                    "redis | check send message to chatGpt fail. appKey : {}, username: {}, error : {}",
                    appKey, username, e.getMessage());
            return true;
        }
    }

    @Override public void addMessageToRedis(String appkey, String groupId, ChatGptMessage message) {
        if (getContextMessagesCount(appkey, groupId) == contextCountLimit) {
            popContextMessage(appkey, groupId);
        }

        pushContextMessage(appkey, groupId, JsonUtil.mapToJsonString(message));
    }

    @Override public List<String> getContextMessages(String appkey, String groupId) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            return redisTemplate.opsForList().range(redisKey, 0, -1);
        } catch (Exception e) {
            log.error("redis | get context messages fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
            return null;
        }
    }

    @Override public Long getContextMessagesCount(String appkey, String groupId) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            Long count = redisTemplate.opsForList().size(redisKey);
            if (count == null) {
                return 0L;
            }

            return count;
        } catch (Exception e) {
            log.error("redis | get context messages count fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
            return 0L;
        }
    }

    private void pushContextMessage(String appkey, String groupId, String messageContent) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            redisTemplate.opsForList().leftPush(redisKey, messageContent);
        } catch (Exception e) {
            log.error("redis | push context message fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
        }
    }

    private void popContextMessage(String appkey, String groupId) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            redisTemplate.opsForList().rightPop(redisKey);
        } catch (Exception e) {
            log.error("redis | pop context message fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
        }
    }
}
