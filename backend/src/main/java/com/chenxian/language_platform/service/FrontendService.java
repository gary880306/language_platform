package com.chenxian.language_platform.service;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.Course;

import java.util.List;
import java.util.Map;

public interface FrontendService {
    List<Course> getAllCourses(CourseQueryParams courseQueryParams);
    List<Course> getAllCoursesWithUserCount(CourseQueryParams courseQueryParams);
    Integer getCoursesCount(CourseQueryParams courseQueryParams);
    Map<Integer, Integer> getCourseUserCounts();

    // 購物車相關
    Cart getCartByUserId(Integer userId);
    List<CartItem> getCartItemsByCartId(Integer cartId);
    boolean hasPurchasedCourse(Integer userId, Integer courseId);
    void addToCart(Integer userId, Integer courseId);
    void removeFromCart(Integer userId, Integer courseId);
    Coupon validateCoupon(Integer userId, String couponCode);

    // 結帳（建立 PENDING 訂單並回傳 ECPay 付款參數）
    CheckoutResponse checkout(Integer userId, Integer couponId);
}
