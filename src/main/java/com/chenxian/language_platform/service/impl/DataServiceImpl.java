package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.DataDao;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataServiceImpl implements DataService {
    @Autowired
    DataDao dataDao;
    @Override
    public List<CategoryData> findAllCategoryData() {
        return dataDao.findAllCategoryData();
    }

    @Override
    public CategoryData getCategoryDataById(Integer id) {
        return dataDao.getCategoryDataById(id);
    }
}
