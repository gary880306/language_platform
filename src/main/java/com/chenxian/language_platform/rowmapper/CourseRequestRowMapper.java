package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRequestRowMapper implements RowMapper<CourseRequest> {
    @Override
    public CourseRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        CourseRequest course = new CourseRequest();
        course.setCourseName(rs.getString("course_name"));
        course.setCategoryId(rs.getInt("category_id"));
        course.setImageUrlString(rs.getString("image_url"));
        course.setTime(rs.getDouble("time"));
        course.setPrice(rs.getInt("price"));
        course.setTeacher(rs.getString("teacher"));
        course.setDescription(rs.getString("description"));
        return course;
    }
}