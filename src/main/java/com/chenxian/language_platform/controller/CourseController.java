package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.DataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private DataService dataService;

//    @GetMapping("/courses/{courseId}")
//    public ResponseEntity<Course> getCourse(@PathVariable Integer courseId) {
//        Course course = courseService.getCourseById(courseId);
//        if (course != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(course);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }

    @GetMapping("/admin")
    public String showCourses(Model model) {
        List<Course> courses = courseService.getAllCourses(); // 獲取所有課程
        List<CategoryData> categories = dataService.findAllCategoryData();
        model.addAttribute("courses", courses);
        model.addAttribute("categories",categories);
        return "admin"; // 返回您的 Thymeleaf 模板名稱
    }

//    @PutMapping("/courses/{courseId}")
//    public ResponseEntity<Course> updateCourse(@PathVariable Integer courseId,
//                                               @RequestBody @Valid CourseRequest courseRequest) {
//
//        // 檢查 class 是否存在
//        Course course = courseService.getCourseById(courseId);
//        if (course == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        // 修改課程資訊
//        courseService.updateCourse(courseId, courseRequest);
//        Course updatedCourse = courseService.getCourseById(courseId);
//        return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
//    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourseById(@PathVariable Integer courseId){
        courseService.deleteCourseById(courseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/courses")
    public String createCourse(@RequestParam("courseName") String courseName,
                               @RequestParam("categoryId") Integer categoryId,
                               @RequestParam("imageUrl") MultipartFile file,
                               @RequestParam("time") Double time,
                               @RequestParam("price") Integer price,
                               @RequestParam("teacher") String teacher,
                               @RequestParam("description") String description,
                               RedirectAttributes redirectAttributes) {

        // 處理上傳的文件
        String imageUrl = uploadFile(file);

        // 創建並設置 Course 對象
        CourseRequest course = new CourseRequest();
        course.setCourseName(courseName);
        course.setCategoryId(categoryId);
        course.setImageUrl(imageUrl);
        course.setTime(time);
        course.setPrice(price);
        course.setTeacher(teacher);
        course.setDescription(description);

        // 保存到資料庫
        courseService.creatCourse(course);

        // 添加一個臨時的屬性來顯示成功消息
        redirectAttributes.addFlashAttribute("successMessage", "課程添加成功");

        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}")
    public String editCourse(@PathVariable("id") Long id, Model model) {
        // 根据id获取课程信息
        // 添加到model
        return "editCourse"; // 返回编辑课程的视图
    }
    public String uploadFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return file.getOriginalFilename();  // 獲取原始檔名
        }
        return null;  // 或返回一個預設檔名，如果需要的話
    }

}
