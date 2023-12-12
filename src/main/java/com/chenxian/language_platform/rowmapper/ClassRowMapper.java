package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Class;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassRowMapper implements RowMapper<Class> {
    @Override
    public Class mapRow(ResultSet rs, int rowNum) throws SQLException {
        Class newClass = new Class();
        newClass.setClassId(rs.getInt("class_id"));
        newClass.setClassName(rs.getString("class_name"));
        newClass.setCategory(rs.getString("category"));
        newClass.setImageUrl(rs.getString("image_url"));
        newClass.setPrice(rs.getInt("price"));
        newClass.setDescription(rs.getString("description"));
        newClass.setCreatDate(rs.getTimestamp("created_date"));
        newClass.setLastModifiedDate(rs.getTimestamp("last_modified_date"));
        return newClass;
    }
}
