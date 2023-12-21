package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.User;

public interface UserService {
    Integer register(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);

    User getUserByEmail(String email);
    Cart findNoneCheckoutCartByUserId(Integer userId);
    void addCart(Cart cart);
    void addCartItem(CartItem cartItem);
}
