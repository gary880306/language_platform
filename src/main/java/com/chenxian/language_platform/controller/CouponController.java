package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.SendCouponsRequest;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserService userService;

    // 返回用於渲染的視圖名稱
    @GetMapping
    public String showCoupons() {
        return "admin/managementCoupons";
    }

    // 創建新優惠券（RESTful API）
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        try {
            Coupon createdCoupon = couponService.createCoupon(coupon);
            return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            // 特定于重复条目的异常
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("優惠券代碼 : " + coupon.getCode() + "已被使用，請換一個");
        } catch (Exception e) {
            // 其他异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating coupon: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getCouponById(@PathVariable int id) {
        try {
            Coupon coupon = couponService.getCouponById(id);
            if (coupon == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found with ID: " + id);
            }
            // 檢查優惠券是否已被標記為刪除
            if (coupon.getIsDeleted()) {
                return ResponseEntity.status(HttpStatus.GONE).body("Coupon with ID: " + id + " has been deleted.");
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
            Coupon updatedCoupon = couponService.updateCoupon(coupon.getCouponId(), coupon);

            Map<String, Object> response = new HashMap<>();
            response.put("newActiveStatus", updatedCoupon.isActive());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新優惠券狀態時出錯：" + e.getMessage());
        }
    }



    // 刪除優惠券（RESTFul API）
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

    // 發送優惠券 model 列出所有未過期/未刪除/上架中的優惠券
    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<?> getAvailableActiveCoupons() {
        try {
            List<Coupon> coupons = couponService.getAvailableActiveCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving available coupons: " + e.getMessage());
        }
    }



    @GetMapping("/sendCoupons")
    private String sendCouponsPage(HttpSession session, Model model){
        List<User> users = userService.findAllNonAdminUsers();
        model.addAttribute("users",users);
        return "admin/sendCoupons";
    }

    // 新增的 API 用于发送优惠券
    @PostMapping("/send")
    public ResponseEntity<?> sendCouponsToUsers(@RequestBody SendCouponsRequest sendCouponsRequest) {
        if (sendCouponsRequest == null) {
            return ResponseEntity.badRequest().body("用戶和優惠券不可為空");
        }

        List<Integer> userIds = sendCouponsRequest.getUserIds();
        List<Integer> couponIds = sendCouponsRequest.getCouponIds();

        if (userIds == null || userIds.isEmpty()) {
            if (couponIds == null || couponIds.isEmpty()) {
                return ResponseEntity.badRequest().body("用戶和優惠券不可為空");
            }
            return ResponseEntity.badRequest().body("用戶不可為空");
        }

        if (couponIds == null || couponIds.isEmpty()) {
            return ResponseEntity.badRequest().body("優惠券不可為空");
        }

        try {
            // 添加逻辑以将优惠券发送给用户
            couponService.assignCouponsToUsers(userIds, couponIds);
            return ResponseEntity.ok("優惠券發送成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("錯誤資訊: " + e.getMessage());
        }
    }


    @ModelAttribute("discountTypes")
    public Coupon.DiscountType[] discountTypes() {
        return Coupon.DiscountType.values();
    }

}
