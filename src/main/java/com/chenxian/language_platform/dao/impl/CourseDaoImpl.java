package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.customize.CategoryUserCount;
import com.chenxian.language_platform.customize.CourseSalesData;
import com.chenxian.language_platform.dao.CourseDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.model.UserCourse;
import com.chenxian.language_platform.rowmapper.CourseRequestRowMapper;
import com.chenxian.language_platform.rowmapper.CourseRowMapper;
import com.chenxian.language_platform.rowmapper.UserCourseRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CourseDaoImpl implements CourseDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public CourseRequest getCourseById(Integer courseId) {
        String sql = "SELECT c.course_id, c.course_name, c.category_id, cat.category_name, c.image_url, c.time, c.price, c.teacher, c.description, c.created_date, c.last_modified_date, c.video_url " +
                "FROM course c " +
                "JOIN category cat ON c.category_id = cat.category_id " +
                "WHERE c.course_id = :courseId ";
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);

        List<CourseRequest> courseList = namedParameterJdbcTemplate.query(sql, map, new CourseRequestRowMapper());

        if (!courseList.isEmpty()) {
            return courseList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Course getCourseByCourseId(Integer courseId) {
        String sql = "SELECT course.course_id, course.course_name, course.category_id, category.category_name, course.image_url, course.`time`, course.price, course.teacher, course.description, course.created_date, course.last_modified_date, course.video_url, course.is_deleted "
                + "FROM course "
                + "JOIN category ON course.category_id = category.category_id "
                + "WHERE course.course_id = :courseId";

        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);

        List<Course> courseList = namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());

        if (!courseList.isEmpty()) {
            return courseList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Course getCoursesByIdForCart(Integer courseId) {
        String sql = "SELECT course_id, course_name, category_name, image_url, `time`, price, teacher, description, created_date, last_modified_date, video_url ,is_deleted " +
                "FROM course LEFT JOIN category on course.category_id = category.category_id " +
                "WHERE course.course_id = :courseId";
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        List<Course> courseList = namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());
        return courseList.get(0);
    }

    @Override
    public List<Course> getAllCourses() {
        String sql = "SELECT course_id , course_name, category_name, image_url, `time`, price, teacher, description, created_date, last_modified_date, video_url FROM course LEFT JOIN category  on course.category_id = category.category_id";
        Map<String, Object> map = new HashMap<>();
        List<Course> courseList = namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());
        return courseList;
    }

    @Override
    public List<Course> getAllActiveCourses() {
        String sql = "SELECT course_id, course_name, category_name, image_url, `time`, price, teacher, description, created_date, last_modified_date, video_url, is_deleted "
                + "FROM course "
                + "LEFT JOIN category ON course.category_id = category.category_id "
                + "WHERE course.is_deleted = FALSE";
        Map<String, Object> map = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, map, new CourseRowMapper());
    }

    @Override
    public List<UserCourse> getPurchasedCourses(Integer userId) {
        String sql = "SELECT user_id,course_id,purchase_date FROM user_course WHERE user_id =:userId;";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        return namedParameterJdbcTemplate.query(sql,map,new UserCourseRowMapper());
    }

    @Override
    public Integer creatCourse(CourseRequest courseRequest) {
        String sql = "INSERT INTO course (course_name, category_id, image_url, time, price, teacher, description, created_date, last_modified_date, video_url) VALUES (:courseName, :categoryId, :imageUrl, :time, :price, :teacher, :description, :createdDate, :lastModifiedDate, :videoUrl)";

        Map<String,Object> map = new HashMap<>();
        map.put("courseName",courseRequest.getCourseName());
        map.put("categoryId",courseRequest.getCategoryId());
        map.put("imageUrl",courseRequest.getImageUrl().getOriginalFilename());
        map.put("time",courseRequest.getTime());
        map.put("price",courseRequest.getPrice());
        map.put("teacher",courseRequest.getTeacher());
        map.put("description",courseRequest.getDescription());
        map.put("videoUrl",courseRequest.getVideoUrl());
        Date now = new Date();
        map.put("createdDate",now);
        map.put("lastModifiedDate",now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map),keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void updateCourse(Integer courseId, CourseRequest courseRequest) {
        String sql = "UPDATE course SET course_name =:courseName,category_id =:categoryId,image_url =:imageUrl,time=:time,price=:price,teacher=:teacher,description=:description,last_modified_date=:lasModifiedDate,video_url=:videoUrl WHERE course_id =:courseId";
        Map<String,Object> map = new HashMap<>();
        map.put("courseId",courseId);
        map.put("courseName",courseRequest.getCourseName());
        map.put("categoryId",courseRequest.getCategoryId());
        map.put("imageUrl",courseRequest.getImageUrl().getOriginalFilename());
        map.put("time",courseRequest.getTime());
        map.put("price",courseRequest.getPrice());
        map.put("teacher",courseRequest.getTeacher());
        map.put("description",courseRequest.getDescription());
        map.put("videoUrl",courseRequest.getVideoUrl());
        map.put("lasModifiedDate",new Date());
        namedParameterJdbcTemplate.update(sql,map);
    }

    @Override
    public boolean deleteCourseById(Integer courseId) {
        String sql = "UPDATE course SET is_deleted = TRUE WHERE course_id = :courseId";
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        namedParameterJdbcTemplate.update(sql, map);
        return true;
    }

    @Override
    public boolean hasUserPurchasedCourse(Integer userId, Integer courseId) {
        String sql = "SELECT user_id,course_id,purchase_date FROM user_course WHERE user_id =:userId AND course_id =:courseId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("courseId",courseId);
        List<UserCourse> userCourseList = namedParameterJdbcTemplate.query(sql,map,new UserCourseRowMapper());
        if(userCourseList.size() > 0){
            userCourseList.get(0);
            return true;
        }
        return false;
    }

    @Override
    public List<CourseSalesData> findCourseSalesData() {
        String sql = "SELECT c.course_name, COUNT(uc.user_id) AS purchase_count FROM user_course uc JOIN course c ON uc.course_id = c.course_id GROUP BY c.course_name";

        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> new CourseSalesData(rs.getString("course_name"), rs.getInt("purchase_count")));
    }

    @Override
    public List<CategoryUserCount> findCategoryUserCounts() {
        String sql = "SELECT cat.category_name, COUNT(uc.user_id) AS user_count "
                + "FROM user_course uc "
                + "JOIN course co ON uc.course_id = co.course_id "
                + "JOIN category cat ON co.category_id = cat.category_id "
                + "GROUP BY cat.category_name";

        return namedParameterJdbcTemplate.query(
                sql,
                (rs, rowNum) -> new CategoryUserCount(
                        rs.getString("category_name"),
                        rs.getInt("user_count")
                )
        );
    }
}
