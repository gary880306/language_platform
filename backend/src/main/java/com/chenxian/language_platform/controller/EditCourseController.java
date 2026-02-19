package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.OrderItem;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.OrderedInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        // 處理文件上傳
        MultipartFile file = courseRequest.getImageUrl(); // 假設 CourseRequest 有一個 getImageFile 方法
        String filePath = uploadFile(file);

        // 您可以根據需要將文件路徑保存到 courseRequest 或其他地方
        if (filePath != null) {
            courseRequest.setImageUrlString(filePath); // 假設 CourseRequest 有一個 setImageUrl 方法
        }

        // 更新課程資料
        courseService.updateCourse(courseId, courseRequest);

        return "redirect:/admin/managementCourses";
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


}
