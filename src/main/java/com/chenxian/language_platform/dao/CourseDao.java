package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;

public interface CourseDao {
    Course getCourseById(Integer courseId);
    Integer creatCourse(CourseRequest courseRequest);
    void updateCourse(Integer courseId,CourseRequest courseRequest);
    void deleteCourseById(Integer courseId);
}
