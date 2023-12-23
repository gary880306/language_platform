package com.chenxian.language_platform.model;

import lombok.Data;

@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private Integer totalAmount;
}
