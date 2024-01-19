package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Course;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class CourseRowMapperIncludeUserCount implements RowMapper<Course> {
    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseName(rs.getString("course_name"));
        course.setCategoryName(rs.getString("category_name"));
        course.setTeacher(rs.getString("teacher"));
        course.setImageUrl(rs.getString("image_url"));
        course.setTime(rs.getDouble("time"));
        course.setPrice(rs.getInt("price"));
        course.setDescription(rs.getString("description"));

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (rs.getTimestamp("created_date") != null) {
            String createdDate = sd.format(rs.getTimestamp("created_date"));
            course.setCreatDate(createdDate);
        }
        if (rs.getTimestamp("last_modified_date") != null) {
            String lastModifiedDate = sd.format(rs.getTimestamp("last_modified_date"));
            course.setLastModifiedDate(lastModifiedDate);
        }

        course.setVideoUrl(rs.getString("video_url"));
        course.setIsDeleted(rs.getBoolean("is_deleted"));
        // 新增用戶數量字段
        // 請確保 'user_count' 列在 SQL 查詢中被選擇
        if (rs.getObject("user_count") != null) {
            course.setUserCount(rs.getInt("user_count"));
        } else {
            course.setUserCount(0);
        }

        return course;
    }
}
