package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;

import java.util.List;
import java.util.Map;

public interface FrontendService {
    List<Course> getAllCourses(CourseQueryParams courseQueryParams);
    List<Course> getAllCoursesWithUserCount(CourseQueryParams courseQueryParams);
    Integer getCoursesCount(CourseQueryParams courseQueryParams);
    Map<Integer, Integer> getCourseUserCounts();
}
