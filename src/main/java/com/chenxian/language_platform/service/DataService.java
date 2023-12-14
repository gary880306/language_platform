package com.chenxian.language_platform.service;

import com.chenxian.language_platform.model.CategoryData;

import java.util.List;

public interface DataService {
    List<CategoryData> findAllCategoryData();

    CategoryData getCategoryDataById(Integer id);
}
