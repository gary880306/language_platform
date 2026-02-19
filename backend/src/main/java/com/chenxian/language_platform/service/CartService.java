package com.chenxian.language_platform.service;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.Coupon;

import java.util.List;

/**
 * 購物車服務介面 (SRP: 只負責購物車相關業務)
 * (ISP: 與用戶個人資料、認證等無關的客戶端無需依賴此介面)
 */
public interface CartService {

    /** 依用戶 ID 取得未結帳購物車 */
    Cart getCartByUserId(Integer userId);

    /** 依購物車 ID 取得購物車明細 */
    List<CartItem> getCartItemsByCartId(Integer cartId);

    /** 檢查用戶是否已購買某課程 */
    boolean hasPurchasedCourse(Integer userId, Integer courseId);

    /** 加入課程至購物車 */
    void addToCart(Integer userId, Integer courseId);

    /** 從購物車移除課程 */
    void removeFromCart(Integer userId, Integer courseId);

    /** 驗證優惠券是否有效（依代碼），回傳有效的 Coupon 或 null */
    Coupon validateCoupon(Integer userId, String couponCode);

    /** 結帳（含優惠券折扣） */
    CheckoutResponse checkout(Integer userId, Integer couponId);
}
