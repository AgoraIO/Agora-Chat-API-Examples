package com.easemob.agora;

import com.easemob.agora.utils.agoratools.chat.ChatTokenBuilder2;
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

        // 3.Register Chat User and get uuid
        String agoraChatUserUuid = "xxx";

        // 4.Generate Chat User Token
        String agoraChatUserToken = builder.buildUserToken(appId, appCert, agoraChatUserUuid, expirePeriod);
        System.out.println("agoraChatUserToken : " + agoraChatUserToken);
    }
    
}
