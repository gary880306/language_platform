package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;

import java.util.List;

public interface UserService {
    Integer register(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);
    User getUserByEmail(String email);
    Cart findNoneCheckoutCartByUserId(Integer userId);
    CartItem findCartItemById(Integer itemId);
    List<CartItem> findCartItemsById(Integer cartId);
    Boolean removeCartItemById(Integer itemId);
    Cart findCartById(Integer cartId);
    Boolean checkoutCartByUserId(Integer userId,Integer cartId);
    Order createOrder(Integer userId, List<CartItem> cartItems);
    void createOrderItems(Integer userId,Integer orderId, List<CartItem> cartItems);
    void addCart(Cart cart);
    void addCartItem(CartItem cartItem);
}
