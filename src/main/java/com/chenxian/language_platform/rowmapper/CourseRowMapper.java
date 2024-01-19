package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Course;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class CourseRowMapper implements RowMapper<Course> {
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
        String creatDate = sd.format(rs.getTimestamp("created_date"));
        course.setCreatDate(creatDate);
        String lastModifiedDate =  sd.format(rs.getTimestamp("last_modified_date"));
        course.setVideoUrl(rs.getString("video_url"));
        course.setLastModifiedDate(lastModifiedDate);
        course.setIsDeleted(rs.getBoolean("is_deleted"));
        return course;
    }
}

