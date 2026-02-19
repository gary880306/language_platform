package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.customize.CategoryUserCount;
import com.chenxian.language_platform.customize.CourseSalesData;
import com.chenxian.language_platform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ReportController {
    @Autowired
    private CourseService courseService;
    // 取得所有銷售圖表
    @GetMapping("/admin/reportInfo")
    public String showAllReport(Model model){
        List<CourseSalesData> salesData = courseService.findCourseSalesData();
        List<CategoryUserCount> categoryUserCounts = courseService.findCategoryUserCounts();

        model.addAttribute("salesData", salesData);
        model.addAttribute("categoryUserCounts", categoryUserCounts);
        return "admin/reportInfo";


    }

}
