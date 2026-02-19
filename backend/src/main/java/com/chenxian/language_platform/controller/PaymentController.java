package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.PaymentTransaction;
import com.chenxian.language_platform.service.EcpayService;
import com.chenxian.language_platform.service.OrderedInfoService;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ECPay 付款相關端點
 *
 * 流程說明：
 * 1. 前端呼叫 POST /api/cart/checkout → 建立 PENDING 訂單，回傳 ECPay 表單參數
 * 2. 前端動態建立表單並送出到 ECPay 付款頁
 * 3. 付款完成後，ECPay POST 到 /payment/return（伺服器端回調）
 *    → 驗證簽章 → 更新訂單狀態 → 建立 user_course → 標記購物車已結帳
 *    → 回傳 "1|OK"
 * 4. 使用者瀏覽器被 ECPay 轉向 /payment/result（前端頁面）
 */
@RestController
public class PaymentController {

    @Autowired
    private EcpayService ecpayService;

    @Autowired
    private OrderedInfoService orderedInfoService;

    @Autowired
    private UserService userService;

    /**
     * ECPay 伺服器端回調（ReturnURL）
     * ECPay 在付款完成後 POST 到此端點，需回傳 "1|OK"
     * 注意：此端點不需要 JWT 驗證，需加入 JwtAuthenticationFilter PUBLIC_PATHS
     */
    @PostMapping(value = "/payment/return",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String handlePaymentReturn(@RequestParam Map<String, String> params) {
        // 1. 驗證 CheckMacValue
        if (!ecpayService.verifyCallback(params)) {
            return "0|CheckMacValue 驗證失敗";
        }

        String merchantTradeNo = params.get("MerchantTradeNo");
        String rtnCodeStr = params.get("RtnCode");
        String rtnMsg = params.get("RtnMsg");
        String tradeNo = params.get("TradeNo");
        String paymentDate = params.get("PaymentDate");

        if (merchantTradeNo == null) {
            return "0|MerchantTradeNo 不可為空";
        }

        // 2. 查找對應的 payment_transaction
        PaymentTransaction transaction = orderedInfoService.findTransactionByMerchantTradeNo(merchantTradeNo);
        if (transaction == null) {
            return "0|找不到對應的交易記錄";
        }

        int rtnCode = rtnCodeStr != null ? Integer.parseInt(rtnCodeStr) : 0;

        // 3. 更新 payment_transaction
        transaction.setRtnCode(rtnCode);
        transaction.setRtnMsg(rtnMsg);
        transaction.setTradeNo(tradeNo);
        transaction.setPaymentDate(paymentDate);
        orderedInfoService.updatePaymentTransaction(transaction);

        if (rtnCode == 1) {
            // 4. 付款成功：更新訂單狀態、建立 user_course、標記購物車已結帳
            orderedInfoService.updatePaymentStatus(transaction.getOrderId(), "PAID");
            userService.confirmOrderPayment(transaction.getOrderId(), transaction.getCartId());
        } else {
            // 付款失敗：更新訂單狀態
            orderedInfoService.updatePaymentStatus(transaction.getOrderId(), "FAILED");
        }

        // 5. 必須回傳 "1|OK" 給 ECPay（否則 ECPay 會持續重試）
        return "1|OK";
    }
}
