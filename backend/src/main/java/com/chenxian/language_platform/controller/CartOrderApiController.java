package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.FrontendService;
import com.chenxian.language_platform.service.OrderedInfoService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 購物車與訂單 REST API
 */
@RestController
@RequestMapping("/api")
public class CartOrderApiController {

    @Autowired
    private FrontendService frontendService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderedInfoService orderedInfoService;

    // ==================== 購物車 ====================

    /** 取得購物車 */
    @GetMapping("/cart")
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Cart cart = frontendService.getCartByUserId(userId);
        if (cart == null) return ResponseEntity.ok(Map.of("items", List.of(), "total", 0));

        List<CartItem> items = frontendService.getCartItemsByCartId(cart.getCartId());
        int total = items.stream()
                .mapToInt(item -> item.getCourse() != null ? item.getCourse().getPrice() : 0)
                .sum();
        return ResponseEntity.ok(Map.of("cartId", cart.getCartId(), "items", items, "total", total));
    }

    /** 加入課程到購物車 */
    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Integer> body,
                                        HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer courseId = body.get("courseId");

        if (courseId == null) return ResponseEntity.badRequest().body(Map.of("message", "課程ID不可為空"));

        // 檢查是否已購買
        if (frontendService.hasPurchasedCourse(userId, courseId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "您已購買此課程"));
        }

        frontendService.addToCart(userId, courseId);
        return ResponseEntity.ok(Map.of("message", "加入購物車成功"));
    }

    /** 從購物車移除課程 */
    @DeleteMapping("/cart/items/{courseId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Integer courseId,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        frontendService.removeFromCart(userId, courseId);
        return ResponseEntity.noContent().build();
    }

    /** 套用優惠券 */
    @PostMapping("/cart/coupon")
    public ResponseEntity<?> applyCoupon(@RequestBody Map<String, String> body,
                                          HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String couponCode = body.get("couponCode");

        Coupon coupon = frontendService.validateCoupon(userId, couponCode);
        if (coupon == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "優惠券無效或已使用"));
        }
        return ResponseEntity.ok(Map.of(
                "couponId", coupon.getCouponId(),
                "discountType", coupon.getDiscountType(),
                "discountValue", coupon.getDiscountValue(),
                "message", "優惠券套用成功"
        ));
    }

    /** 結帳 */
    @PostMapping("/cart/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> body,
                                       HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer couponId = body.get("couponId") != null
                ? ((Number) body.get("couponId")).intValue() : null;

        CheckoutResponse result = frontendService.checkout(userId, couponId);
        if (result == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "結帳失敗，購物車可能為空"));
        }
        return ResponseEntity.ok(result);
    }

    // ==================== 我的訂單（用戶） ====================

    /** 取得用戶訂單列表 */
    @GetMapping("/user/orders")
    public ResponseEntity<?> getUserOrders(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<Order> orders = orderedInfoService.findOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /** 取得訂單詳細項目 */
    @GetMapping("/user/orders/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Integer orderId,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Order order = orderedInfoService.findOrderById(orderId);
        if (order == null) return ResponseEntity.notFound().build();

        List<OrderItem> items = orderedInfoService.findOrderItemByOrderId(orderId);
        return ResponseEntity.ok(Map.of("order", order, "items", items));
    }

    // ==================== 訂單管理（管理員） ====================

    /** 取得所有訂單 */
    @GetMapping("/admin/orders")
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderedInfoService.findAllOrder();
        return ResponseEntity.ok(orders);
    }

    /** 取得訂單詳細項目（管理員） */
    @GetMapping("/admin/orders/{orderId}")
    public ResponseEntity<?> getAdminOrderDetail(@PathVariable Integer orderId) {
        Order order = orderedInfoService.findOrderById(orderId);
        if (order == null) return ResponseEntity.notFound().build();
        List<OrderItem> items = orderedInfoService.findOrderItemByOrderId(orderId);
        return ResponseEntity.ok(Map.of("order", order, "items", items));
    }
}
