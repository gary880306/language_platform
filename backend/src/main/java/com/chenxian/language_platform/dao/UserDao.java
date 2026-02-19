package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;

import java.util.List;

/**
 * 用戶資料存取介面 (SRP: 統一管理用戶相關資料存取)
 * (LSP: 修正方法命名 findALlUsers → findAllUsers)
 */
public interface UserDao {
    List<User> findAllUsers();
    List<User> findAllNonAdminUsers();
    Integer createUser(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);
    User getUserIncludeActiveById(Integer userId);
    User getUserByEmail(String email);
    void updateUserInfo(User user);

    // ---- 購物車 ----
    boolean isCourseInUserCart(Integer userId, Integer courseId);
    Cart findNoneCheckoutCartByUserId(Integer userId);
    CartItem findCartItemById(Integer itemId);
    List<CartItem> findCartItemsById(Integer cartId);
    Boolean removeCartItemById(Integer itemId);
    Cart findCartById(Integer cartId);
    void addCart(Cart cart);
    void addCartItem(CartItem cartItem);
    Integer getCartCourseCount(Integer userId);

    // ---- 結帳 / 訂單 ----
    CheckoutResponse checkoutCartByUserId(Integer userId, Integer cartId, Integer discount);
    Order createOrder(Integer userId, List<CartItem> cartItems, Integer discount);
    void createOrderItems(Integer userId, Integer orderId, List<CartItem> cartItems);
    void createUserCourse(Integer userId, List<CartItem> cartItems);

    // ---- 優惠券 ----
    void addUserCoupon(Integer userId, Integer couponId);
    void updateUserCoupon(Integer userId, Integer couponId);
    List<UserCoupon> findUserCouponsByUserId(Integer userId);
    void decrementCouponQuantity(Integer couponId);
    void updateCartCoupon(Integer couponId, Integer cartId);
    void markCouponAsUsed(Integer userId, Integer couponId);
    List<UserCoupon> findUnusedUserCouponsByUserId(Integer userId);
    Integer getCurrentCouponIdByUserId(Integer userId);
    void cancelCoupon(Integer userId);
}
