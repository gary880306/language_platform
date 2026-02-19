package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.DataService;
import com.chenxian.language_platform.service.FrontendService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 課程 REST API
 * 公開端點：GET /api/courses/**
 * 管理端點：/api/admin/courses/**（需管理員 JWT）
 */
@RestController
public class CourseApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private FrontendService frontendService;

    @Autowired
    private DataService dataService;

    // ==================== 公開 API ====================

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

        List<Course> courses = frontendService.getCourses(params);
        int total = frontendService.countCourses(params);

        return ResponseEntity.ok(Map.of(
                "courses", courses,
                "total", total,
                "page", page,
                "size", size,
                "totalPages", (int) Math.ceil((double) total / size)
        ));
    }

    /** 取得單一課程詳細資訊 */
    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable Integer courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(course);
    }

    /** 取得所有分類 */
    @GetMapping("/api/categories")
    public ResponseEntity<List<CategoryData>> getCategories() {
        return ResponseEntity.ok(dataService.getCategories());
    }

    // ==================== 管理員 API ====================

    /** 取得所有課程（管理後台用） */
    @GetMapping("/api/admin/courses")
    public ResponseEntity<?> adminGetCourses() {
        List<Course> courses = courseService.getFormattedCourses();
        List<CategoryData> categories = dataService.getCategories();
        return ResponseEntity.ok(Map.of("courses", courses, "categories", categories));
    }

    /** 新增課程 */
    @PostMapping("/api/admin/courses")
    public ResponseEntity<?> createCourse(
            @RequestParam("courseName") String courseName,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("imageUrl") MultipartFile file,
            @RequestParam("time") Double time,
            @RequestParam("price") Integer price,
            @RequestParam("teacher") String teacher,
            @RequestParam("description") String description,
            @RequestParam("videoUrl") String videoUrl) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "課程圖片不可為空"));
        }

        CourseRequest course = new CourseRequest();
        course.setCourseName(courseName);
        course.setCategoryId(categoryId);
        course.setImageUrl(file);
        course.setTime(time);
        course.setPrice(price);
        course.setTeacher(teacher);
        course.setDescription(description);
        course.setVideoUrl(videoUrl);

        courseService.creatCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "課程新增成功"));
    }

    /** 取得單一課程資料（供編輯用） */
    @GetMapping("/api/admin/courses/{courseId}")
    public ResponseEntity<?> getAdminCourseById(@PathVariable Integer courseId) {
        CourseRequest course = courseService.getCourseRequestById(courseId);
        if (course == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(course);
    }

    /** 更新課程 */
    @PutMapping("/api/admin/courses/{courseId}")
    public ResponseEntity<?> updateCourse(
            @PathVariable Integer courseId,
            @ModelAttribute CourseRequest courseRequest) {
        courseService.updateCourse(courseId, courseRequest);
        return ResponseEntity.ok(Map.of("message", "課程更新成功"));
    }

    /** 刪除課程（軟刪除） */
    @DeleteMapping("/api/admin/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Integer courseId) {
        courseService.deleteCourseById(courseId);
        return ResponseEntity.noContent().build();
    }

    /** 驗證新增課程表單資料 */
    @PostMapping("/api/admin/courses/validate")
    public ResponseEntity<?> validateCourseData(@ModelAttribute CourseRequest courseRequest) {
        Map<String, String> errors = new HashMap<>();

        if (courseRequest.getCourseName() == null || courseRequest.getCourseName().isBlank()) {
            errors.put("courseName", "課程名稱不可為空");
        } else if (courseService.existsCourseName(courseRequest.getCourseName())) {
            errors.put("courseName", "課程名稱已存在");
        }
        if (courseRequest.getCategoryId() == null) errors.put("categoryId", "類別不可為空");
        if (courseRequest.getImageUrl() == null || courseRequest.getImageUrl().isEmpty()) {
            errors.put("imageUrl", "課程圖片不可為空");
        }
        if (courseRequest.getTime() == null) errors.put("time", "時數不可為空");
        else if (courseRequest.getTime() < 0.5) errors.put("time", "時數必須大於0.5小時");
        if (courseRequest.getPrice() == null) errors.put("price", "價格不可為空");
        else if (courseRequest.getPrice() < 1) errors.put("price", "價格需大於0");
        if (courseRequest.getTeacher() == null || courseRequest.getTeacher().isBlank()) {
            errors.put("teacher", "老師名稱不可為空");
        }
        if (courseRequest.getVideoUrl() == null || courseRequest.getVideoUrl().isBlank()) {
            errors.put("videoUrl", "課程影片不可為空");
        }

        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        return ResponseEntity.ok(Map.of("message", "驗證通過"));
    }
}
