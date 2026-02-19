package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.customize.CategoryUserCount;
import com.chenxian.language_platform.customize.CourseSalesData;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.OrderedInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 後台報表 REST API（管理員）
 */
@RestController
@RequestMapping("/api/admin/reports")
public class ReportApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private OrderedInfoService orderedInfoService;

    /** 取得銷售總覽報表 */
    @GetMapping
    public ResponseEntity<?> getReport() {
        List<CourseSalesData> salesData = courseService.getCourseSalesData();
        List<CategoryUserCount> categoryData = courseService.getCategoryUserCount();
        int totalRevenue = orderedInfoService.getTotalRevenue();
        int totalOrders = orderedInfoService.getTotalOrderCount();

        return ResponseEntity.ok(Map.of(
                "salesData", salesData,
                "categoryData", categoryData,
                "totalRevenue", totalRevenue,
                "totalOrders", totalOrders
        ));
    }

    /** 取得課程銷售排行 */
    @GetMapping("/sales")
    public ResponseEntity<List<CourseSalesData>> getSalesData() {
        return ResponseEntity.ok(courseService.getCourseSalesData());
    }

    /** 取得分類用戶統計 */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryUserCount>> getCategoryData() {
        return ResponseEntity.ok(courseService.getCategoryUserCount());
    }
}
