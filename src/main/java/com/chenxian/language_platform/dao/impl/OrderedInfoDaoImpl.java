package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.OrderedInfoDao;
import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;
import com.chenxian.language_platform.rowmapper.OrderIncludeUserRowMapper;
import com.chenxian.language_platform.rowmapper.OrderItemIncludeUserRowMapper;
import com.chenxian.language_platform.rowmapper.OrderItemRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderedInfoDaoImpl implements OrderedInfoDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public List<Order> findAllOrder() {
        String sql = "SELECT o.order_id, o.user_id,u.user_name, o.total_amount, o.created_date FROM `order` o LEFT JOIN user u ON o.user_id = u.user_id";
        Map<String,Object> map = new HashMap<>();
        List<Order> orders = namedParameterJdbcTemplate.query(sql,map,new OrderIncludeUserRowMapper());
        if(!orders.isEmpty()){

            return orders;
        }
        return null;
    }

    @Override
    public List<OrderItem> findOrderItemByOrderId(Integer orderId) {
        String sql = "SELECT o.order_item_id, o.order_id, c.course_name, c.image_url, o.course_id, o.amount, ord.created_date, ord.total_amount FROM order_item o LEFT JOIN course c ON o.course_id = c.course_id LEFT JOIN `order` ord ON o.order_id = ord.order_id WHERE o.order_id =:orderId";
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",orderId);
        List<OrderItem> orderItems = namedParameterJdbcTemplate.query(sql,map,new OrderItemIncludeUserRowMapper());
        if(!orderItems.isEmpty()){
            return orderItems;
        }
        return null;
    }
}
