package com.chenxian.language_platform.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private Integer totalAmount;
    private Integer discount;
    private Timestamp createdDate;
    private String userName;
    private String formattedTotalAmount;
    private List<OrderItem> orderItems;
}
