package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public Cart findNoneCheckoutCartByUserId(Integer userId) {
        return userDao.findNoneCheckoutCartByUserId(userId);
    }

    @Override
    public void addCart(Cart cart) {
        userDao.addCart(cart);
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        userDao.addCartItem(cartItem);
    }


}
