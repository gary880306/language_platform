package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.CouponDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.model.UserCourse;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponDao couponDao;

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
    public List<Coupon> getAllCoupons() {
        return couponDao.getAllCoupons();
    }
}
