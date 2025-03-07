package com.agora.app.model;

import lombok.Data;

@Data
public class ChannelResponse {
    private ResCode code = ResCode.RES_OK;
    private int statusCode = ResCode.RES_OK.getCode();
    private String channelName;
    private Object result;
}
