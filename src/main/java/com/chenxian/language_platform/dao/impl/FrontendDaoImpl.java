package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.FrontendDao;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.rowmapper.CourseRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FrontendDaoImpl implements FrontendDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public List<Course> getAllCourses(CourseQueryParams courseQueryParams) {
        String sql = "SELECT course_id , course_name, category_name, image_url, `time`, price, teacher, description, created_date, last_modified_date " +
                "FROM course LEFT JOIN category  on course.category_id = category.category_id " +
                "WHERE 1=1";
        Map<String, Object> map = new HashMap<>();

        if(courseQueryParams.getSearch() != null){
            sql = sql + " AND course_name || category_name LIKE :search";
            map.put("search","%" + courseQueryParams.getSearch() + "%");
        }

        sql = sql + " ORDER BY " + courseQueryParams.getOrderBy() + " " + courseQueryParams.getSort();

        List<Course> courseList = namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());
        return courseList;
    }
}
