package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.OrderedInfoDao;
import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;
import com.chenxian.language_platform.model.PaymentTransaction;
import com.chenxian.language_platform.service.OrderedInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderedInfoServiceImpl implements OrderedInfoService {
    @Autowired
    private OrderedInfoDao orderedInfoDao;

    @Override
    public List<Order> findAllOrder() {
        return orderedInfoDao.findAllOrder();
    }

    @Override
    public List<OrderItem> findOrderItemByOrderId(Integer orderId) {
        return orderedInfoDao.findOrderItemByOrderId(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderedInfoDao.getOrdersByUserId(userId);
    }

    @Override
    public List<Order> findOrdersByUserId(Integer userId) {
        return orderedInfoDao.findOrdersByUserId(userId);
    }

    @Override
    public void saveOrder(Order order) {
        orderedInfoDao.saveOrder(order);
    }

    @Override
    public Order findOrderById(Integer orderId) {
        return orderedInfoDao.findOrderById(orderId);
    }

    @Override
    public void updatePaymentStatus(Integer orderId, String paymentStatus) {
        orderedInfoDao.updatePaymentStatus(orderId, paymentStatus);
    }

    @Override
    public void savePaymentTransaction(PaymentTransaction transaction) {
        orderedInfoDao.savePaymentTransaction(transaction);
    }

    @Override
    public PaymentTransaction findTransactionByMerchantTradeNo(String merchantTradeNo) {
        return orderedInfoDao.findTransactionByMerchantTradeNo(merchantTradeNo);
    }

    @Override
    public void updatePaymentTransaction(PaymentTransaction transaction) {
        orderedInfoDao.updatePaymentTransaction(transaction);
    }
}
