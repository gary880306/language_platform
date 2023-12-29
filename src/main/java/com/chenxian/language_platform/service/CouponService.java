package com.chenxian.language_platform.service;

import com.chenxian.language_platform.model.Coupon;

import java.util.List;

public interface CouponService {
    // 創建優惠券
    Coupon createCoupon(Coupon coupon);
    // 取得優惠券
    Coupon getCouponById(Integer couponId);
    // 修改優惠券
    Coupon updateCoupon(Integer couponId, Coupon coupon);
    // 刪除優惠券
    void deleteCoupon(Integer couponId);
    // 取得所有優惠券
    List<Coupon> getAllCoupons();
}
