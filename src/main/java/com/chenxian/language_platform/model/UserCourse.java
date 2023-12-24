package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserCourse {
    private Integer userId;
    private Integer courseId;
    private Timestamp purchaseDate;
}
