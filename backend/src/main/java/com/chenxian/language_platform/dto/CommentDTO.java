package com.chenxian.language_platform.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private String content;
    private Integer userId;
    private Integer postId;
}
