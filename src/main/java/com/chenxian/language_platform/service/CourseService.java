package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;

public interface CourseService {
    Course getCourseById(Integer courseId);

    Integer creatCourse(CourseRequest courseRequest);
    void updateCourse(Integer courseId, CourseRequest courseRequest);
    void deleteCourseById(Integer courseId);
}
