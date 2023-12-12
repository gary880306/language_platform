package com.chenxian.language_platform.dto;

import com.chenxian.language_platform.constant.ClassCategory;
import jakarta.validation.constraints.NotNull;

public class ClassRequest {
    @NotNull
    // 需要前端傳過來的參數
    private String className;
    @NotNull
    private ClassCategory category;
    @NotNull
    private String imageUrl;
    @NotNull
    private Integer price;
    private String description;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ClassCategory getCategory() {
        return category;
    }

    public void setCategory(ClassCategory category) {
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
