package com.chenxian.language_platform.dto;

import lombok.Data;

@Data
public class CourseQueryParams {
    private String search;
    private String orderBy;
    private String sort;
    private Integer page;  // 默认第1页
    private Integer size;  // 默认每页4条数据

}
