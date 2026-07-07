package com.agora.app.model;

import lombok.Data;

@Data
public class TokenInfo {
    private String token;

    private Long expireTimestamp;

    private String agoraUid;
}
