package com.chenxian.language_platform.customize;

import lombok.Data;

@Data
public class CategoryUserCount {
    private String categoryName;
    private Integer userCount;

    // 構造函數
    public CategoryUserCount(String categoryName, Integer userCount) {
        this.categoryName = categoryName;
        this.userCount = userCount;
    }
}
