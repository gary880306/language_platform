package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.model.UserCoupon;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用戶個人資料 REST API
 */
@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    /** 取得個人資料 */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        if (user == null) return ResponseEntity.notFound().build();
        // 不回傳密碼
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    /** 更新個人資料 */
    @PutMapping("/info")
    public ResponseEntity<?> updateUserInfo(@RequestBody User user,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        user.setUserId(userId); // 確保只能更新自己的資料
        userService.updateUser(user);
        return ResponseEntity.ok(Map.of("message", "個人資料已更新"));
    }

    /** 取得我的優惠券 */
    @GetMapping("/coupons")
    public ResponseEntity<?> getMyCoupons(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<UserCoupon> coupons = couponService.getUserCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    /** 刪除我的優惠券 */
    @DeleteMapping("/coupons/{couponId}")
    public ResponseEntity<?> deleteMyCoupon(@PathVariable Integer couponId,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        couponService.deleteUserCoupon(couponId, userId);
        return ResponseEntity.noContent().build();
    }
}
