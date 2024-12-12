package com.agora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EntityScan("com.agora.model")
@SpringBootApplication(scanBasePackages = "com.agora")
public class AgoraServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgoraServerApplication.class, args);
    }
}
