package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;

import java.util.List;

public interface FrontendService {
    List<Course> getAllCourses(CourseQueryParams courseQueryParams);
}
