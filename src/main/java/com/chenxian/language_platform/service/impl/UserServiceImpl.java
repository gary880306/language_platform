package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

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
    public CartItem findCartItemById(Integer itemId) {
        return userDao.findCartItemById(itemId);
    }

    @Override
    public List<CartItem> findCartItemsById(Integer cartId) {
        return userDao.findCartItemsById(cartId);
    }

    @Override
    public Boolean removeCartItemById(Integer itemId) {
        return userDao.removeCartItemById(itemId);
    }

    @Override
    public Cart findCartById(Integer cartId) {
        return userDao.findCartById(cartId);
    }

    @Override
    public Boolean checkoutCartByUserId(Integer userId,Integer cartId) {
        return userDao.checkoutCartByUserId(userId,cartId);
    }

    @Override
    public Order createOrder(Integer userId, List<CartItem> cartItems) {
        return userDao.createOrder(userId,cartItems);
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
