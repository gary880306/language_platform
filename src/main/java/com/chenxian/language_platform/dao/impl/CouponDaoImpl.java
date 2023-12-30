package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.CouponDao;
import com.chenxian.language_platform.model.Coupon;
import com.chenxian.language_platform.rowmapper.CouponRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CouponDaoImpl implements CouponDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Coupon createCoupon(Coupon coupon) {
        String sql = "INSERT INTO coupon (code, description, discount_type, discount_value, start_date, end_date, quantity) VALUES (:code, :description, :discountType, :discountValue, :startDate, :endDate, :quantity)";
        Map<String, Object> map = new HashMap<>();
        map.put("code", coupon.getCode());
        map.put("description", coupon.getDescription());
        map.put("discountType", coupon.getDiscountType().toString());
        map.put("discountValue", coupon.getDiscountValue());
        map.put("startDate", coupon.getStartDate());
        map.put("endDate", coupon.getEndDate());
        map.put("quantity", coupon.getQuantity());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        Integer couponId = keyHolder.getKey().intValue();
        Coupon newCoupon = getCouponById(couponId);
        return newCoupon;
    }

    @Override
    public Coupon getCouponById(Integer couponId) {
        String sql = "SELECT coupon_id, code, description, discount_type, discount_value, start_date, end_date, is_active, quantity FROM coupon WHERE coupon_id = :couponId";
        Map<String, Object> map = new HashMap<>();
        map.put("couponId", couponId);

        List<Coupon> coupons = namedParameterJdbcTemplate.query(sql, map, new CouponRowMapper());
        if(!coupons.isEmpty()){
            return coupons.get(0);
        }
        return null;
    }

    @Override
    public Coupon updateCoupon(Integer couponId, Coupon coupon) {
        String sql = "UPDATE coupon SET code =:code, description =:description, discount_type =:discountType, discount_value =:discountValue, start_date =:startDate, end_date =:endDate, is_active =:isActive, quantity =:quantity WHERE coupon_id =:couponId";
        Map<String, Object> map = new HashMap<>();
        map.put("couponId", couponId);
        map.put("code", coupon.getCode());
        map.put("description", coupon.getDescription());
        map.put("discountType", coupon.getDiscountType().toString());
        map.put("discountValue", coupon.getDiscountValue());
        map.put("startDate", coupon.getStartDate());
        map.put("endDate", coupon.getEndDate());
        map.put("isActive", coupon.isActive());
        map.put("quantity", coupon.getQuantity());

        Integer updatedRows = namedParameterJdbcTemplate.update(sql, map);
        if (updatedRows > 0) {
            return getCouponById(couponId);
        } else {
            // 處理沒有找到對應優惠券進行更新的情況，例如拋出一個異常或返回 null
            return null;
        }
    }

    @Override
    public void deleteCoupon(Integer couponId) {
        String sql = "DELETE FROM coupon WHERE coupon_id = :couponId";
        Map<String, Object> map = new HashMap<>();
        map.put("couponId", couponId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        String sql = "SELECT coupon_id, code, description, discount_type, discount_value, start_date, end_date, is_active, quantity FROM coupon";
        Map<String, Object> map = new HashMap<>();
        List<Coupon> coupons = namedParameterJdbcTemplate.query(sql, map, new CouponRowMapper());
        if(!coupons.isEmpty()){
            return coupons;
        }
        return null;
    }
}
