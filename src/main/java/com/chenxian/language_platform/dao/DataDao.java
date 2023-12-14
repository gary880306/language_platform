package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.model.CategoryData;

import java.util.List;
import java.util.Optional;

public interface DataDao {
    List<CategoryData> findAllCategoryData();

    CategoryData getCategoryDataById(Integer id);
}
