package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {
    List<User> findALlUsers();
    List<User> findAllNonAdminUsers();
    Integer createUser(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);
    User getUserIncludeActiveById(Integer userId);
    User getUserByEmail(String email);
    void updateUserInfo(User user);
    boolean isCourseInUserCart(Integer userId,Integer courseId);
    // 根據使用者ID來查找其未結帳的購物車資料(單筆)
    Cart findNoneCheckoutCartByUserId(Integer userId);
    CartItem findCartItemById(Integer itemId);
    List<CartItem> findCartItemsById(Integer cartId);
    Boolean removeCartItemById(Integer itemId);
    Cart findCartById(Integer cartId);
    CheckoutResponse checkoutCartByUserId(Integer userId, Integer cartId, Integer discount);
    Order createOrder(Integer userId,List<CartItem> cartItems, Integer discount);
    void createOrderItems(Integer userId,Integer orderId, List<CartItem> cartItems);
    void createUserCourse(Integer userId,List<CartItem> cartItems );

    // 新增購物車資料
    void addCart(Cart cart);

    void addCartItem(CartItem cartItem);

    // ECPay 付款流程：建立 PENDING 訂單（不建立 user_course，不標記購物車已結帳）
    Order createPendingOrderFromCart(Integer userId, Integer cartId, Integer discount);

    // ECPay 付款確認後：建立 user_course + 標記購物車已結帳
    void confirmOrderPayment(Integer orderId, Integer cartId);

    // 查詢用戶是否已購買某課程
    boolean hasPurchasedCourse(Integer userId, Integer courseId);

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
}
