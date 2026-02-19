package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;
import com.chenxian.language_platform.model.PaymentTransaction;

import java.util.List;

public interface OrderedInfoDao {
    List<Order> findAllOrder();
    List<OrderItem> findOrderItemByOrderId(Integer orderId);
    List<Order> getOrdersByUserId(Integer userId);
    List<Order> findOrdersByUserId(Integer userId);
    void saveOrder(Order order);
    Order findOrderById(Integer orderId);
    // 付款相關
    void updatePaymentStatus(Integer orderId, String paymentStatus);
    void savePaymentTransaction(PaymentTransaction transaction);
    PaymentTransaction findTransactionByMerchantTradeNo(String merchantTradeNo);
    void updatePaymentTransaction(PaymentTransaction transaction);
}