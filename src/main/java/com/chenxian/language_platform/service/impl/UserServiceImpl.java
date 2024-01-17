package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.repository.UserRepository;
import com.chenxian.language_platform.service.UserService;
import com.chenxian.language_platform.util.MyEncryptionComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyEncryptionComponent myEncryptionComponent;

    @Override
    public List<User> findALlUsers() {
        return userDao.findALlUsers();
    }

    @Override
    public List<User> findAllNonAdminUsers() {
        return userDao.findAllNonAdminUsers();
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        return userDao.createUser(userRegisterRequest);
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
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public void updateUserInfo(User user) {
        userDao.updateUserInfo(user);
    }

    @Override
    public boolean isCourseInUserCart(Integer userId, Integer courseId) {
        return userDao.isCourseInUserCart(userId,courseId);
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
    public CheckoutResponse checkoutCartByUserId(Integer userId, Integer cartId) {
        return userDao.checkoutCartByUserId(userId,cartId);
    }

    @Override
    public Order createOrder(Integer userId, List<CartItem> cartItems) {
        return userDao.createOrder(userId,cartItems);
    }

    @Override
    public void createOrderItems(Integer userId,Integer orderId, List<CartItem> cartItems) {
        userDao.createOrderItems(userId,orderId,cartItems);
    }

    @Override
    public void addCart(Cart cart) {
        userDao.addCart(cart);
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        userDao.addCartItem(cartItem);
    }

    @Override
    public boolean updateUserActiveStatus(Integer userId, boolean isActive) {
        User user = userDao.getUserIncludeActiveById(userId);
        user.setActive(isActive);
        userDao.updateUserInfo(user);
        return false;
    }

    @Override
    public void addUserCoupon(Integer userId, Integer couponId) {
        userDao.addUserCoupon(userId,couponId);
    }

    @Override
    public void updateUserCoupon(Integer userId, Integer couponId) {
        userDao.updateUserCoupon(userId,couponId);
    }

    @Override
    public List<UserCoupon> findUserCouponsByUserId(Integer userId) {
        return userDao.findUserCouponsByUserId(userId);
    }

    @Override
    public void decrementCouponQuantity(Integer couponId) {
        userDao.decrementCouponQuantity(couponId);
    }

    @Override
    public Integer getCartCourseCount(Integer userId) {
        return userDao.getCartCourseCount(userId);
    }

    @Override
    public void updateCartCoupon(Integer couponId, Integer cartId) {
        userDao.updateCartCoupon(couponId,cartId);
    }

    @Override
    public void markCouponAsUsed(Integer userId, Integer couponId) {
        userDao.markCouponAsUsed(userId,couponId);
    }

    @Override
    public List<UserCoupon> findUnusedUserCouponsByUserId(Integer userId) {
        return userDao.findUnusedUserCouponsByUserId(userId);
    }

    @Override
    public Integer getCurrentCouponIdByUserId(Integer userId) {
        return userDao.getCurrentCouponIdByUserId(userId);
    }

    @Override
    public void cancelCoupon(Integer userId) {
        userDao.cancelCoupon(userId);
    }

    @Override
    public String generateResetToken(String email) {
        // 從數據庫查找用戶
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // 處理用戶不存在的情況
            return null;
        }

        // 生成唯一令牌
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenCreationTime(Instant.now());

        // 更新用戶信息以保存令牌
        userRepository.save(user);

        return token;
    }

    @Override
    public boolean resetPasswordWithToken(String token,String newPassword) throws Exception {
        // 檢查該用戶令牌是否存在於數據庫中
        User user =  userRepository.findByResetToken(token);
        boolean isTokenValid = checkTokenValidity(token);
        if (isTokenValid) {
            // 将新密码加密
            String encodedNewPassword = myEncryptionComponent.encrypt(newPassword);

            // 更新用户的密码
            user.setPassword(encodedNewPassword);
            user.setResetToken(null); // 清除令牌
            user.setTokenCreationTime(null); // 清除令牌創建時間
            userRepository.save(user); // 更新用戶資訊

            return true; // 密碼重製成功
        }

        return false; // 令牌無效或用戶不存在
    }

    @Override
    public boolean checkTokenValidity(String token) {
        User user =  userRepository.findByResetToken(token);
        // 获取当前时间
        Instant currentTime = Instant.now();

        // 获取令牌的创建时间（假设令牌创建时间存储在token对象中）
        Instant tokenCreationTime = user.getTokenCreationTime();

        // 计算令牌的有效期为30秒
        Instant tokenExpirationTime = tokenCreationTime.plus(30, ChronoUnit.SECONDS);

        // 检查当前时间是否在有效期内
        boolean isValid = currentTime.isBefore(tokenExpirationTime);

        return isValid;
    }


}
