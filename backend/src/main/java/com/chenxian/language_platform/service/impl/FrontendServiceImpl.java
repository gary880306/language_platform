package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dao.FrontendDao;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FrontendServiceImpl implements FrontendService {

    @Autowired
    private FrontendDao frontendDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private EcpayService ecpayService;

    @Autowired
    private OrderedInfoService orderedInfoService;

    @Override
    public List<Course> getAllCourses(CourseQueryParams courseQueryParams) {
        return frontendDao.getAllCourses(courseQueryParams);
    }

    @Override
    public List<Course> getAllCoursesWithUserCount(CourseQueryParams courseQueryParams) {
        return frontendDao.getAllCoursesWithUserCount(courseQueryParams);
    }

    @Override
    public Integer getCoursesCount(CourseQueryParams courseQueryParams) {
        return frontendDao.getCoursesCount(courseQueryParams);
    }

    @Override
    public Map<Integer, Integer> getCourseUserCounts() {
        return frontendDao.getCourseUserCounts();
    }

    // ==================== 購物車相關 ====================

    @Override
    public Cart getCartByUserId(Integer userId) {
        return userService.findNoneCheckoutCartByUserId(userId);
    }

    @Override
    public List<CartItem> getCartItemsByCartId(Integer cartId) {
        return userService.findCartItemsById(cartId);
    }

    @Override
    public boolean hasPurchasedCourse(Integer userId, Integer courseId) {
        return userService.hasPurchasedCourse(userId, courseId);
    }

    @Override
    public void addToCart(Integer userId, Integer courseId) {
        Cart cart = userService.findNoneCheckoutCartByUserId(userId);
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setCheckout(false);
            userService.addCart(newCart);
            cart = userService.findNoneCheckoutCartByUserId(userId);
        }
        CartItem item = new CartItem();
        item.setCartId(cart.getCartId());
        item.setCourseId(courseId);
        userService.addCartItem(item);
    }

    @Override
    public void removeFromCart(Integer userId, Integer courseId) {
        Cart cart = userService.findNoneCheckoutCartByUserId(userId);
        if (cart == null) return;
        List<CartItem> items = userService.findCartItemsById(cart.getCartId());
        items.stream()
                .filter(item -> item.getCourseId().equals(courseId))
                .findFirst()
                .ifPresent(item -> userService.removeCartItemById(item.getItemId()));
    }

    @Override
    public Coupon validateCoupon(Integer userId, String couponCode) {
        List<Coupon> userCoupons = couponService.getCouponsByUserId(userId);
        if (userCoupons == null) return null;
        return userCoupons.stream()
                .filter(c -> couponCode.equals(c.getCode())
                        && c.isActive()
                        && !Boolean.TRUE.equals(c.getIsDeleted())
                        && "active".equals(c.getStatus()))
                .findFirst()
                .orElse(null);
    }

    // ==================== 結帳（ECPay 付款流程） ====================

    @Override
    public CheckoutResponse checkout(Integer userId, Integer couponId) {
        Cart cart = userService.findNoneCheckoutCartByUserId(userId);
        if (cart == null) return null;

        // 計算折扣金額
        int discount = 0;
        if (couponId != null) {
            Coupon coupon = couponService.getCouponById(couponId);
            if (coupon != null) {
                List<CartItem> items = userService.findCartItemsById(cart.getCartId());
                int subtotal = items.stream()
                        .mapToInt(item -> {
                            try {
                                return item.getCourse() != null
                                        ? item.getCourse().getPrice()
                                        : 0;
                            } catch (Exception e) { return 0; }
                        }).sum();
                if (subtotal == 0) {
                    // cart items may not have course attached, approximate from cart total
                    subtotal = cart.getCartItems() != null
                            ? cart.getCartItems().stream().mapToInt(i -> i.getCourse() != null ? i.getCourse().getPrice() : 0).sum()
                            : 0;
                }
                if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
                    discount = coupon.getDiscountValue().intValue();
                } else if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                    discount = subtotal * coupon.getDiscountValue().intValue() / 100;
                }
            }
        }

        // 建立 PENDING 訂單
        Order order = userService.createPendingOrderFromCart(userId, cart.getCartId(), discount);
        if (order == null) return null;

        // 計算實付金額
        int payAmount = Math.max(1, order.getTotalAmount() - discount);

        // 組合商品名稱
        String itemName = buildItemName(cart);

        // 產生 MerchantTradeNo 並儲存
        String merchantTradeNo = ecpayService.generateMerchantTradeNo(order.getOrderId());
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(order.getOrderId());
        transaction.setCartId(cart.getCartId());
        transaction.setMerchantTradeNo(merchantTradeNo);
        orderedInfoService.savePaymentTransaction(transaction);

        // 建立 ECPay 參數
        Map<String, String> ecpayParams = ecpayService.buildCheckoutParams(merchantTradeNo, payAmount, itemName);

        return new CheckoutResponse(true, order.getOrderId(), ecpayParams, ecpayService.getPaymentUrl());
    }

    private String buildItemName(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return "線上課程";
        }
        String names = cart.getCartItems().stream()
                .filter(i -> i.getCourse() != null)
                .map(i -> i.getCourse().getCourseName())
                .collect(Collectors.joining("#"));
        return names.isBlank() ? "線上課程" : names;
    }
}
