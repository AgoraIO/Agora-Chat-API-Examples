package com.agora.app.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigProperties {

    private Property channel;

    @Data
    public static class Property {

        //configuration
        private String type = "standalone";
        private String master = "mymaster";
        private String nodes;
        private String password = "default_password";
        private Boolean ssl = false;
        private String connectionFactory = "lettuce";

        private Integer maxRedirects = 5;
        private Integer maxAttempts = 5;
        private Integer soTimeout = 5000;

        //pool
        private Integer maxIdle;
        private Integer minIdle;
        private Integer maxTotal = 100;
        private Long maxWait = 5000L;
        private Long readTimeout = 60000L;
        private Long connectTimeout = 2000L;

        private Boolean testOnBorrow;
        private Boolean testOnCreate;
        private Boolean testOnReturn;

        private Long timeBetweenEvictionRuns;
        private Long minEvictableIdleTimeMillis;

    }
}
