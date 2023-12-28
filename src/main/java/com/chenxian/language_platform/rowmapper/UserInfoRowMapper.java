package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setUserName(rs.getString("user_name"));
        user.setBirth(rs.getString("birth"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAddress(rs.getString("address"));
        user.setCreatedDate(rs.getTimestamp("created_date"));
        user.setLastModifiedDate(rs.getTimestamp("last_modified_date"));
        user.setLevelId(rs.getInt("levelId"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
