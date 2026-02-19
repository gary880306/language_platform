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
import java.util.Objects;

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
        Integer couponId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getCouponById(couponId);
    }

    @Override
    public Coupon getCouponById(Integer couponId) {
        String sql = "SELECT coupon_id, code, description, discount_type, discount_value, start_date, end_date, is_active, quantity, is_deleted FROM coupon WHERE coupon_id = :couponId";
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
    public void markCouponAsDeleted(Integer couponId) {
        String sql = "UPDATE coupon SET is_deleted = :isDeleted WHERE coupon_id = :couponId";
        Map<String, Object> map = new HashMap<>();
        map.put("isDeleted", true);
        map.put("couponId", couponId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        String sql = "SELECT coupon_id, code, description, discount_type, discount_value, start_date, end_date, is_active, quantity, is_deleted FROM coupon";
        Map<String, Object> map = new HashMap<>();
        List<Coupon> coupons = namedParameterJdbcTemplate.query(sql, map, new CouponRowMapper());
        if(!coupons.isEmpty()){
            return coupons;
        }
        return null;
    }

    @Override
    public List<Coupon> getAllActiveCoupons() {
        // 在 SQL 查詢中加入條件以過濾掉標記為已刪除的優惠券
        String sql = "SELECT coupon_id, code, description, discount_type, discount_value, start_date, end_date, is_active, quantity, is_deleted FROM coupon WHERE is_deleted = false";

        Map<String, Object> map = new HashMap<>();
        List<Coupon> coupons = namedParameterJdbcTemplate.query(sql, map, new CouponRowMapper());

        // 檢查返回的列表是否為空
        return coupons.isEmpty() ? null : coupons;
    }

    @Override
    public List<Coupon> getAvailableActiveCoupons() {
        String sql = "SELECT coupon_id, code, description, discount_type, discount_value, start_date, end_date, is_active, quantity, is_deleted " +
                "FROM coupon " +
                "WHERE is_deleted = false AND is_active = true ";

        Map<String, Object> map = new HashMap<>();
        List<Coupon> coupons = namedParameterJdbcTemplate.query(sql, map, new CouponRowMapper());

        return coupons.isEmpty() ? null : coupons;
    }

    @Override
    public boolean checkCouponExists(Integer userId, Integer couponId) {
        String sql = "SELECT COUNT(*) FROM user_coupon WHERE user_id = :userId AND coupon_id = :couponId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("couponId", couponId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean checkCouponExistsByIsUsed(Integer userId, Integer couponId) {
        String sql = "SELECT COUNT(*) FROM user_coupon WHERE user_id = :userId AND coupon_id = :couponId AND is_used = false";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("couponId", couponId);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public List<Coupon> getCouponsByUserId(Integer userId) {
        String sql = "SELECT c.coupon_id, c.code, c.description, c.discount_type, c.discount_value, c.start_date, c.end_date, c.is_active, c.quantity " +
                "FROM user_coupon uc " +
                "INNER JOIN coupon c ON uc.coupon_id = c.coupon_id " +
                "WHERE uc.user_id = :userId AND uc.is_used = false";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        return namedParameterJdbcTemplate.query(
                sql,
                map,
                (rs, rowNum) -> {
                    Coupon coupon = new Coupon();
                    coupon.setCouponId(rs.getInt("coupon_id"));
                    coupon.setCode(rs.getString("code"));
                    coupon.setDescription(rs.getString("description"));
                    coupon.setDiscountType(Coupon.DiscountType.valueOf(rs.getString("discount_type")));
                    coupon.setDiscountValue(rs.getBigDecimal("discount_value"));
                    coupon.setStartDate(rs.getDate("start_date"));
                    coupon.setEndDate(rs.getDate("end_date"));
                    coupon.setActive(rs.getBoolean("is_active"));
                    coupon.setQuantity(rs.getInt("quantity"));
                    return coupon;
                }
        );
    }

    @Override
    public void deleteUserCoupon(Integer userId, Integer couponId) {
        String sql = "DELETE FROM user_coupon WHERE user_id = :userId AND coupon_id = :couponId";
        Map<String, Object> map= new HashMap<>();
        map.put("userId", userId);
        map.put("couponId", couponId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void assignCouponsToUsers(List<Integer> userIds, List<Integer> couponIds) {
        String insertSql = "INSERT INTO user_coupon (user_id, coupon_id, is_used) VALUES (:userId, :couponId, FALSE) " +
                "ON DUPLICATE KEY UPDATE is_used = VALUES(is_used)";

        String checkSql = "SELECT COUNT(*) FROM user_coupon WHERE user_id = :userId AND coupon_id = :couponId";

        for (Integer userId : userIds) {
            for (Integer couponId : couponIds) {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", userId);
                params.put("couponId", couponId);

                // 检查记录是否存在
                Integer count = namedParameterJdbcTemplate.queryForObject(checkSql, params, Integer.class);

                // 如果记录已存在且is_used为TRUE，则更新，否则插入
                if (count != null && count > 0) {
                    namedParameterJdbcTemplate.update(insertSql, params);
                } else {
                    namedParameterJdbcTemplate.update("INSERT INTO user_coupon (user_id, coupon_id, is_used) VALUES (:userId, :couponId, FALSE)", params);
                }
            }
        }
    }

    @Override
    public boolean isCodeExists(String code) {
        String sql = "SELECT COUNT(*) FROM coupon WHERE code = :code";
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);

        int count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
        return count > 0;
    }

}
