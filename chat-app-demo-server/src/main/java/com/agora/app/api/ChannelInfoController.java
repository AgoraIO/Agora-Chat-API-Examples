package com.agora.app.api;

import com.agora.app.model.ChannelResponse;
import com.agora.app.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class ChannelInfoController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/agora/channel/mapper")
    public ChannelResponse getAgoraChannelInfo(@RequestParam(name = "channelName") String channelName,
            @RequestParam(name = "userAccount", required = false) String userId) {
        Set<String> channelInfo = redisService.getAgoraChannelInfo(channelName);
        Map<String, String> resultMap = new HashMap<>();
        if (!channelInfo.isEmpty()) {
            channelInfo.forEach(uid -> {
                resultMap.put(uid, redisService.getUidMapper(uid));
            });
        }
        ChannelResponse response = new ChannelResponse();
        response.setChannelName(channelName);
        response.setResult(resultMap);
        return response;
    }

    @GetMapping("/app/chat/agora/channel/mapper")
    public ChannelResponse getAgoraChannelInfoV1(@RequestParam(name = "channelName") String channelName,
                                               @RequestParam(name = "userAccount", required = false) String userId) {
        Set<String> channelInfo = redisService.getAgoraChannelInfo(channelName);
        Map<String, String> resultMap = new HashMap<>();
        if (!channelInfo.isEmpty()) {
            channelInfo.forEach(uid -> {
                resultMap.put(uid, redisService.getUidMapper(uid));
            });
        }
        ChannelResponse response = new ChannelResponse();
        response.setChannelName(channelName);
        response.setResult(resultMap);
        return response;
    }

}
