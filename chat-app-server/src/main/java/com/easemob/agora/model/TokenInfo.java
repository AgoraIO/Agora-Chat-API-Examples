package com.easemob.agora.model;

import lombok.Data;

@Data
public class TokenInfo {
    private String token;
    private Long expireTimestamp;
    private String chatUserName;
    private String agoraUid;
}
