package com.agora.app.api;

import com.agora.app.model.ResponseParam;
import com.agora.app.service.CallBackService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallBackController {

    private CallBackService callBackService;

    @Value("${agora.chat.robot.enable.switch:false}")
    private Boolean isUseAIChatBot;


    public CallBackController(CallBackService callBackService) {
        this.callBackService = callBackService;
    }

    @PostMapping("/app/chat/chat-gpt/interact/callback")
    public ResponseEntity receiveCallBackMessage(@RequestBody JSONObject jsonObject) {

        String appKey = jsonObject.getString("appkey");
        String from = jsonObject.getString("from");
        String to = jsonObject.getString("to");
        String chatType = jsonObject.getString("chat_type");
        String groupId = jsonObject.getString("group_id");
        JSONObject payload = jsonObject.getJSONObject("payload");

        if (isUseAIChatBot) {
            callBackService.receiveCallBackMessage(appKey, from, to, chatType, groupId, payload);
        }

        ResponseParam responseParam = new ResponseParam();

        return ResponseEntity.ok(responseParam);
    }
}
