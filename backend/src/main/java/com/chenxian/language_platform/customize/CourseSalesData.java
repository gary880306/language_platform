package com.chenxian.language_platform.customize;

import lombok.Data;

@Data
public class CourseSalesData {
    private String courseName;
    private Integer purchaseCount;

    public CourseSalesData(String courseName, Integer purchaseCount) {
        this.courseName = courseName;
        this.purchaseCount = purchaseCount;
    }
}
