package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrderItem {
    private Integer orderItemId;
    private Integer orderId;
    private Integer courseId;
    private Integer amount;
    private String courseName;
    private String imageUrlString;
    private Timestamp created_date;
    private Integer totalAmount;
}
