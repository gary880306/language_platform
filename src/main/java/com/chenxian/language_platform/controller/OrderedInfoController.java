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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class OrderedInfoController {
    @Autowired
    private OrderedInfoService orderedInfoService;
    @GetMapping("/admin/orderedInfo")
    public String showAllOrderedInfo(Model model){
        List<Order> orders = orderedInfoService.findAllOrder(); // 獲取所有訂單資訊
        if (orders != null) {
            for (Order order : orders) {
                String formattedTotalAmount = formatCoursePrice(order.getTotalAmount());
                order.setFormattedTotalAmount(formattedTotalAmount);
            }
        }
        model.addAttribute("orders",orders);
        return "/admin/orderedInfo";
    }

    @GetMapping("/admin/orderedItemInfo")
    public String getOrderDetails(@RequestParam Integer orderId, Model model) {
        // 假设您的服务返回一个订单详情列表
        List<OrderItem> orderItems = orderedInfoService.findOrderItemByOrderId(orderId);

        Order order = orderedInfoService.findOrderById(orderId);

        // 格式化每个 OrderItem 的价格
        for (OrderItem item : orderItems) {
            // 先将价格转换成字符串，然后格式化添加逗号
            String priceAsString = String.format("%,d", item.getAmount());
            item.setFormattedAmount(priceAsString);
        }
        // 计算总金额和折扣
        int total = orderItems.stream()
                .mapToInt(OrderItem::getAmount)
                .sum();
        int discount = order.getDiscount();

        // 计算最终总金额
        int finalTotal = total - discount;

        String formattedDiscount = String.format("%,d", discount);
        String formattedFinalTotal = String.format("%,d", finalTotal);
        model.addAttribute("formattedDiscount", formattedDiscount);
        model.addAttribute("finalTotal", formattedFinalTotal);
        model.addAttribute("orderItems", orderItems);
        return "/admin/orderedItemInfo";
    }




    private String formatCoursePrice(Integer price) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return formatter.format(price);
    }

}
