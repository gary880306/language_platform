package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.customize.CategoryUserCount;
import com.chenxian.language_platform.customize.CourseSalesData;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.model.UserCourse;

import java.util.List;
import java.util.Optional;

public interface CourseDao {
    CourseRequest getCourseById(Integer courseId);
    Course getCourseByCourseId(Integer courseId);
    Course getCoursesByIdForCart(Integer courseId);
    List<Course> getAllCourses();
    List<Course> getAllActiveCourses();
    List<UserCourse> getPurchasedCourses(Integer userId);
    Integer creatCourse(CourseRequest courseRequest);
    void updateCourse(Integer courseId,CourseRequest courseRequest);
    // 課程軟刪除
    boolean deleteCourseById(Integer courseId);
    boolean hasUserPurchasedCourse(Integer userId,Integer courseId);
    List<CourseSalesData> findCourseSalesData();
    List<CategoryUserCount> findCategoryUserCounts();
    boolean existsCourseName(String name);
}
