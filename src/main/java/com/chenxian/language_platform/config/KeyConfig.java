package com.chenxian.language_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class KeyConfig {

    @Value("${myapp.secretKey}")
    private String secretKeyString;

    @Bean
    public SecretKey secretKey() {
        // 將設定檔中的金鑰字串轉換為SecretKey對象
        byte[] keyBytes = secretKeyString.getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
