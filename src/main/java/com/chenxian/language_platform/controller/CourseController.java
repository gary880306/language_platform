package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> getCourse(@PathVariable Integer courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course != null) {
            return ResponseEntity.status(HttpStatus.OK).body(course);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody @Valid CourseRequest courseRequest) {
        Integer courseId = courseService.creatCourse(courseRequest);
        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @PutMapping("/courses/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable Integer courseId,
                                             @RequestBody @Valid CourseRequest courseRequest) {

        // 檢查 class 是否存在
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 修改課程資訊
        courseService.updateCourse(courseId, courseRequest);
        Course updatedCourse = courseService.getCourseById(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourseById(@PathVariable Integer courseId){
        courseService.deleteCourseById(courseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
