package com.chenxian.language_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.context.config.annotation.EnableContextInstanceData;

@SpringBootApplication
public class LanguagePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LanguagePlatformApplication.class, args);
    }

}