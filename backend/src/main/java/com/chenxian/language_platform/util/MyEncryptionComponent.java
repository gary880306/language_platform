package com.chenxian.language_platform.util;

import com.chenxian.language_platform.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * AES 加密服務實作 (DIP: 實作 EncryptionService 介面)
 */
@Component
public class MyEncryptionComponent implements EncryptionService {

    private final SecretKey secretKey;

    @Autowired
    public MyEncryptionComponent(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    // 使用注入的密钥进行加密操作
    public String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 使用注入的密钥进行解密操作
    public String decrypt(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

}
