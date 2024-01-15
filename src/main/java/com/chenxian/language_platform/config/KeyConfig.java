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
        // 将配置文件中的密钥字符串转换为SecretKey对象
        byte[] keyBytes = secretKeyString.getBytes();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
