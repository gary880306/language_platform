package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.customize.CourseSalesData;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.DataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private DataService dataService;

    // 加載頁面時獲取所有課程資訊
    @GetMapping("/managementCourses")
    public String showCourses(Model model) {
        List<Course> courses = courseService.getAllActiveCourses(); // 獲取所有课程
        List<CategoryData> categories = dataService.findAllCategoryData();

        for (Course course : courses) {
            // 課程價格格式化
            String formattedPrice = formatCoursePrice(course.getPrice());
            course.setFormattedPrice(formattedPrice);
        }

        model.addAttribute("courses", courses);
        model.addAttribute("categories", categories);
        return "admin/managementCourses"; // 返回 Thymeleaf 模板
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourseById(@PathVariable Integer courseId){
        courseService.deleteCourseById(courseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/creatCourse")
    public String createCourse(@RequestParam("courseName") String courseName,
                               @RequestParam("categoryId") Integer categoryId,
                               @RequestParam("imageUrl") MultipartFile file,
                               @RequestParam("time") Double time,
                               @RequestParam("price") Integer price,
                               @RequestParam("teacher") String teacher,
                               @RequestParam("description") String description,
                               @RequestParam("videoUrl") String videoUrl,
                               RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            // 繼續您的文件處理邏輯...
            uploadFile(file);

            // 創建並設置 CourseRequest 對象
            CourseRequest course = new CourseRequest();
            course.setCourseName(courseName);
            course.setCategoryId(categoryId);
            course.setImageUrl(file);
            course.setTime(time);
            course.setPrice(price);
            course.setTeacher(teacher);
            course.setDescription(description);
            course.setVideoUrl(videoUrl);

            // 保存到資料庫
            courseService.creatCourse(course);

            // 添加一個臨時的屬性來顯示成功消息
            redirectAttributes.addFlashAttribute("successMessage", "課程添加成功");

            return "redirect:/admin/managementCourses";
        }

        return "redirect:/admin/managementCourses"; // 如果文件为空，直接重定向，不创建课程
    }

    // 下載到本地端 C槽的 uploadImages 資料夾
    private String uploadFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String directory = "C:\\images";
                Path path = Paths.get(directory);

                // 如果資料夾不存在，則創建
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                // 獲取文件名稱
                String filename = file.getOriginalFilename();
                Path filePath = path.resolve(filename);

                // 將文件儲存到系統
                // 使用 StandardCopyOption.REPLACE_EXISTING 來覆蓋存在的文件
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                return filePath.toString(); // 返回文件路徑
            } catch (IOException e) {
                e.printStackTrace();
                // 錯誤處理邏輯（可選）
            }
        }
        return null; // 如果沒有文件，返回null或者預設值
    }

    // 新增課程表單驗證
    @PostMapping("/validateCourseData")
    public ResponseEntity<?> validateCourseData(@ModelAttribute CourseRequest courseRequest) {
        Map<String, String> errors = new HashMap<>();

        // 手動驗證
        if (courseRequest.getCourseName() == null || courseRequest.getCourseName().trim().isEmpty()) {
            errors.put("courseName", "課程名稱不可為空");
        } else if (courseService.existsCourseName(courseRequest.getCourseName())) {
            errors.put("courseName", "課程名稱已存在");
        }

        if (courseRequest.getCategoryId() == null) {
            errors.put("categoryId", "類別不可為空");
        }

        if ((courseRequest.getImageUrl() == null || courseRequest.getImageUrl().isEmpty()) &&
                (courseRequest.getImageUrlString() == null || courseRequest.getImageUrlString().trim().isEmpty())) {
            errors.put("imageUrl", "課程圖片不可為空");
        }

        if (courseRequest.getTime() == null) {
            errors.put("time", "時數不可為空");
        } else if (courseRequest.getTime() < 0.5) {
            errors.put("time", "時數必須大於0.5小時");
        }

        if (courseRequest.getPrice() == null) {
            errors.put("price", "價格不可為空");
        } else if (courseRequest.getPrice() < 1) {
            errors.put("price", "價格需大於0");
        }


        if (courseRequest.getTeacher() == null || courseRequest.getTeacher().trim().isEmpty()) {
            errors.put("teacher", "老師名稱不可為空");
        }

        if (courseRequest.getVideoUrl() == null || courseRequest.getVideoUrl().trim().isEmpty()) {
            errors.put("videoUrl", "課程影片不可為空");
        }

        // 檢查是否有驗證錯誤
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 驗證通過的處理邏輯
        return ResponseEntity.ok(Collections.singletonMap("message", "Validation successful"));
    }



    // 修改課程表單驗證
    @PostMapping("/validateRevisedCourseData")
    public ResponseEntity<?> validateRevisedCourseData(@ModelAttribute CourseRequest courseRequest) {
        Map<String, String> errors = new HashMap<>();

        // 手動驗證課程名稱是否為空
        if (courseRequest.getCourseName() == null || courseRequest.getCourseName().trim().isEmpty()) {
            errors.put("courseName", "課程名稱不可為空");
        }

        // 手動驗證圖片是否已上傳
        if (courseRequest.getImageUrl() == null || courseRequest.getImageUrl().isEmpty()) {
            errors.put("imageUrl", "課程圖片不可為空");
        }

        // 手動驗證類別是否為空
        if (courseRequest.getCategoryId() == null) {
            errors.put("categoryId", "類別不可為空");
        }

        if (courseRequest.getTime() == null) {
            errors.put("time", "時數不可為空");
        } else if (courseRequest.getTime() < 0.5) {
            errors.put("time", "時數必須大於0.5小時");
        }

        if (courseRequest.getPrice() == null) {
            errors.put("price", "價格不可為空");
        } else if (courseRequest.getPrice() < 1) {
            errors.put("price", "價格需大於0");
        }

        if (courseRequest.getTeacher() == null || courseRequest.getTeacher().trim().isEmpty()) {
            errors.put("teacher", "老師名稱不可為空");
        }

        if (courseRequest.getVideoUrl() == null || courseRequest.getVideoUrl().trim().isEmpty()) {
            errors.put("videoUrl", "課程影片不可為空");
        }

        // 檢查是否有驗證錯誤
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 驗證通過的處理邏輯
        return ResponseEntity.ok(Collections.singletonMap("message", "Validation successful"));
    }

    // 格式化價格 (千分位)
    private String formatCoursePrice(Integer price) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return formatter.format(price);
    }

}