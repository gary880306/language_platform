package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.util.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 圖片上傳/取得 REST API
 */
@RestController
@RequestMapping("/api/images")
public class ImageApiController {

    @Autowired
    private ImageService imageService;

    /** 上傳圖片（回傳圖片 ID 或 URL） */
    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "請選擇檔案"));
        }
        try {
            String imageId = imageService.uploadImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageId", imageId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "圖片上傳失敗"));
        }
    }

    /** 取得圖片（以 ID 查詢） */
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        try {
            byte[] imageData = imageService.getImageById(imageId);
            if (imageData == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
