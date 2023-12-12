package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.CourseDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.rowmapper.CourseRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CourseDaoImpl implements CourseDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Course getCourseById(Integer courseId) {
        String sql = "SELECT course_id , course_name, category, image_url, price, description, created_date, last_modified_date FROM course WHERE course_id=:courseId";
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);

        List<Course> courseList = namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());

        if (courseList.size() > 0) {
            return courseList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public Integer creatCourse(CourseRequest courseRequest) {
        String sql = "INSERT INTO course (course_name, category, image_url, price, description, created_date, last_modified_date) VALUES (:courseName,:category,:imageUrl,:price,:description,:createdDate,:lastModifiedDate)";
        Map<String,Object> map = new HashMap<>();
        map.put("courseName",courseRequest.getCourseName());
        map.put("category",courseRequest.getCategory().toString());
        map.put("imageUrl",courseRequest.getImageUrl());
        map.put("price",courseRequest.getPrice());
        map.put("description",courseRequest.getDescription());

        Date now = new Date();
        map.put("createdDate",now);
        map.put("lastModifiedDate",now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map),keyHolder);
        int courseId = keyHolder.getKey().intValue();

        return courseId;
    }

    @Override
    public void updateCourse(Integer courseId, CourseRequest courseRequest) {
        String sql = "UPDATE course SET course_id =:courseId,course_name =:courseName,category =:category,image_url =:imageUrl,price=:price,description=:description,last_modified_date=:lasModifiedDate WHERE course_id =:courseId";
        Map<String,Object> map = new HashMap<>();
        map.put("courseId",courseId);
        map.put("courseName",courseRequest.getCourseName());
        map.put("category",courseRequest.getCategory().toString());
        map.put("imageUrl",courseRequest.getImageUrl());
        map.put("price",courseRequest.getPrice());
        map.put("description",courseRequest.getDescription());

        map.put("lasModifiedDate",new Date());
        namedParameterJdbcTemplate.update(sql,map);
    }

    @Override
    public void deleteCourseById(Integer courseId) {
        String sql = "DELETE FROM course WHERE course_id=:courseId";
        Map<String,Object> map = new HashMap<>();
        map.put("courseId",courseId);
        namedParameterJdbcTemplate.update(sql,map);
    }
}
