package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class CourseRequest {
    // 需要前端傳過來的參數
    @NotEmpty(message = "課程名稱不可為空")
    private String courseName;
    @NotNull(message = "類別不可為空")
    private Integer categoryId;
    private MultipartFile imageUrl;
    private String imageUrlString;
    @NotNull(message = "時數不可為空")
    @DecimalMin(value = "0.5", message = "時數必須大於0.5小時")
    private Double time;
    @NotNull(message = "價格不可為空")
    @Min(value = 1, message = "價格需大於0")
    private Integer price;
    @NotEmpty(message = "老師名稱不可為空")
    private String teacher;
    private String description;
    @NotEmpty(message = "課程影片不可為空")
    private String videoUrl;

}
