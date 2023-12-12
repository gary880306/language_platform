package com.chenxian.language_platform.dto;

import com.chenxian.language_platform.constant.CourseCategory;
import jakarta.validation.constraints.NotNull;

public class CourseRequest {
    @NotNull
    // 需要前端傳過來的參數
    private String courseName;
    @NotNull
    private CourseCategory category;
    @NotNull
    private String imageUrl;
    @NotNull
    private Integer price;
    private String description;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public CourseCategory getCategory() {
        return category;
    }

    public void setCategory(CourseCategory category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
