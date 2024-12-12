package com.agora;

import io.agora.chat.ChatTokenBuilder2;
import org.junit.Test;

public class AgoraTokenTest {

    @Test
    public void testGenerateToken() {
        String appId = "xxx";
        String appCert = "xxx";
        int expirePeriod = 86400;

        ChatTokenBuilder2 builder = new ChatTokenBuilder2();

        // 1. Generate Agora App Token
        String agoraAppToken = builder.buildAppToken(appId, appCert, expirePeriod);
        System.out.println("agoraAppToken : " + agoraAppToken);

        // 2.Get a Chat App token with Agora App Token
        String chatAppToken = "";

        // 3.Generate Chat User Token
        String agoraChatUsername = "xxx";
        String agoraChatUserToken = builder.buildUserToken(appId, appCert, agoraChatUsername, expirePeriod);
        System.out.println("agoraChatUserToken : " + agoraChatUserToken);
    }
    
}
