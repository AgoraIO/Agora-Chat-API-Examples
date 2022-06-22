package com.easemob.agora.config;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {
//    private String baseUri;
    private String appkey;
    private String agoraCert;
    private String agoraAppId;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public EMService service() {
        if (StringUtils.isEmpty(this.appkey) || StringUtils.isEmpty(this.agoraCert) || StringUtils.isEmpty(this.agoraAppId)) {
            throw new IllegalArgumentException("appkey or agoraCert or agoraAppId cannot be empty!");
        }

        EMProperties properties = EMProperties.builder()
                .setRealm(EMProperties.Realm.AGORA_REALM)
                .setAppkey(this.appkey)
                .setAppId(this.agoraAppId)
                .setAppCert(this.agoraCert)
                .turnOffUserNameValidation()
                .build();

        return new EMService(properties);
    }
}
