package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.Class;
import com.chenxian.language_platform.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClassController {
    @Autowired
    private ClassService classService;
    @GetMapping("/classes/{classId}")
    public ResponseEntity<Class> getClass(@PathVariable Integer classId){
        Class result =  classService.getClassById(classId);
        if(result != null){
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
