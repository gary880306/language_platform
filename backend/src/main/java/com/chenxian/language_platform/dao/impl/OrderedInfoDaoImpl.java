package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.OrderedInfoDao;
import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;
import com.chenxian.language_platform.rowmapper.OrderIncludeUserRowMapper;
import com.chenxian.language_platform.rowmapper.OrderItemIncludeUserRowMapper;
import com.chenxian.language_platform.rowmapper.OrderItemRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderedInfoDaoImpl implements OrderedInfoDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public List<Order> findAllOrder() {
        String sql = "SELECT o.order_id, o.user_id,u.user_name, o.total_amount, o.created_date ,o.discount FROM `order` o LEFT JOIN user u ON o.user_id = u.user_id";
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

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        String sql = "SELECT order_id, user_id, total_amount, created_date, discount FROM `order` WHERE user_id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<Order> orders = namedParameterJdbcTemplate.query(
                sql,
                map,
                new BeanPropertyRowMapper<>(Order.class)
        );

        for (Order order : orders) {
            order.setOrderItems(getOrderItemsByOrderId(order.getOrderId()));
        }

        return orders;
    }

    @Override
    public void saveOrder(Order order) {
        String sql = "INSERT INTO `order` (user_id, total_amount, discount) " +
                "VALUES (:userId, :totalAmount, :discount)";

        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("userId", order.getUserId())
                .addValue("totalAmount", order.getTotalAmount())
                .addValue("discount", order.getDiscount());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public Order findOrderById(Integer orderId) {
        String sql = "SELECT order_id, user_id, total_amount, discount FROM `order` WHERE order_id = :orderId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderId", orderId);

        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalAmount(rs.getInt("total_amount"));
                order.setDiscount(rs.getInt("discount"));
                return order;
            }
        });
    }

    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.course_id, oi.amount, c.course_name, c.image_url " +
                "FROM order_item oi " +
                "INNER JOIN course c ON oi.course_id = c.course_id " +
                "WHERE oi.order_id = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);

        return namedParameterJdbcTemplate.query(
                sql,
                map,
                new BeanPropertyRowMapper<>(OrderItem.class)
        );
    }
}
