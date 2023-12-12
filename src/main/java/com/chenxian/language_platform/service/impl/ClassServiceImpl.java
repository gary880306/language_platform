package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.dao.ClassDao;
import com.chenxian.language_platform.model.Class;
import com.chenxian.language_platform.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassDao classDao;
    @Override
    public Class getClassById(Integer classId) {
        return classDao.getClassById(classId);
    }
}
