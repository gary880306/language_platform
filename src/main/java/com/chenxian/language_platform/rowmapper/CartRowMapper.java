package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CategoryData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CartRowMapper implements RowMapper<Cart> {
    @Override
    public Cart mapRow(ResultSet rs, int rowNum) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(rs.getInt("cart_id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setCheckout(rs.getBoolean("isCheckout"));
        cart.setCreatedDate(rs.getTimestamp("created_date"));
        cart.setCheckoutDate(rs.getTimestamp("checkout_date"));
        cart.setCouponId(rs.getInt("coupon_id"));
        return cart;
    }
}
