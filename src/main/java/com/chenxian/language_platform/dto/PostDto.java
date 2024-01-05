package com.chenxian.language_platform.dto;

import lombok.Data;

@Data
public class PostDto {
    private String title;
    private String content;
    private Integer userId; // 用戶 ID
    private Integer languageId; // 語言 ID
}
