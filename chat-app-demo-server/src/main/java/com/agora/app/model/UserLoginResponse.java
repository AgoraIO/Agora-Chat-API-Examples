package com.agora.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class UserLoginResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;

    private String avatarUrl;

    private String token;

    private Long expireTimestamp;

    private String agoraUid;
}
