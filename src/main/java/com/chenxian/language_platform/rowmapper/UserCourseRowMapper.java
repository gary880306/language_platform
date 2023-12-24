package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.UserCourse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCourseRowMapper implements RowMapper<UserCourse> {
    @Override
    public UserCourse mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(rs.getInt("user_id"));
        userCourse.setCourseId(rs.getInt("course_id"));
        userCourse.setPurchaseDate(rs.getTimestamp("purchase_date"));
        return userCourse;
    }
}
