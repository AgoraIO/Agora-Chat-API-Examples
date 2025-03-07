package com.agora.app.service.impl;

import com.agora.app.exception.ASDuplicateUniquePropertyExistsException;
import com.agora.app.model.AppUserInfo;
import com.agora.app.infra.repository.AppUserInfoRepository;
import com.agora.app.service.AssemblyService;
import com.agora.app.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AssemblyServiceImpl implements AssemblyService {

    @Autowired
    private AppUserInfoRepository appUserInfoRepository;

    @Override
    public String generateUniqueAgoraUid(String appkey) {
        String uid = RandomUidUtil.getUid();
        while (true) {
            if (checkIfAgoraUidExistsDB(appkey, uid)) {
                uid = RandomUidUtil.getUid();
            } else {
                break;
            }
        }
        return uid;
    }

    @Override
    public AppUserInfo getAppUserInfoFromDB(String appkey, String userAccount) {
        return this.appUserInfoRepository.findByAppKeyAndUserAccount(appkey, userAccount);
    }

    @Override
    public boolean checkIfUserAccountExistsDB(String appkey, String userAccount) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByAppKeyAndUserAccount(appkey, userAccount);
        return appUserInfo != null;
    }

    @Override
    public boolean checkIfAgoraUidExistsDB(String appkey, String agoraUid) {
        AppUserInfo appUserInfo = this.appUserInfoRepository.findByAgoraUid(appkey, agoraUid);
        return appUserInfo != null;
    }

    @Override
    public void saveAppUserToDB(String appkey, String userAccount, String userNickname,
            String userPassword, String chatUserName, String agoraUid, String avatarUrl) {
        AppUserInfo appUserInfo = new AppUserInfo();
        appUserInfo.setAppkey(appkey);
        appUserInfo.setUserAccount(userAccount);
        appUserInfo.setUserNickname(userNickname);
        appUserInfo.setUserPassword(userPassword);
        appUserInfo.setChatUserName(chatUserName);
        appUserInfo.setAgoraUid(agoraUid);
        if (Strings.isNotBlank(avatarUrl)) {
            appUserInfo.setAvatarUrl(avatarUrl);
        }
        appUserInfo.setCreatedAt(LocalDateTime.now());

        try {
            this.appUserInfoRepository.save(appUserInfo);
        } catch (Exception e) {
            log.error("save appUserInfo to db failed : {}", e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new ASDuplicateUniquePropertyExistsException("userAccount " + userAccount + " already exists");
            }
        }

        log.info("userAccount info save to db successfully :{}", userAccount);
    }

    @Override public void updateAppUserInfoToDB(AppUserInfo appUserInfo) {
        this.appUserInfoRepository.save(appUserInfo);
        log.info("userAccount avatar url update to db successfully :{}", appUserInfo);
    }
}
