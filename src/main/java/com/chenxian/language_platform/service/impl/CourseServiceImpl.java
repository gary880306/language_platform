package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.CourseDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseDao courseDao;
    @Override
    public Course getCourseById(Integer courseId) {
        return courseDao.getCourseById(courseId);
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
    public void deleteCourseById(Integer courseId) {
        courseDao.deleteCourseById(courseId);
    }
}
