package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class CourseRequest {
    // 需要前端傳過來的參數
    private String courseName;
    private Integer categoryId;
    private MultipartFile imageUrl;
    private String imageUrlString;
    private Double time;
    private Integer price;
    private String teacher;
    private String description;
    private String videoUrl;

}
