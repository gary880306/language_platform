package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Coupon;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponRowMapper implements RowMapper<Coupon> {
    @Override
    public Coupon mapRow(ResultSet rs, int rowNum) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setCouponId(rs.getInt("coupon_id"));
        coupon.setCode(rs.getString("code"));
        coupon.setDescription(rs.getString("description"));
        String discountTypeStr = rs.getString("discount_type");
        Coupon.DiscountType discountType = Coupon.DiscountType.valueOf(discountTypeStr.toUpperCase());
        coupon.setDiscountType(discountType);
        coupon.setDiscountValue(rs.getBigDecimal("discount_value"));
        coupon.setStartDate(rs.getTimestamp("start_date"));
        coupon.setEndDate(rs.getTimestamp("end_date"));
        coupon.setActive(rs.getBoolean("is_active"));
        coupon.setQuantity(rs.getInt("quantity"));
        return coupon;
    }
}
