package com.agora.app.service;

import com.alibaba.fastjson.JSONObject;

public interface CallBackService {

    void receiveCallBackMessage(String appKey, String from, String to, String chatType,
            String groupId, JSONObject callBackPayload);

}
