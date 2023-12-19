package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;

import java.util.List;

public interface FrontendDao {
    List<Course> getAllCourses(CourseQueryParams courseQueryParams);
}
