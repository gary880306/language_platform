package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class Cart {
    private Integer cartId;
    private User user;
    private List<CartItem> cartItems;
    private Integer userId;
    private boolean isCheckout;
    private Timestamp createdDate;
    private Timestamp checkoutDate;
    private Integer couponId;
}
