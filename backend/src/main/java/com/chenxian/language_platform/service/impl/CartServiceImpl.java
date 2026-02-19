package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dao.CouponDao;
import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.service.CartService;
import com.chenxian.language_platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 購物車服務實作 (SRP: 只處理購物車業務邏輯)
 * (DIP: 依賴 UserDao、CouponDao、CourseService 介面)
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private CourseService courseService;

    @Override
    public Cart getCartByUserId(Integer userId) {
        return userDao.findNoneCheckoutCartByUserId(userId);
    }

    @Override
    public List<CartItem> getCartItemsByCartId(Integer cartId) {
        return userDao.findCartItemsById(cartId);
    }

    @Override
    public boolean hasPurchasedCourse(Integer userId, Integer courseId) {
        return courseService.hasUserPurchasedCourse(userId, courseId);
    }

    @Override
    public void addToCart(Integer userId, Integer courseId) {
        // 確保購物車存在
        Cart cart = userDao.findNoneCheckoutCartByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setCheckout(false);
            userDao.addCart(cart);
            cart = userDao.findNoneCheckoutCartByUserId(userId);
        }
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getCartId());
        cartItem.setCourseId(courseId);
        userDao.addCartItem(cartItem);
    }

    @Override
    public void removeFromCart(Integer userId, Integer courseId) {
        Cart cart = userDao.findNoneCheckoutCartByUserId(userId);
        if (cart == null) return;
        List<CartItem> items = userDao.findCartItemsById(cart.getCartId());
        items.stream()
                .filter(item -> item.getCourseId() == courseId)
                .findFirst()
                .ifPresent(item -> userDao.removeCartItemById(item.getItemId()));
    }

    @Override
    public Coupon validateCoupon(Integer userId, String couponCode) {
        // 1. 依代碼查找優惠券
        Coupon coupon = couponDao.findCouponByCode(couponCode);
        if (coupon == null || !coupon.isActive()) {
            return null;
        }
        // 2. 確認用戶持有此優惠券且尚未使用
        if (!couponDao.checkCouponExistsByIsUsed(userId, coupon.getCouponId())) {
            return null;
        }
        return coupon;
    }

    @Override
    public CheckoutResponse checkout(Integer userId, Integer couponId) {
        Cart cart = userDao.findNoneCheckoutCartByUserId(userId);
        if (cart == null) return null;

        Integer discount = 0;
        if (couponId != null) {
            Coupon coupon = couponDao.getCouponById(couponId);
            if (coupon != null && coupon.isActive()) {
                discount = coupon.getDiscountValue() != null
                        ? coupon.getDiscountValue().intValue() : 0;
                userDao.markCouponAsUsed(userId, couponId);
                userDao.decrementCouponQuantity(couponId);
            }
        }

        CheckoutResponse response = userDao.checkoutCartByUserId(userId, cart.getCartId(), discount);
        return response;
    }
}
