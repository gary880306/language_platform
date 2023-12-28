package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemIncludeUserRowMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(rs.getInt("order_item_id"));
        orderItem.setAmount(rs.getInt("amount"));
        orderItem.setCourseId(rs.getInt("course_id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setCourseName(rs.getString("course_name"));
        orderItem.setImageUrlString(rs.getString("image_url"));
        orderItem.setTotalAmount(rs.getInt("total_amount"));
        orderItem.setCreated_date(rs.getTimestamp("created_date"));

        return orderItem;
    }
}
