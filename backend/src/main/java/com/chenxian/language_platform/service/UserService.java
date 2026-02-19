package com.chenxian.language_platform.service;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;

import java.util.List;

public interface UserService {
    List<User> findALlUsers();
    List<User> findAllNonAdminUsers();
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
    CheckoutResponse checkoutCartByUserId(Integer userId, Integer cartId, Integer discountInteger);
    Order createOrder(Integer userId, List<CartItem> cartItems, Integer discount);
    void createOrderItems(Integer userId,Integer orderId, List<CartItem> cartItems);
    void addCart(Cart cart);
    void addCartItem(CartItem cartItem);
    boolean updateUserActiveStatus(Integer userId, boolean isActive);;
    void addUserCoupon(Integer userId, Integer couponId);
    void updateUserCoupon(Integer userId, Integer couponId);
    List<UserCoupon> findUserCouponsByUserId(Integer userId);
    void decrementCouponQuantity(Integer couponId);
    Integer getCartCourseCount(Integer userId);
    void updateCartCoupon(Integer couponId , Integer cartId);
    void markCouponAsUsed(Integer userId, Integer couponId);
    List<UserCoupon> findUnusedUserCouponsByUserId(Integer userId);
    Integer getCurrentCouponIdByUserId(Integer userId);
    void cancelCoupon(Integer userId);
    String generateResetToken(String email);
    boolean resetPasswordWithToken(String token,String newPassword) throws Exception;
    boolean checkTokenValidity(String token);
    User updateUser(User user);
}
