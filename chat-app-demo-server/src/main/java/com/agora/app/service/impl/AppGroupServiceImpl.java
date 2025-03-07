package com.agora.app.service.impl;

import com.agora.app.model.AppUserInfo;
import com.agora.app.service.AppGroupService;
import com.agora.app.service.AssemblyService;
import com.agora.app.service.RestService;
import com.agora.app.utils.GenerateGroupAvatarUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AppGroupServiceImpl implements AppGroupService {

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Override public String getAvatarUrl(String appKey, String groupId) {
        String chatGroupCustom = restService.getChatGroupCustom(appKey, groupId);
        if (StringUtils.isNotBlank(chatGroupCustom)) {
            return chatGroupCustom;
        }

        List<String> groupMembers = restService.getChatGroupMembers(appKey, groupId);
        if (groupMembers != null && groupMembers.size() > 0) {
            List<String> groupMemberAvatarUrlList = new ArrayList<>();
            groupMembers.forEach(chatUserName -> {
                AppUserInfo appUserInfo = assemblyService.getAppUserInfoFromDB(appKey, chatUserName);
                if (appUserInfo == null) {
                    // 传自己默认头像 url
                    groupMemberAvatarUrlList.add("https://a41.chat.agora.io/default_avatar");
                } else {
                    String avatarUrl = appUserInfo.getAvatarUrl();
                    if (avatarUrl == null) {
                        // 传自己默认头像 url
                        groupMemberAvatarUrlList.add("https://a41.chat.agora.io/default_avatar");
                    } else {
                        groupMemberAvatarUrlList.add(avatarUrl);
                    }
                }
            });

            BufferedImage outImage;
            try {
                outImage = GenerateGroupAvatarUtil.getCombinationOfHead(restService, appKey, groupId, groupMemberAvatarUrlList);
            } catch (Exception e) {
                log.error("Generate group avatar failed, appKey: {}, groupId: {}", appKey, groupId, e);
                throw new IllegalArgumentException("Generate group avatar failed.");
            }

            String groupAvatarUrl;
            File tempFile;
            try {
                // 创建临时文件
                tempFile = File.createTempFile("tempImage", ".jpg");

                // 将BufferedImage对象写入临时文件
                ImageIO.write(outImage, "jpg", tempFile);
            } catch (IOException e) {
                log.error("Create temp file failed, appKey: {}, groupId: {}", appKey, groupId, e);
                throw new IllegalArgumentException("Upload group avatar failed.");
            }

            groupAvatarUrl = restService.uploadFile(appKey, groupId, tempFile);
            tempFile.delete();

            restService.updateGroupCustom(appKey, groupId, groupAvatarUrl);

            return groupAvatarUrl;
        } else {
            log.error("Group members is empty, appKey: {}, groupId: {}", appKey, groupId);
            throw new IllegalArgumentException("Group members is empty.");
        }
    }

}
