package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;

public interface UserDao {
    Integer createUser(UserRegisterRequest userRegisterRequest);

    User getUserById(Integer userId);
}
