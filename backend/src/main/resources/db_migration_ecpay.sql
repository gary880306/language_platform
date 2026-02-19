-- ECPay 金流整合 - 資料庫變更腳本
-- 執行前請先備份資料庫！

-- 1. 在 `order` 表新增 payment_status 欄位
ALTER TABLE `order`
    ADD COLUMN payment_status VARCHAR(10) NOT NULL DEFAULT 'PAID'
    COMMENT '付款狀態：PENDING=待付款, PAID=已付款, FAILED=付款失敗'
    AFTER discount;

-- 將舊資料的 payment_status 設為 PAID（因為舊資料都已完成付款）
UPDATE `order` SET payment_status = 'PAID' WHERE payment_status = 'PAID';

-- 2. 建立 payment_transaction 資料表（記錄每筆 ECPay 交易）
CREATE TABLE IF NOT EXISTS payment_transaction (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    order_id    INT         NOT NULL            COMMENT '對應的訂單 ID',
    cart_id     INT         NOT NULL            COMMENT '結帳時的購物車 ID',
    merchant_trade_no VARCHAR(20) NOT NULL UNIQUE COMMENT 'ECPay MerchantTradeNo（最多20碼）',
    trade_no    VARCHAR(20)                     COMMENT 'ECPay 交易編號（付款後由 ECPay 回傳）',
    payment_date VARCHAR(20)                    COMMENT '付款時間（ECPay 回傳格式）',
    rtn_code    INT                             COMMENT 'ECPay 回傳碼（1=成功）',
    rtn_msg     VARCHAR(200)                    COMMENT 'ECPay 回傳訊息',
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    FOREIGN KEY (order_id) REFERENCES `order`(order_id) ON DELETE CASCADE
) COMMENT='ECPay 付款交易記錄';
