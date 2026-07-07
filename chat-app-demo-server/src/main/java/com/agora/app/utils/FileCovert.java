package com.agora.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
public class FileCovert {

    public static File convertMultipartFileToFile(String appkey, String userId, MultipartFile multipartFile) {
        try {
            File file = File.createTempFile(multipartFile.getOriginalFilename(), null);
            FileCopyUtils.copy(multipartFile.getBytes(), file);
            return file;
        } catch (Exception e) {
            log.error("convert multipart file to file fail. appkey : {}, userId : {}, error : {}", appkey, userId, e.getMessage());
            throw new IllegalArgumentException("Convert multipart file to chat file error.");
        }
    }
}
