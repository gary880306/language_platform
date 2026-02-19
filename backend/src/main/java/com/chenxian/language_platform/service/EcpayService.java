package com.chenxian.language_platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 綠界 ECPay 金流服務
 * 負責組合付款參數與驗證回調簽章
 */
@Service
public class EcpayService {

    @Value("${ecpay.merchant-id}")
    private String merchantId;

    @Value("${ecpay.hash-key}")
    private String hashKey;

    @Value("${ecpay.hash-iv}")
    private String hashIv;

    @Value("${ecpay.payment-url}")
    private String paymentUrl;

    @Value("${ecpay.return-url}")
    private String returnUrl;

    @Value("${ecpay.order-result-url}")
    private String orderResultUrl;

    /**
     * 產生唯一的 MerchantTradeNo（最多 20 字元，英數字）
     * 格式：ORD{orderId}{8位毫秒尾數}
     */
    public String generateMerchantTradeNo(Integer orderId) {
        long suffix = System.currentTimeMillis() % 100000000L;
        return String.format("ORD%d%08d", orderId, suffix);
    }

    /**
     * 組合送往 ECPay 的參數（含 CheckMacValue）
     *
     * @param merchantTradeNo 交易編號（事先產生後存 DB）
     * @param amount          實際付款金額（總金額 - 折扣，最低 1 元）
     * @param itemName        商品名稱（課程名稱）
     */
    public Map<String, String> buildCheckoutParams(String merchantTradeNo, Integer amount, String itemName) {
        String tradeDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Map<String, String> params = new TreeMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("MerchantTradeDate", tradeDate);
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(Math.max(1, amount)));
        params.put("TradeDesc", "EnjoyLearning線上課程");
        params.put("ItemName", itemName);
        params.put("ReturnURL", returnUrl);
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");
        params.put("OrderResultURL", orderResultUrl);

        String checkMacValue = computeCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);
        return params;
    }

    /**
     * 計算 CheckMacValue（SHA256）
     * 演算法：Sort → Join → 前後加 HashKey/HashIV → URL Encode 小寫 → SHA256 → 大寫
     */
    public String computeCheckMacValue(Map<String, String> params) {
        Map<String, String> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sorted.putAll(params);
        sorted.remove("CheckMacValue");

        String joined = sorted.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String raw = "HashKey=" + hashKey + "&" + joined + "&HashIV=" + hashIv;
        String encoded = URLEncoder.encode(raw, StandardCharsets.UTF_8).toLowerCase();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(encoded.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 演算法不可用", e);
        }
    }

    /**
     * 驗證 ECPay 回調的 CheckMacValue
     */
    public boolean verifyCallback(Map<String, String> params) {
        String received = params.get("CheckMacValue");
        if (received == null || received.isBlank()) return false;

        Map<String, String> paramsWithoutMac = new HashMap<>(params);
        paramsWithoutMac.remove("CheckMacValue");
        String computed = computeCheckMacValue(paramsWithoutMac);
        return computed.equalsIgnoreCase(received);
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }
}
