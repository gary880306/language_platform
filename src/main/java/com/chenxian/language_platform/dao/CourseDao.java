package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;

import java.util.List;

public interface CourseDao {
    CourseRequest getCourseById(Integer courseId);
    List<Course> getAllCourses();
    Integer creatCourse(CourseRequest courseRequest);
    void updateCourse(Integer courseId,CourseRequest courseRequest);
    void deleteCourseById(Integer courseId);
}
