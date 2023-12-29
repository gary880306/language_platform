package com.chenxian.language_platform.model;

import lombok.Data;

@Data
public class UserCoupon {
    private Integer userId;
    private Integer couponId;
    private boolean isUsed;
}
