package com.chenxian.language_platform.util;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordLink(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("EnjoyLearing@google.com");
        message.setTo(toEmail);
        message.setSubject("重置密碼");
        message.setText("請點擊以下鏈接重置您的密碼: " + resetLink);
        mailSender.send(message);
    }
}
