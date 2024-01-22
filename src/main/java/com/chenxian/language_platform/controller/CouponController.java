package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CouponDTO;
import com.chenxian.language_platform.dto.SendCouponsRequest;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpSession;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        }
        catch (Exception e) {
            // 其他异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating coupon: " + e.getMessage());
        }
    }



    private Coupon convertToEntity(CouponDTO couponDTO) {
        Coupon coupon = new Coupon();
        coupon.setCode(couponDTO.getCode());
        coupon.setDescription(couponDTO.getDescription());
        coupon.setDiscountType(couponDTO.getDiscountType());
        coupon.setDiscountValue(couponDTO.getDiscountValue());
        coupon.setStartDate(couponDTO.getStartDate());
        coupon.setEndDate(couponDTO.getEndDate());
        coupon.setQuantity(couponDTO.getQuantity());
        // 设置其他字段，如果有的话
        return coupon;
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

    @PostMapping("/validateCouponData")
    public ResponseEntity<?> validateCouponData(@RequestBody CouponDTO couponDTO) throws ParseException {
        Map<String, String> errors = new HashMap<>();
        System.out.println(couponDTO);
        // 手动验证每个字段
        if (couponDTO.getCode() == null || couponDTO.getCode().trim().isEmpty()) {
            errors.put("code", "優惠券代碼不能為空");
        } else if (couponService.isCodeExists(couponDTO.getCode())) {
            errors.put("code", "優惠券代碼已存在");
        }

        System.out.println(couponDTO.getDescription());
        if (couponDTO.getDescription().isEmpty()) {
            errors.put("description", "描述不能為空");
        }

        if (couponDTO.getDiscountType() == null) {
            errors.put("discountType", "折扣類型不能為空");
        } else {
            if (couponDTO.getDiscountValue() == null) {
                errors.put("discountValue", "折扣值不能為空");
            } else {
                if (couponDTO.getDiscountType() == Coupon.DiscountType.FIXED && couponDTO.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.put("discountValue", "固定折扣值必須大於0");
                } else if (couponDTO.getDiscountType() == Coupon.DiscountType.PERCENTAGE &&
                        (couponDTO.getDiscountValue().compareTo(BigDecimal.ONE) < 0 ||
                                couponDTO.getDiscountValue().compareTo(new BigDecimal("99")) > 0)) {
                    errors.put("discountValue", "百分比折扣值必須在1到99之間");
                }
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = sdf.parse(sdf.format(new Date()));

        if (couponDTO.getStartDate() == null) {
            errors.put("startDate", "開始日期不能為空");
        } else if (!couponDTO.getStartDate().after(today) && !sdf.format(couponDTO.getStartDate()).equals(sdf.format(today))) {
            errors.put("startDate", "開始日期必須是今天或以後的日期");
        }


        if (couponDTO.getEndDate() == null) {
            errors.put("endDate", "截止日期不能為空");
        }

        if (couponDTO.getQuantity() == null) {
            errors.put("quantity", "數量不能為空");
        } else if (couponDTO.getQuantity() > 10) {
            errors.put("quantity", "數量不能超過10");
        }

        // 检查是否有验证错误
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 验证通过的处理逻辑
        return ResponseEntity.ok(Collections.singletonMap("message", "Validation successful"));
    }


}
