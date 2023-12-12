package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.ClassRequest;
import com.chenxian.language_platform.model.Class;

public interface ClassService {
    Class getClassById(Integer classId);

    Integer creatClass(ClassRequest classRequest);
}
