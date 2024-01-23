package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.CouponDao;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponDao couponDao;
    @Autowired
    private UserService userService;

    // 創建優惠券
    @Override
    public Coupon createCoupon(Coupon coupon) {
        return couponDao.createCoupon(coupon);
    }

    // 取得優惠券
    @Override
    public Coupon getCouponById(Integer couponId) {
        return couponDao.getCouponById(couponId);
    }

    // 修改優惠券
    @Override
    public Coupon updateCoupon(Integer couponId, Coupon coupon) {
        return couponDao.updateCoupon(couponId,coupon);
    }

    // 刪除優惠券
    @Override
    public void deleteCoupon(Integer couponId) {
        couponDao.deleteCoupon(couponId);
    }

    // 軟刪除優惠券
    @Override
    public void markCouponAsDeleted(Integer couponId) {
        couponDao.markCouponAsDeleted(couponId);
    }

    // 取得所有優惠券
    @Override
    public List<Coupon> getAllCoupons() {
        return couponDao.getAllCoupons();
    }

    // 取得所有未被軟刪除的優惠券
    @Override
    public List<Coupon> getAllActiveCoupons() {
        return couponDao.getAllActiveCoupons();
    }

    // 取得所有上架/未被軟刪除/未過期的優惠券
    @Override
    public List<Coupon> getAvailableActiveCoupons() {
        // 從 DAO 獲取可用的活動優惠券
        List<Coupon> availableCoupons = couponDao.getAvailableActiveCoupons();
        Date now = new Date();
        // 對每個優惠券進行日期調整並過濾掉已過期的優惠券
        return availableCoupons.stream()
                .map(this::adjustCouponDates)
                .filter(coupon -> coupon.getEndDate() == null || coupon.getEndDate().after(now))
                .collect(Collectors.toList());
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

    @Override
    public boolean isCodeExists(String code) {
        return couponDao.isCodeExists(code);
    }

    // 調整優惠券的開始日期和結束日期
    private Coupon adjustCouponDates(Coupon coupon) {
        // 調整開始日期為當天的開始時刻
        if (coupon.getStartDate() != null) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(coupon.getStartDate());
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            coupon.setStartDate(startCal.getTime());
        }

        // 調整結束日期為當天的最後一刻
        if (coupon.getEndDate() != null) {
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(coupon.getEndDate());
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            coupon.setEndDate(endCal.getTime());
        }

        return coupon;
    }
}
