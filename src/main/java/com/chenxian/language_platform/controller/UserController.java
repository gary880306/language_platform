package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/courseType")
public class UserController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private DataService dataService;
    @GetMapping("/englishCourses")
    public String showCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        List<CategoryData> categories = dataService.findAllCategoryData();
        model.addAttribute("courses", courses);
        model.addAttribute("categories",categories);
        return "/user/courseType/englishCourses";
    }


//    @GetMapping("/managementCourses")
//    public String showCourses(Model model) {
//        List<Course> courses = courseService.getAllCourses(); // 獲取所有課程
//        List<CategoryData> categories = dataService.findAllCategoryData();
//        model.addAttribute("courses", courses);
//        model.addAttribute("categories",categories);
//        return "admin/managementCourses"; // 返回您的 Thymeleaf 模板名稱
//    }
}
