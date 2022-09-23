package com.easemob.agora.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class TokenInfo {
    private String token;

    private Long expireTimestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String chatUserName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String agoraUid;

}
