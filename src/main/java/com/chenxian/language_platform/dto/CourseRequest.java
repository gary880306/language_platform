package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public MultipartFile getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(MultipartFile imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrlString() {
        return imageUrlString;
    }

    public void setImageUrlString(String imageUrlString) {
        this.imageUrlString = imageUrlString;
    }
}
