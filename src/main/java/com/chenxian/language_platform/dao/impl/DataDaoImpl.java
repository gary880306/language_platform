package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.DataDao;
import com.chenxian.language_platform.model.CategoryData;
import com.chenxian.language_platform.rowmapper.CategoryRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DataDaoImpl implements DataDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public List<CategoryData> findAllCategoryData() {
        String sql = "SELECT category_id,category_name FROM category";
        Map<String,Object> map = new HashMap<>();
        List<CategoryData> categoryDataList =  namedParameterJdbcTemplate.query(sql,map,new CategoryRowMapper());
        return categoryDataList;
    }

    @Override
    public CategoryData getCategoryDataById(Integer id) {
        String sql = "SELECT category_id,category_name FROM category WHERE category_id=:id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        List<CategoryData> categoryDataList = namedParameterJdbcTemplate.query(sql, map, new CategoryRowMapper());
        if (categoryDataList.size() > 0) {
            return categoryDataList.get(0);
        }
        return null;
    }
}
