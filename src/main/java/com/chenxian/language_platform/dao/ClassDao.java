package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.ClassRequest;
import com.chenxian.language_platform.model.Class;

public interface ClassDao {
    Class getClassById(Integer classId);
    Integer creatClass(ClassRequest classRequest);
}
