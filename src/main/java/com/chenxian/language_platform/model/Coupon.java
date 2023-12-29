package com.chenxian.language_platform.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class Coupon {
    private Integer couponId;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isActive;

    // 枚舉類型，用於表示折扣類型
    public enum DiscountType {
        FIXED, PERCENTAGE
    }
}
