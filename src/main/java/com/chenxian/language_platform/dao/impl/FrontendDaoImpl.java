package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.FrontendDao;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.rowmapper.CourseRowMapper;
import com.chenxian.language_platform.rowmapper.CourseRowMapperIncludeUserCount;
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
        Map<String, Object> map = new HashMap<>();
        String sql = "SELECT course.course_id, course_name, category_name, image_url, `time`, price, teacher, description, created_date, last_modified_date, video_url, is_deleted ";

        if ("userCount".equals(courseQueryParams.getOrderBy())) {
            sql += ", COUNT(user_course.user_id) as user_count ";
        }

        sql += "FROM course LEFT JOIN category ON course.category_id = category.category_id ";

        if ("userCount".equals(courseQueryParams.getOrderBy())) {
            sql += "LEFT JOIN user_course ON course.course_id = user_course.course_id ";
        }

        sql += "WHERE 1=1";

        if (courseQueryParams.getSearch() != null && !courseQueryParams.getSearch().isEmpty()) {
            sql += " AND (course_name LIKE :search OR category_name LIKE :search)";
            map.put("search", "%" + courseQueryParams.getSearch() + "%");
        }

        if ("userCount".equals(courseQueryParams.getOrderBy())) {
            sql += " GROUP BY course.course_id";
        }

        if ("userCount".equals(courseQueryParams.getOrderBy())) {
            sql += " ORDER BY user_count " + courseQueryParams.getSort();
        } else {
            sql += " ORDER BY " + courseQueryParams.getOrderBy() + " " + courseQueryParams.getSort();
        }

        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit", courseQueryParams.getSize());
        map.put("offset", (courseQueryParams.getPage() - 1) * courseQueryParams.getSize());

        return namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());
    }


    @Override
    public List<Course> getAllCoursesWithUserCount(CourseQueryParams courseQueryParams) {
        String sql = "SELECT course.course_id, course.course_name, category.category_name, course.image_url, course.`time`, course.price, course.teacher, course.description, course.created_date, course.last_modified_date, course.video_url, course.is_deleted, COUNT(user_course.user_id) as user_count " +
                "FROM course LEFT JOIN category ON course.category_id = category.category_id " +
                "LEFT JOIN user_course ON course.course_id = user_course.course_id WHERE 1=1";

        Map<String, Object> params = new HashMap<>();
        params.put("limit", courseQueryParams.getSize());
        params.put("offset", (courseQueryParams.getPage() - 1) * courseQueryParams.getSize());

        if (courseQueryParams.getSearch() != null && !courseQueryParams.getSearch().isEmpty()) {
            sql += " AND (course_name LIKE :search OR category_name LIKE :search)";
            params.put("search", "%" + courseQueryParams.getSearch() + "%");
        }

        sql += " GROUP BY course.course_id, course.course_name, category.category_name, course.image_url, course.`time`, course.price, course.teacher, course.description, course.created_date, course.last_modified_date, course.video_url " +
                "ORDER BY user_count " + courseQueryParams.getSort() + " " +
                "LIMIT :limit OFFSET :offset";

        return namedParameterJdbcTemplate.query(sql, params, new CourseRowMapperIncludeUserCount());
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

    @Override
    public Map<Integer, Integer> getCourseUserCounts() {
        String sql = "SELECT course_id, COUNT(user_id) as user_count " +
                "FROM user_course " +
                "GROUP BY course_id";

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(), rs -> {
            HashMap<Integer, Integer> userCounts = new HashMap<>();
            while (rs.next()) {
                userCounts.put(rs.getInt("course_id"), rs.getInt("user_count"));
            }
            return userCounts;
        });
    }


}
