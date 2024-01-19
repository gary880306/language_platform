package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.model.Coupon;

import java.util.List;

public interface CouponDao {
    // 創建優惠券
    Coupon createCoupon(Coupon coupon);
    // 取得優惠券
    Coupon getCouponById(Integer couponId);
    // 修改優惠券
    Coupon updateCoupon(Integer couponId, Coupon coupon);
    // 刪除優惠券
    void deleteCoupon(Integer couponId);
    // 軟刪除優惠券
    void markCouponAsDeleted(Integer couponId);
    // 取得所有優惠券
    List<Coupon> getAllCoupons();
    List<Coupon> getAllActiveCoupons();
    boolean checkCouponExists(Integer userId, Integer couponId);
    boolean checkCouponExistsByIsUsed(Integer userId, Integer couponId);
    List<Coupon> getCouponsByUserId(Integer userId);
    void deleteUserCoupon(Integer userId, Integer couponId);

}
