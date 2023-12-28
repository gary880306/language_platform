package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;

import java.util.List;

public interface OrderedInfoDao {
    List<Order> findAllOrder();
    List<OrderItem> findOrderItemByOrderId(Integer orderId);
}