package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CouponDTO;
import com.chenxian.language_platform.dto.SendCouponsRequest;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 優惠券 REST API（管理員）
 */
@RestController
@RequestMapping("/api/admin/coupons")
public class CouponApiController {

    @Autowired
    private CouponService couponService;

    /** 取得所有優惠券 */
    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    /** 取得可用優惠券 */
    @GetMapping("/available")
    public ResponseEntity<List<Coupon>> getAvailableCoupons() {
        return ResponseEntity.ok(couponService.getAvailableActiveCoupons());
    }

    /** 取得單一優惠券 */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCouponById(@PathVariable int id) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(coupon);
    }

    /** 建立優惠券 */
    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        couponService.createCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "優惠券建立成功"));
    }

    /** 啟用/停用優惠券 */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> updateCouponStatus(@PathVariable int id,
                                                 @RequestBody Map<String, Boolean> body) {
        Boolean isActive = body.get("isActive");
        if (isActive == null) return ResponseEntity.badRequest().body(Map.of("message", "isActive 不可為空"));
        couponService.updateCouponActiveStatus(id, isActive);
        return ResponseEntity.ok(Map.of("message", "優惠券狀態已更新"));
    }

    /** 刪除優惠券 */
    @DeleteMapping("/{couponId}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    /** 發放優惠券給用戶 */
    @PostMapping("/send")
    public ResponseEntity<?> sendCoupons(@RequestBody SendCouponsRequest req) {
        couponService.sendCouponsToUsers(req);
        return ResponseEntity.ok(Map.of("message", "優惠券發放成功"));
    }

    /** 驗證優惠券資料 */
    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody CouponDTO couponDTO) {
        Map<String, String> errors = couponService.validateCouponData(couponDTO);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        return ResponseEntity.ok(Map.of("message", "驗證通過"));
    }
}
