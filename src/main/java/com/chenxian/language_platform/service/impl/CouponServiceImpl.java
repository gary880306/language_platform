package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.CouponDao;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponDao couponDao;
    @Autowired
    private UserService userService;

    @Override
    public Coupon createCoupon(Coupon coupon) {
        return couponDao.createCoupon(coupon);
    }

    @Override
    public Coupon getCouponById(Integer couponId) {
        return couponDao.getCouponById(couponId);
    }

    @Override
    public Coupon updateCoupon(Integer couponId, Coupon coupon) {
        return couponDao.updateCoupon(couponId,coupon);
    }

    @Override
    public void deleteCoupon(Integer couponId) {
        couponDao.deleteCoupon(couponId);
    }

    @Override
    public void markCouponAsDeleted(Integer couponId) {
        couponDao.markCouponAsDeleted(couponId);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponDao.getAllCoupons();
    }

    @Override
    public List<Coupon> getAllActiveCoupons() {
        return couponDao.getAllActiveCoupons();
    }

    @Override
    public List<Coupon> getAvailableActiveCoupons() {
        return couponDao.getAvailableActiveCoupons();
    }

    @Override
    public void applyCouponToCart(Integer userId , Integer couponId) {
        Cart cart = userService.findNoneCheckoutCartByUserId(userId);
        cart.setCouponId(couponId);
        userService.updateCartCoupon(cart.getCouponId(),cart.getCartId());
    }

    @Override
    public boolean checkCouponExists(Integer userId, Integer couponId) {
        return couponDao.checkCouponExists(userId,couponId);
    }

    @Override
    public boolean checkCouponExistsByIsUsed(Integer userId, Integer couponId) {
        return couponDao.checkCouponExistsByIsUsed(userId,couponId);
    }

    @Override
    public List<Coupon> getCouponsByUserId(Integer userId) {
        return couponDao.getCouponsByUserId(userId);
    }

    @Override
    public void deleteUserCoupon(Integer userId, Integer couponId) {
        couponDao.deleteUserCoupon(userId,couponId);
    }

    @Override
    public void assignCouponsToUsers(List<Integer> userIds, List<Integer> couponIds) {
        couponDao.assignCouponsToUsers(userIds,couponIds);
    }

}
