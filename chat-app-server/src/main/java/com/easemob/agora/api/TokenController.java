package com.easemob.agora.api;

import com.easemob.agora.model.ResCode;
import com.easemob.agora.model.ResponseParam;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/agora/rtc/token")
    public ResponseEntity getAgoraRtcToken(@RequestParam String channelName,
                                          @RequestParam Integer agoraUid) {
        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(channelName) && agoraUid != null) {
            TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());

            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR);
            responseParam.setErrorInfo("channelName or agoraUid is not null");

            return ResponseEntity.badRequest().body(responseParam);
        }
    }
}
