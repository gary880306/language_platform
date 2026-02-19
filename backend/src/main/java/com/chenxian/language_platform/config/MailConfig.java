package com.chenxian.language_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 配置郵件服務器相關信息
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // 根據郵件服務器設定
        mailSender.setUsername("ch66971542@gmail.com");
        mailSender.setPassword("uohb byvd khss zxrr");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true"); // 啟用TLS
        props.put("mail.smtp.auth", "true"); // 啟用驗證
        return mailSender;
    }
}
