package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.NotNull;

public class CourseRequest {
    @NotNull
    // 需要前端傳過來的參數
    private String courseName;
    @NotNull
    private Integer categoryId;
    @NotNull
    private String imageUrl;
    @NotNull
    private Double time;
    @NotNull
    private Integer price;
    @NotNull
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
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
}
