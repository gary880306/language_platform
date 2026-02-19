package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.CouponDao;
import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.CouponDTO;
import com.chenxian.language_platform.dto.SendCouponsRequest;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.UserCoupon;
import com.chenxian.language_platform.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 優惠券服務實作 (SRP: 只處理優惠券業務邏輯)
 * (DIP: 依賴 CouponDao 與 UserDao 介面；移除對 UserService 的循環依賴)
 */
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private UserDao userDao;

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
        return couponDao.updateCoupon(couponId, coupon);
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
        List<Coupon> availableCoupons = couponDao.getAvailableActiveCoupons();
        if (availableCoupons == null) return List.of();
        Date now = new Date();
        return availableCoupons.stream()
                .map(this::adjustCouponDates)
                .filter(coupon -> coupon.getEndDate() == null || coupon.getEndDate().after(now))
                .collect(Collectors.toList());
    }

    @Override
    public void applyCouponToCart(Integer userId, Integer couponId) {
        // (DIP: 直接透過 UserDao 操作購物車，解除對 UserService 的循環依賴)
        Cart cart = userDao.findNoneCheckoutCartByUserId(userId);
        if (cart != null) {
            userDao.updateCartCoupon(couponId, cart.getCartId());
        }
    }

    @Override
    public boolean checkCouponExists(Integer userId, Integer couponId) {
        return couponDao.checkCouponExists(userId, couponId);
    }

    @Override
    public boolean checkCouponExistsByIsUsed(Integer userId, Integer couponId) {
        return couponDao.checkCouponExistsByIsUsed(userId, couponId);
    }

    @Override
    public List<Coupon> getCouponsByUserId(Integer userId) {
        return couponDao.getCouponsByUserId(userId);
    }

    @Override
    public List<UserCoupon> getUserCoupons(Integer userId) {
        return userDao.findUserCouponsByUserId(userId);
    }

    @Override
    public void deleteUserCoupon(Integer userId, Integer couponId) {
        couponDao.deleteUserCoupon(userId, couponId);
    }

    @Override
    public void sendCouponsToUsers(SendCouponsRequest request) {
        couponDao.assignCouponsToUsers(request.getUserIds(), request.getCouponIds());
    }

    @Override
    public void updateCouponActiveStatus(Integer couponId, boolean isActive) {
        Coupon coupon = couponDao.getCouponById(couponId);
        if (coupon != null) {
            coupon.setActive(isActive);
            couponDao.updateCoupon(couponId, coupon);
        }
    }

    @Override
    public Map<String, String> validateCouponData(CouponDTO couponDTO) {
        Map<String, String> errors = new HashMap<>();
        if (couponDTO.getCode() == null || couponDTO.getCode().isBlank()) {
            errors.put("code", "優惠券代碼不可為空");
        } else if (isCodeExists(couponDTO.getCode())) {
            errors.put("code", "優惠券代碼已存在");
        }
        if (couponDTO.getDiscountType() == null) {
            errors.put("discountType", "折扣類型不可為空");
        }
        if (couponDTO.getDiscountValue() == null || couponDTO.getDiscountValue().signum() <= 0) {
            errors.put("discountValue", "折扣值必須大於 0");
        }
        if (couponDTO.getQuantity() == null || couponDTO.getQuantity() <= 0) {
            errors.put("quantity", "數量必須大於 0");
        }
        if (couponDTO.getStartDate() != null && couponDTO.getEndDate() != null
                && couponDTO.getStartDate().after(couponDTO.getEndDate())) {
            errors.put("endDate", "結束日期不可早於開始日期");
        }
        return errors;
    }

    @Override
    public boolean isCodeExists(String code) {
        return couponDao.isCodeExists(code);
    }

    private Coupon adjustCouponDates(Coupon coupon) {
        if (coupon.getStartDate() != null) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(coupon.getStartDate());
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            coupon.setStartDate(startCal.getTime());
        }
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
