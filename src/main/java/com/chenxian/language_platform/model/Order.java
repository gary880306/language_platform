package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private Integer totalAmount;
    private Timestamp createdDate;
    private String userName;
}
