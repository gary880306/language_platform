package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.model.UserCoupon;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCouponRowMapper implements RowMapper<UserCoupon> {
    @Override
    public UserCoupon mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCouponId(rs.getInt("coupon_id"));
        userCoupon.setUserId(rs.getInt("user_id"));
        userCoupon.setUsed(rs.getBoolean("is_used"));
        Coupon coupon = new Coupon();
        coupon.setCode(rs.getString("code"));
        coupon.setDescription(rs.getString("description"));
        String discountTypeStr = rs.getString("discount_type");
        Coupon.DiscountType discountType = Coupon.DiscountType.valueOf(discountTypeStr.toUpperCase());
        coupon.setDiscountType(discountType);
        coupon.setDiscountValue(rs.getBigDecimal("discount_value"));
        coupon.setStartDate(rs.getTimestamp("start_date"));
        coupon.setEndDate(rs.getTimestamp("end_date"));
        coupon.setActive(rs.getBoolean("is_active"));
        coupon.setIsDeleted(rs.getBoolean("is_deleted"));
        userCoupon.setCoupon(coupon);
        return userCoupon;
    }
}
