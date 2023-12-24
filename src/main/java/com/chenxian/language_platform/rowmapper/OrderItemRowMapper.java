package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(rs.getInt("order_item_id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setCourseId(rs.getInt("course_id"));
        orderItem.setAmount(rs.getInt("amount"));
        return orderItem;
    }
}
