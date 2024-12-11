package com.easemob.agora.api;

import com.easemob.agora.model.ResponseParam;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping("/token")
    public ResponseEntity getToken(@RequestParam(name = "userAccount") String userAccount,
            @RequestParam(name = "channelName") String channelName,
            @RequestParam(name = "publisherRole", required = false) Boolean publisherRole) {

        TokenInfo token = tokenProvider.includeUserAndRtcPrivileges(userAccount, channelName, publisherRole);

        ResponseParam responseParam = new ResponseParam();
        responseParam.setAccessToken(token.getToken());
        responseParam.setExpireTimestamp(token.getExpireTimestamp());
        responseParam.setChatUserName(token.getChatUserName());
        responseParam.setAgoraUid(token.getAgoraUid());

        return ResponseEntity.ok(responseParam);
    }

}
