package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.DataService;
import com.chenxian.language_platform.service.FrontendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FrontendController {
    @Autowired
    private FrontendService frontendService;

    @Autowired
    private DataService dataService;
    @GetMapping("/enjoyLearning/courses")
    public String showCourses(Model model,
                              // 查詢條件
                              @RequestParam(required = false) String search,
                              // 排序
                              @RequestParam(defaultValue = "created_date") String orderBy,
                              @RequestParam(defaultValue = "desc") String sort
                              // 分頁
                                ) {
        CourseQueryParams courseQueryParams = new CourseQueryParams();
        courseQueryParams.setSearch(search);
        courseQueryParams.setOrderBy(orderBy);
        courseQueryParams.setSort(sort);
        List<Course> courses = frontendService.getAllCourses(courseQueryParams);
        List<CategoryData> categories = dataService.findAllCategoryData();
        model.addAttribute("courses", courses);
        model.addAttribute("categories",categories);
        return "user/courses/main";
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
