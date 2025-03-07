package com.agora.app.api;


import com.agora.app.model.ResCode;
import com.agora.app.model.ResponseParam;
import com.agora.app.model.TokenInfo;
import com.agora.app.service.RedisService;
import com.agora.app.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token/rtc/channel/{channelName}/agorauid/{agoraUid}")
    public ResponseEntity getAgoraRtcToken(@PathVariable String channelName,
            @PathVariable Integer agoraUid,
            @RequestParam("userAccount") String userAccount) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(channelName) && agoraUid != null) {
            TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());

            redisService.saveAgoraChannelInfo(false, channelName, String.valueOf(agoraUid));
            redisService.saveUidMapper(String.valueOf(agoraUid), userAccount);

            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR);
            responseParam.setStatusCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or agoraUid is not null");

            return ResponseEntity.badRequest().body(responseParam);
        }
    }

    @GetMapping("/app/chat/token/rtc/channel/{channelName}/agorauid/{agoraUid}")
    public ResponseEntity getAgoraRtcTokenV1(@PathVariable String channelName,
            @PathVariable Integer agoraUid,
            @RequestParam("userAccount") String userAccount) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(channelName) && agoraUid != null) {
            TokenInfo token = tokenService.getRtcToken(channelName, agoraUid);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());

            redisService.saveAgoraChannelInfo(false, channelName, String.valueOf(agoraUid));
            redisService.saveUidMapper(String.valueOf(agoraUid), userAccount);

            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR);
            responseParam.setStatusCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or agoraUid is not null");

            return ResponseEntity.badRequest().body(responseParam);
        }
    }

    @GetMapping("/app/chat/token/rtc/channel/{channelName}")
    public ResponseEntity getAgoraRtcToken(@PathVariable String channelName,
            @RequestParam("userAccount") String userAccount) {

        ResponseParam responseParam = new ResponseParam();
        if (StringUtils.isNotBlank(channelName) && userAccount != null) {
            TokenInfo token = tokenService.getRtcToken(channelName, userAccount);
            responseParam.setAccessToken(token.getToken());
            responseParam.setExpireTimestamp(token.getExpireTimestamp());
            responseParam.setAgoraUid(token.getAgoraUid());

            redisService.saveAgoraChannelInfo(false, channelName, token.getAgoraUid());
            redisService.saveUidMapper(token.getAgoraUid(), userAccount);

            return ResponseEntity.ok(responseParam);
        } else {
            responseParam.setCode(ResCode.RES_REQUEST_PARAM_ERROR);
            responseParam.setStatusCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());
            responseParam.setErrorInfo("channelName or userAccount is not null");

            return ResponseEntity.badRequest().body(responseParam);
        }
    }
}
