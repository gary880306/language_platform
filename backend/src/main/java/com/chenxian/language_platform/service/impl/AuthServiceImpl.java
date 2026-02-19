package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.repository.UserRepository;
import com.chenxian.language_platform.service.AuthService;
import com.chenxian.language_platform.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * 認證服務實作 (SRP: 只處理認證邏輯)
 * (DIP: 依賴 EncryptionService 介面，而非具體實作)
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public String generateResetToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenCreationTime(Instant.now());
        userRepository.save(user);
        return token;
    }

    @Override
    public boolean resetPasswordWithToken(String token, String newPassword) throws Exception {
        if (!checkTokenValidity(token)) {
            return false;
        }
        User user = userRepository.findByResetToken(token);
        String encodedNewPassword = encryptionService.encrypt(newPassword);
        user.setPassword(encodedNewPassword);
        user.setResetToken(null);
        user.setTokenCreationTime(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean checkTokenValidity(String token) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getTokenCreationTime() == null) {
            return false;
        }
        Instant expirationTime = user.getTokenCreationTime().plus(60, ChronoUnit.SECONDS);
        return Instant.now().isBefore(expirationTime);
    }
}
