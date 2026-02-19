package com.chenxian.language_platform.model;

import lombok.Data;

@Data
public class UserCoupon {
    private Integer userId;  // 用戶 ID
    private Integer couponId;// 優惠券 ID
    private boolean isUsed;  // 標示是否已被使用
    private String status;   // 標示優惠券狀態

    // 關聯
    private Coupon coupon;   // 關聯優惠券
}
