package com.chenxian.language_platform.util;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendResetPasswordLink(String toEmail, String resetLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom("ch66971542@gmail.com", "响學");
        helper.setTo(toEmail);
        helper.setSubject("重置密碼確認信");
        helper.setText("請點擊以下鏈接重置您的密碼: " + resetLink);

        javaMailSender.send(mimeMessage);
    }
}
