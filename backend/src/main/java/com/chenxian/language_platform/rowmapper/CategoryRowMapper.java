package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.CategoryData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryRowMapper implements RowMapper<CategoryData> {
    @Override
    public CategoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryData categoryData = new CategoryData();
        categoryData.setId(rs.getInt("category_Id"));
        categoryData.setName(rs.getString("category_name"));
        return categoryData;
    }
}
