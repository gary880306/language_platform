package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.model.UserCourse;

import java.util.List;

public interface CourseService {
    CourseRequest getCourseById(Integer courseId);
    Course getCoursesByIdForCart(Integer courseId);
    List<Course> getAllCourses();
    List<UserCourse> getPurchasedCourses(Integer userId);
    Integer creatCourse(CourseRequest courseRequest);
    void updateCourse(Integer courseId, CourseRequest courseRequest);
    boolean deleteCourseById(Integer courseId);
    boolean hasUserPurchasedCourse(Integer userId,Integer courseId);
}
