package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;

import java.util.List;

public interface UserService {
    List<User> findALlUsers();
    Integer register(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);
    User getUserIncludeActiveById(Integer userId);
    User getUserByEmail(String email);
    void updateUserInfo(User user);
    boolean isCourseInUserCart(Integer userId,Integer courseId);
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
    boolean updateUserActiveStatus(Integer userId, boolean isActive);
    boolean checkCouponExists(Integer userId, Integer couponId);
    void addUserCoupon(Integer userId, Integer couponId);
    List<UserCoupon> findUserCouponsByUserId(Integer userId);
    void decrementCouponQuantity(Integer couponId);
    Integer getCartCourseCount(Integer userId);
    void updateCartCoupon(Integer couponId , Integer cartId);
}
