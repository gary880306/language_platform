package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentTransaction {
    private Integer id;
    private Integer orderId;
    private Integer cartId;
    private String merchantTradeNo;
    private String tradeNo;
    private String paymentDate;
    private Integer rtnCode;
    private String rtnMsg;
    private Timestamp createdAt;
}
