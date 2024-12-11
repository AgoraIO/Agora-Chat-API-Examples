package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.agora.exception.ASNotFoundException;
import com.easemob.agora.exception.ASPasswordErrorException;
import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.AppUserInfoRepository;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AgoraChatService;
import com.easemob.agora.service.AppUserService;
import com.easemob.agora.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AgoraChatService agoraChatService;

    @Autowired
    private AppUserInfoRepository appUserInfoRepository;

    @Override
    public void registerUser(AppUser appUser) {
        // 1.Get user account
        String userAccount = appUser.getUserAccount();

        // 2.Check if user account exists DB
        if (checkIfUserAccountExistsDB(userAccount)) {
            throw new ASDuplicateUniquePropertyExistsException("userAccount " + userAccount + " already exists.");
        }

        // 3.Save user account and Agora Chat user name to DB
        // The Agora Chat user name used here is the same as the user account name, it is not recommended to do this in actual use
        saveAppUserToDB(userAccount, appUser.getUserPassword(), userAccount);
    }

    @Override
    public TokenInfo loginUser(AppUser appUser) {
        // 1.Get user account
        String userAccount = appUser.getUserAccount();
        AppUserInfo appUserInfo;

        // 2.Check if user account exists DB
        if (checkIfUserAccountExistsDB(userAccount)) {

            // 3.Get app user information from db
            appUserInfo = getAppUserInfoFromDB(userAccount);

            // 4.Check whether the login user password is correct
            if (!appUser.getUserPassword().equals(appUserInfo.getUserPassword())) {
                throw new ASPasswordErrorException("User password error.");
            }
        } else {
            throw new ASNotFoundException(userAccount + " does not exists");
        }

        // 5.Get AgoraChat user token for user account
        return this.agoraChatService.getAgoraChatUserTokenWithAccount(appUserInfo);
    }

    public AppUserInfo getAppUserInfoFromDB(String userAccount) {
        return this.appUserInfoRepository.findByUserAccount(userAccount);
    }

    public boolean checkIfUserAccountExistsDB(String userAccount) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByUserAccount(userAccount);
        return appUserInfo != null;
    }

    public void saveAppUserToDB(String userAccount, String userPassword, String agoraChatUserName) {
        AppUserInfo appUserInfo = new AppUserInfo();
        appUserInfo.setUserAccount(userAccount);
        appUserInfo.setUserPassword(userPassword);
        appUserInfo.setAgoraChatUserName(agoraChatUserName);

        this.appUserInfoRepository.save(appUserInfo);

        log.info("UserAccount info save to db successfully : {}", userAccount);
    }
}
