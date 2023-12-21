package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Cart {
    private Integer cartId;
    private User user;
    private Integer userId;
    private boolean isCheckout;
    private Timestamp createdDate;
    private Timestamp checkoutDate;
}
