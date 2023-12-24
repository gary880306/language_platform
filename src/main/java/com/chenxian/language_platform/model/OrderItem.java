package com.chenxian.language_platform.model;

import lombok.Data;

@Data
public class OrderItem {
    private Integer orderItemId;
    private Integer orderId;
    private Integer courseId;
    private Integer amount;
}
