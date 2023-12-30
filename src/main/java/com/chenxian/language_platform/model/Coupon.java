package com.chenxian.language_platform.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Coupon {
    private Integer couponId;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    private boolean isActive;
    private Integer quantity;

    // 枚舉類型，用於表示折扣類型
    public enum DiscountType {
        FIXED,PERCENTAGE
    }

}
