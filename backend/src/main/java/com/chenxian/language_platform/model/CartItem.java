package com.chenxian.language_platform.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CartItem {
    private Integer itemId;
    private Integer cartId;
    private Integer courseId;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;
    private Cart cart;
    private Course course;

    public Integer getAmount() {
        if (course != null) {
            return course.getPrice();
        }
        return 0;
    }
}
