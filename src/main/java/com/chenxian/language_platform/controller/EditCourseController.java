package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.OrderItem;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.OrderedInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EditCourseController {
    @Autowired
    private OrderedInfoService orderedInfoService;

    @Autowired
    private CourseService courseService;

    // 修改時獲取表單相關資料
    @GetMapping("/admin/managementCourses/info")
    public ResponseEntity<CourseRequest> getCourseData(@RequestParam("courseId") Integer courseId) {
        CourseRequest course = courseService.getCourseById(courseId);
        if (course != null) {
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 刪除課程的方法
    @DeleteMapping("/admin/managementCourses/delete")
    public ResponseEntity<?> deleteCourse(@RequestParam("courseId") Integer courseId) {
        boolean isDeleted = courseService.deleteCourseById(courseId);

        if (isDeleted) {
            return ResponseEntity.ok().build(); // 回應 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 回應 404 Not Found
        }
    }

    @PutMapping("/managementCourses/put/{courseId}")
    public String putCourse(@PathVariable("courseId") Integer courseId,
                            @ModelAttribute CourseRequest courseRequest) {
        courseService.updateCourse(courseId,courseRequest);

        return "redirect:/admin/managementCourses";
    }


}
