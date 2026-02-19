package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.DataService;
import com.chenxian.language_platform.service.FrontendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公開課程 API (SRP: 只負責公開課程瀏覽，不含管理功能)
 * 所有端點無需 JWT 認證
 */
@RestController
public class PublicCourseController {

    @Autowired
    private FrontendService frontendService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DataService dataService;

    /** 取得課程列表（含分頁、搜尋、篩選） */
    @GetMapping("/api/courses")
    public ResponseEntity<?> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "createdDate") String orderBy,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        CourseQueryParams params = new CourseQueryParams();
        params.setSearch(keyword);
        params.setCategoryName(categoryName);
        params.setOrderBy(orderBy);
        params.setSort(sort);
        params.setPage(page);
        params.setSize(size);

        List<Course> courses = frontendService.getAllCourses(params);
        int total = frontendService.getCoursesCount(params);

        return ResponseEntity.ok(Map.of(
                "courses", courses != null ? courses : List.of(),
                "total", total,
                "page", page,
                "size", size,
                "totalPages", (int) Math.ceil((double) total / size)
        ));
    }

    /** 取得單一課程詳細資訊 */
    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable Integer courseId) {
        Course course = courseService.getCourseByCourseId(courseId);
        if (course == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(course);
    }

    /** 取得所有分類 */
    @GetMapping("/api/categories")
    public ResponseEntity<List<CategoryData>> getCategories() {
        return ResponseEntity.ok(dataService.getCategories());
    }
}
