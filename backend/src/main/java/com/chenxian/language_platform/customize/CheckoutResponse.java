package com.chenxian.language_platform.customize;

import lombok.Data;

import java.util.Map;

@Data
public class CheckoutResponse {
    private Boolean success;
    private Integer orderId;
    // ECPay 付款相關欄位（僅在啟用 ECPay 流程時有值）
    private Map<String, String> ecpayParams;
    private String ecpayUrl;

    public CheckoutResponse(boolean success, Integer orderId) {
        this.success = success;
        this.orderId = orderId;
    }

    public CheckoutResponse(boolean success, Integer orderId,
                            Map<String, String> ecpayParams, String ecpayUrl) {
        this.success = success;
        this.orderId = orderId;
        this.ecpayParams = ecpayParams;
        this.ecpayUrl = ecpayUrl;
    }
}
