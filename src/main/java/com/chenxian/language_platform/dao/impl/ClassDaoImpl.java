package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.ClassDao;
import com.chenxian.language_platform.model.Class;
import com.chenxian.language_platform.rowmapper.ClassRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClassDaoImpl implements ClassDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Class getClassById(Integer classId) {
        String sql = "SELECT class_id , class_name, category, image_url, price, description, created_date, last_modified_date FROM class WHERE class_id=:classId";
        Map<String, Object> map = new HashMap<>();
        map.put("classId", classId);

        List<Class> classList = namedParameterJdbcTemplate.query(sql, map, new ClassRowMapper());

        if (classList.size() > 0) {
            return classList.get(0);
        } else {
            return null;
        }

    }
}
