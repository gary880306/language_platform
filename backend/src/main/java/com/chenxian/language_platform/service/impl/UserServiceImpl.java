package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.repository.UserRepository;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用戶個人資料服務實作 (SRP: 只處理用戶個人資料管理)
 * (DIP: 依賴 UserDao 介面，而非具體實作)
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public List<User> findAllNonAdminUsers() {
        return userDao.findAllNonAdminUsers();
    }

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public User getUserIncludeActiveById(Integer userId) {
        return userDao.getUserIncludeActiveById(userId);
    }

    @Override
    public User updateUser(User user) {
        User existing = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + user.getUserId()));
        existing.setUserName(user.getUserName());
        existing.setEmail(user.getEmail());
        existing.setPhoneNumber(user.getPhoneNumber());
        existing.setAddress(user.getAddress());
        existing.setBirth(user.getBirth());
        return userRepository.save(existing);
    }

    @Override
    public boolean updateUserStatus(Integer userId, boolean isActive) {
        User user = userDao.getUserIncludeActiveById(userId);
        if (user == null) return false;
        user.setActive(isActive);
        userDao.updateUserInfo(user);
        return true;
    }
}
