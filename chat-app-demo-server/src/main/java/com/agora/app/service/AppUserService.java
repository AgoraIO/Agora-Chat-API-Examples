package com.agora.app.service;

import com.agora.app.model.AppUser;
import com.agora.app.model.TokenInfo;
import com.agora.app.model.UserLoginResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AppUserService {

    /**
     * 在应用中为用户注册一个账号（此账号是与chat username一致）
     * @param appUser appUser
     * @return 注册成功或失败
     */
    void registerWithChatUser(AppUser appUser);

    /**
     * 用户在应用上登录并获取一个token（此账号是与chat username一致）
     * @param appUser appUser 用户名与密码
     * @return token信息
     */
    UserLoginResponse loginWithChatUser(AppUser appUser);

    /**
     * 上传用户头像
     *
     * @param appkey appkey
     * @param userAccount userAccount
     * @param file file
     * @return 头像 url
     */
    String uploadAvatar(String appkey, String userAccount, MultipartFile file);

}
