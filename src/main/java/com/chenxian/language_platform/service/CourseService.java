package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;

import java.util.List;

public interface CourseService {
    CourseRequest getCourseById(Integer courseId);
    List<Course> getAllCourses();
    Integer creatCourse(CourseRequest courseRequest);
    void updateCourse(Integer courseId, CourseRequest courseRequest);
    boolean deleteCourseById(Integer courseId);
}
