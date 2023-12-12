package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.constant.CourseCategory;
import com.chenxian.language_platform.model.Course;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRowMapper implements RowMapper<Course> {
    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseName(rs.getString("course_name"));
        String categoryStr = rs.getString("category");
        CourseCategory category = CourseCategory.valueOf(categoryStr);
        course.setCategory(category);
        course.setImageUrl(rs.getString("image_url"));
        course.setPrice(rs.getInt("price"));
        course.setDescription(rs.getString("description"));
        course.setCreatDate(rs.getTimestamp("created_date"));
        course.setLastModifiedDate(rs.getTimestamp("last_modified_date"));
        return course;
    }
}
