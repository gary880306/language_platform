package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.repository.UserRepository;
import com.chenxian.language_platform.service.CouponService;
import com.chenxian.language_platform.service.OrderedInfoService;
import com.chenxian.language_platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class UserInfoController {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private OrderedInfoService orderedInfoService;
    @Autowired
    private CouponService couponService;

    @GetMapping("/info")
    public String getUserInfo(@RequestParam Integer userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "user/personalInformation/myInfo";
    }

    @PostMapping("/updateUserInfo")
    public ResponseEntity<?> updateUserInfo(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error updating user"));
        }
    }


    @GetMapping("/orders")
    public String getOrders(@RequestParam Integer userId, Model model) {
        List<Order> orders = orderedInfoService.getOrdersByUserId(userId);
        for (Order order : orders) {
            Integer totalAmount = order.getTotalAmount();
            Integer discount = order.getDiscount() != null ? order.getDiscount() : 0; // 確保 discount 不為 null
            Integer discountedTotal = totalAmount - discount; // 計算折扣後的總金額

            String formattedTotalAmount = formatCoursePrice(discountedTotal); // 格式化折扣後的總金額
            order.setFormattedTotalAmount(formattedTotalAmount);
        }
        model.addAttribute("orders", orders);
        return "user/personalInformation/orderedList";
    }


    @GetMapping("/coupons")
    public String getCoupons(@RequestParam Integer userId, Model model) {
        List<Coupon> coupons = couponService.getCouponsByUserId(userId);
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("coupons", coupons);
        return "user/personalInformation/myCoupon";
    }

    @DeleteMapping("/deleteCoupon/{couponId}/{userId}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Integer couponId, @PathVariable Integer userId) {
        try {
            couponService.deleteUserCoupon(userId, couponId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting coupon");
        }
    }



    @GetMapping("/admin/userInfo")
    public String showAllReport(Model model){
        List<User> users = userService.findAllNonAdminUsers();
        model.addAttribute("users",users);
        return "admin/userInfo";
    }

    @PostMapping("/admin/updateUserStatus")
    public ResponseEntity<?> updateUserStatus(@RequestParam Integer userId,
                                              @RequestParam Boolean isActive) {
        try {
            userService.updateUserActiveStatus(userId, isActive);
            return ResponseEntity.ok("User status updated successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user status: " + e.getMessage());
        }
    }

    private String formatCoursePrice(Integer price) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return formatter.format(price);
    }

}
