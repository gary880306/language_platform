package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.FrontendDao;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.rowmapper.CourseRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
        String sql = "SELECT course_id, course_name, category_name, image_url, `time`, price, teacher, description, created_date, last_modified_date, video_url " +
                "FROM course LEFT JOIN category ON course.category_id = category.category_id WHERE 1=1";

        Map<String, Object> params = new HashMap<>();

        // 处理搜索
        if (courseQueryParams.getSearch() != null) {
            sql += " AND course_name || category_name LIKE :search";
            params.put("search", "%" + courseQueryParams.getSearch() + "%");
        }

        // 添加排序
        sql += " ORDER BY " + courseQueryParams.getOrderBy() + " " + courseQueryParams.getSort();

        // 添加分页
        sql += " LIMIT :limit OFFSET :offset";
        params.put("limit", courseQueryParams.getSize());
        params.put("offset", (courseQueryParams.getPage() - 1) * courseQueryParams.getSize());

        return namedParameterJdbcTemplate.query(sql, params, new CourseRowMapper());
    }

    @Override
    public Integer getCoursesCount(CourseQueryParams courseQueryParams) {
        String sql = "SELECT COUNT(*) FROM course LEFT JOIN category ON course.category_id = category.category_id WHERE 1=1";

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (courseQueryParams.getSearch() != null) {
            sql += " AND (course_name LIKE :search OR category_name LIKE :search)";
            params.addValue("search", "%" + courseQueryParams.getSearch() + "%");
        }

        // 如果有其他查询条件，继续在这里添加

        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }
}
