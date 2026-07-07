package com.agora.app.api;

import com.agora.app.model.*;
import com.agora.app.service.AppUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Value("${application.appkey}")
    private String defaultAppKey;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/app/chat/user/register")
    public ResponseEntity registerWithChatUser(@RequestBody @Valid AppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        this.appUserService.registerWithChatUser(appUser);

        responseParam.setCode(ResCode.RES_OK);
        responseParam.setStatusCode(ResCode.RES_OK.getCode());
        return ResponseEntity.ok(responseParam);
    }

    @PostMapping("/app/chat/user/login")
    public ResponseEntity loginWithChatUser(@RequestBody @Valid AppUser appUser) {
        ResponseParam responseParam = new ResponseParam();

        UserLoginResponse loginResponse = appUserService.loginWithChatUser(appUser);

        responseParam.setAccessToken(loginResponse.getToken());
        responseParam.setToken(loginResponse.getToken());
        responseParam.setExpireTimestamp(loginResponse.getExpireTimestamp());
        responseParam.setChatUserName(appUser.getUserAccount());
        responseParam.setAgoraUid(loginResponse.getAgoraUid());
        responseParam.setAvatarUrl(loginResponse.getAvatarUrl());
        return ResponseEntity.ok(responseParam);
    }

    @PostMapping(value = "/app/chat/user/{userAccount}/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity appUserAvatarUpload(@PathVariable("userAccount") String userAccount, MultipartFile file) {

        if (StringUtils.isBlank(userAccount)) {
            throw new IllegalArgumentException("User account cannot be empty.");
        }

        if (file == null) {
            throw new IllegalArgumentException("File must be provided.");
        }

        ResponseParam responseParam = new ResponseParam();
        String url = appUserService.uploadAvatar(defaultAppKey, userAccount, file);
        responseParam.setAvatarUrl(url);

        return ResponseEntity.ok(responseParam);
    }

}
