package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.FrontendDao;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.FrontendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FrontendServiceImpl implements FrontendService {
    @Autowired
    private FrontendDao frontendDao;
    @Override
    public List<Course> getAllCourses(CourseQueryParams courseQueryParams) {
        return frontendDao.getAllCourses(courseQueryParams);
    }

    @Override
    public List<Course> getAllCoursesWithUserCount(CourseQueryParams courseQueryParams) {
        return frontendDao.getAllCoursesWithUserCount(courseQueryParams);
    }

    @Override
    public Integer getCoursesCount(CourseQueryParams courseQueryParams) {
        return frontendDao.getCoursesCount(courseQueryParams);
    }

    @Override
    public Map<Integer, Integer> getCourseUserCounts() {
        return frontendDao.getCourseUserCounts();
    }
}
