package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.service.OrderedInfoService;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderedInfoController {
    @Autowired
    private OrderedInfoService orderedInfoService;
    @GetMapping("/admin/orderedInfo")
    public String showAllOrderedInfo(Model model){
        List<Order> orders = orderedInfoService.findAllOrder(); // 獲取所有訂單資訊
        model.addAttribute("orders",orders);
        return "/admin/orderedInfo";
    }

    @GetMapping("/admin/orderedItemInfo")
    public String getOrderDetails(@RequestParam Integer orderId, Model model) {
        // 假设您的服务返回一个订单详情列表
        List<OrderItem> orderItems = orderedInfoService.findOrderItemByOrderId(orderId);
        model.addAttribute("orderItems", orderItems);
        return "/admin/orderedItemInfo";
    }

}
