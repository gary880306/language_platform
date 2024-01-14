package com.chenxian.language_platform.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Course {
    private Integer courseId;

    private String courseName;
    private String categoryName;
    private String imageUrl;
    private Double time;
    private Integer price;
    private String teacher;
    private String description;
    private String creatDate;
    private String lastModifiedDate;
    private String videoUrl;
    private Integer userCount;
    private String formattedPrice;

}
