package com.chenxian.language_platform.customize;

import lombok.Data;

@Data
public class CheckoutResponse {
    private Boolean success;
    private Integer orderId;

    public CheckoutResponse(boolean success, Integer orderId) {
        this.success = success;
        this.orderId = orderId;
    }
}
