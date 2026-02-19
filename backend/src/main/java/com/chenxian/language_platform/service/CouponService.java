package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.CouponDTO;
import com.chenxian.language_platform.dto.SendCouponsRequest;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.UserCoupon;

import java.util.List;
import java.util.Map;

/**
 * 優惠券服務介面 (SRP: 只負責優惠券管理)
 * (ISP: 補齊控制器實際呼叫的方法，消除介面不一致)
 */
public interface CouponService {
    /** 創建優惠券 */
    Coupon createCoupon(Coupon coupon);

    /** 依 ID 取得優惠券 */
    Coupon getCouponById(Integer couponId);

    /** 修改優惠券 */
    Coupon updateCoupon(Integer couponId, Coupon coupon);

    /** 刪除優惠券 */
    void deleteCoupon(Integer couponId);

    /** 軟刪除優惠券 */
    void markCouponAsDeleted(Integer couponId);

    /** 取得所有優惠券 */
    List<Coupon> getAllCoupons();

    /** 取得所有未被軟刪除的優惠券 */
    List<Coupon> getAllActiveCoupons();

    /** 取得所有上架且未過期的優惠券 */
    List<Coupon> getAvailableActiveCoupons();

    /** 將優惠券套用至購物車 */
    void applyCouponToCart(Integer userId, Integer couponId);

    /** 檢查用戶是否持有此優惠券 */
    boolean checkCouponExists(Integer userId, Integer couponId);

    /** 檢查用戶是否持有此優惠券且未使用 */
    boolean checkCouponExistsByIsUsed(Integer userId, Integer couponId);

    /** 依用戶 ID 取得 Coupon 列表（管理後台用） */
    List<Coupon> getCouponsByUserId(Integer userId);

    /** 依用戶 ID 取得 UserCoupon 列表（含使用狀態，前端用） */
    List<UserCoupon> getUserCoupons(Integer userId);

    /** 刪除用戶優惠券 */
    void deleteUserCoupon(Integer userId, Integer couponId);

    /** 發放優惠券給指定用戶（OCP: 封裝發送邏輯，未來擴展不需修改介面） */
    void sendCouponsToUsers(SendCouponsRequest request);

    /** 更新優惠券啟用/停用狀態 */
    void updateCouponActiveStatus(Integer couponId, boolean isActive);

    /** 驗證優惠券表單資料，回傳錯誤 Map（空 Map 表示驗證通過） */
    Map<String, String> validateCouponData(CouponDTO couponDTO);

    /** 判斷優惠券代碼是否已存在 */
    boolean isCodeExists(String code);
}
