package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASDuplicateUniquePropertyExistsException;
import com.easemob.agora.exception.ASNotFoundException;
import com.easemob.agora.exception.ASPasswordErrorException;
import com.easemob.agora.model.AppUser;
import com.easemob.agora.model.AppUserInfo;
import com.easemob.agora.model.TokenInfo;
import com.easemob.agora.service.AppUserService;
import com.easemob.agora.service.AssemblyService;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.agora.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AssemblyService assemblyService;

    @Override
    public void registerUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();

        if (this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            throw new ASDuplicateUniquePropertyExistsException("userAccount " + userAccount + " already exists");
        }

        this.assemblyService.registerUserAccount(appUser.getUserAccount(), appUser.getUserPassword());
    }

    @Override
    public TokenInfo loginUser(AppUser appUser) {
        String userAccount = appUser.getUserAccount();

        if (this.assemblyService.checkIfUserAccountExistsDB(userAccount)) {
            AppUserInfo userInfo = this.assemblyService.getAppUserInfoFromDB(userAccount);

            if (!appUser.getUserPassword().equals(userInfo.getUserPassword())) {
                throw new ASPasswordErrorException("user password error");
            }
        } else {
            throw new ASNotFoundException(userAccount + " does not exists");
        }

        return this.tokenService.getUserTokenWithAccount(userAccount);
    }
}
