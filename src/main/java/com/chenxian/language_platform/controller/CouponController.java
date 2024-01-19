package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    // 返回用於渲染的視圖名稱
    @GetMapping
    public String showCoupons() {
        return "admin/managementCoupons";
    }

    @GetMapping("/sendCoupons")
    public String sendCoupons() {
        return "admin/sendCoupons";
    }

    // 創建新優惠券（RESTful API）
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        try {
            Coupon createdCoupon = couponService.createCoupon(coupon);
            return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating coupon: " + e.getMessage());
        }
    }

    // 根據 ID 讀取優惠券（RESTful API）
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getCouponById(@PathVariable int id) {
        try {
            Coupon coupon = couponService.getCouponById(id);
            if (coupon == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found with ID: " + id);
            }
            return ResponseEntity.ok(coupon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving coupon: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/activate")
    @ResponseBody
    public ResponseEntity<?> updateCouponActiveStatus(@PathVariable int id, @RequestBody boolean isActive) {
        try {
            Coupon coupon = couponService.getCouponById(id);
            if (coupon == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("優惠券未找到，ID: " + id);
            }
            coupon.setActive(isActive);
            Coupon updatedCoupon = couponService.updateCoupon(coupon.getCouponId(),coupon);
            return ResponseEntity.ok(updatedCoupon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新優惠券狀態時出錯：" + e.getMessage());
        }
    }


    // 刪除優惠券（RESTful API）
    @DeleteMapping("/{couponId}")
    @ResponseBody
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer couponId) {
        try {
            // 更新優惠券記錄為 '已刪除' 狀態，而不是從資料庫中完全移除
            couponService.markCouponAsDeleted(couponId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting coupon: " + e.getMessage());
        }
    }

    // 列出所有優惠券（RESTful API）
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<?> getAllCoupons() {
        try {
            List<Coupon> coupons = couponService.getAllActiveCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving coupons: " + e.getMessage());
        }
    }

    @ModelAttribute("discountTypes")
    public Coupon.DiscountType[] discountTypes() {
        return Coupon.DiscountType.values();
    }

}
