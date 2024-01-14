package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;

import java.util.List;
import java.util.Map;

public interface FrontendDao {
    List<Course> getAllCourses(CourseQueryParams courseQueryParams);
    List<Course> getAllCoursesWithUserCount(CourseQueryParams courseQueryParams);
    Integer getCoursesCount(CourseQueryParams courseQueryParams);
    Map<Integer, Integer> getCourseUserCounts();
}
