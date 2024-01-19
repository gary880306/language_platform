package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class CourseRequest {
    @NotEmpty(message = "course.name.nonempty")
    @Size(min = 1, message = "course.name.size")
    // 需要前端傳過來的參數
    private String courseName;
    @NotNull(message = "course.categoryId.notnull")
    private Integer categoryId;
    private MultipartFile imageUrl;
    private String imageUrlString;
    @NotNull(message = "course.time.notnull")
    @DecimalMin(value = "0.5", message = "時數必須大於0.5小時")
    private Double time;
    @NotNull(message = "course.price.notnull")
    @Min(value = 1, message = "價格需大於0")
    private Integer price;
    @NotEmpty(message = "course.teacher.nonempty")
    @Size(min = 1, message = "course.teacher.size")
    private String teacher;
    private String description;

    private String videoUrl;

}
