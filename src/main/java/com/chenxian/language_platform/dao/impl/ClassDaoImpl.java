package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.ClassDao;
import com.chenxian.language_platform.dto.ClassRequest;
import com.chenxian.language_platform.model.Class;
import com.chenxian.language_platform.rowmapper.ClassRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
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

    @Override
    public Integer creatClass(ClassRequest classRequest) {
        String sql = "INSERT INTO class (class_name, category, image_url, price, description, created_date, last_modified_date) VALUES (:className,:category,:imageUrl,:price,:description,:createdDate,:lastModifiedDate)";
        Map<String,Object> map = new HashMap<>();
        map.put("className",classRequest.getClassName());
        map.put("category",classRequest.getCategory().toString());
        map.put("imageUrl",classRequest.getImageUrl());
        map.put("price",classRequest.getPrice());
        map.put("description",classRequest.getDescription());

        Date now = new Date();
        map.put("createdDate",now);
        map.put("lastModifiedDate",now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map),keyHolder);
        int classId = keyHolder.getKey().intValue();

        return classId;
    }

    @Override
    public void updateClass(Integer classId, ClassRequest classRequest) {
        String sql = "UPDATE class SET class_id =:classId,class_name =:className,category =:category,image_url =:imageUrl,price=:price,description=:description,last_modified_date=:lasModifiedDate WHERE class_id =:classId";
        Map<String,Object> map = new HashMap<>();
        map.put("classId",classId);
        map.put("className",classRequest.getClassName());
        map.put("category",classRequest.getCategory().toString());
        map.put("imageUrl",classRequest.getImageUrl());
        map.put("price",classRequest.getPrice());
        map.put("description",classRequest.getDescription());

        map.put("lasModifiedDate",new Date());
        namedParameterJdbcTemplate.update(sql,map);
    }
}
