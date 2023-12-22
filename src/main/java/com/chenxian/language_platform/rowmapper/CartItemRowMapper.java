package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemRowMapper implements RowMapper<CartItem> {
    @Override
    public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setItemId(rs.getInt("item_id"));
        cartItem.setCartId(rs.getInt("cart_id"));
        cartItem.setCourseId(rs.getInt("course_id"));
        cartItem.setCreatedDate(rs.getTimestamp("created_date"));
        cartItem.setLastModifiedDate(rs.getTimestamp("last_modified_date"));
        return cartItem;
    }
}
