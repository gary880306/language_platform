package com.chenxian.language_platform.service;

import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;

import java.util.List;

public interface OrderedInfoService {
    List<Order> findAllOrder();
    List<OrderItem> findOrderItemByOrderId(Integer orderId);
}
