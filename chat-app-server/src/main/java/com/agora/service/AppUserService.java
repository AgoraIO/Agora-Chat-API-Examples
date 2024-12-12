package com.agora.service;

import com.agora.model.AppUser;
import com.agora.model.TokenInfo;

public interface AppUserService {

    /**
     * Register an account for the user in the app
     *
     * @param appUser appUser
     * @return void
     */
    void registerUser(AppUser appUser);

    /**
     * The user login in on the app and get a user token
     *
     * @param appUser appUser
     * @return TokenInfo
     */
    TokenInfo loginUser(AppUser appUser);
}
