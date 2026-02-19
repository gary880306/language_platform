package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailForm {
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式錯誤")
    private String email;
}
