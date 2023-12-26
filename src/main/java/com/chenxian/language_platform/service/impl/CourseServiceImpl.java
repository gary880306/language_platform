package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.CourseDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.model.UserCourse;
import com.chenxian.language_platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseDao courseDao;
    @Override
    public CourseRequest getCourseById(Integer courseId) {
        return courseDao.getCourseById(courseId);
    }

    @Override
    public Course getCoursesByIdForCart(Integer courseId) {
        return courseDao.getCoursesByIdForCart(courseId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.getAllCourses();
    }

    @Override
    public List<UserCourse> getPurchasedCourses(Integer userId) {
        return courseDao.getPurchasedCourses(userId);
    }

    @Override
    public Integer creatCourse(CourseRequest courseRequest) {
        return courseDao.creatCourse(courseRequest);
    }

    @Override
    public void updateCourse(Integer courseId, CourseRequest courseRequest) {
        courseDao.updateCourse(courseId,courseRequest);
    }

    @Override
    public boolean deleteCourseById(Integer courseId) {
        return courseDao.deleteCourseById(courseId);
    }

    @Override
    public boolean hasUserPurchasedCourse(Integer userId, Integer courseId) {
        return courseDao.hasUserPurchasedCourse(userId,courseId);
    }
}
