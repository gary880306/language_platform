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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        List<Course> courses = courseService.getAllCourses(); // 獲取所有課程
        List<CategoryData> categories = dataService.findAllCategoryData();
        model.addAttribute("courses", courses);
        model.addAttribute("categories",categories);
        return "admin/managementCourses"; // 返回您的 Thymeleaf 模板名稱
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
                String directory = "C:\\uploadImages";
                Path path = Paths.get(directory);

                // 如果資料夾不存在，則創建
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                // 獲取文件名稱
                String filename = file.getOriginalFilename();
                Path filePath = path.resolve(filename);

                // 檢查是否已經存在相同的檔案，如果存在，可以在文件名上做一些更改，以區別它們
                int counter = 1;
                while (Files.exists(filePath)) {
                    String baseFilename = filename.substring(0, filename.lastIndexOf('.'));
                    String extension = filename.substring(filename.lastIndexOf('.'));
                    String newFilename = baseFilename + "_" + counter + extension;
                    filePath = path.resolve(newFilename);
                    counter++;
                }

                // 將文件儲存到系統
                Files.copy(file.getInputStream(), filePath);

                return filePath.toString(); // 返回文件路徑
            } catch (IOException e) {
                e.printStackTrace();
                // 錯誤處理邏輯（可選）
            }
        }
        return null; // 如果沒有文件，返回null或者預設值
    }

    @PostMapping("/validateCourseData")
    public ResponseEntity<?> validateCourseData(@ModelAttribute @Valid CourseRequest courseRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }

        // 驗證通過的處理邏輯
       return ResponseEntity.ok(Collections.singletonMap("message", "Validation successful"));
    }

    @PostMapping("/validateRevisedCourseData")
    public ResponseEntity<?> validateRevisedCourseData(@ModelAttribute @Valid CourseRequest courseRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }

        // 驗證通過的處理邏輯
        return ResponseEntity.ok(Collections.singletonMap("message", "Validation successful"));
    }

}
