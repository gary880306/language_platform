package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank(message = "電子郵件不可為空")
    @Email(message = "Email格式不正確")
    private String email;
    @NotBlank(message = "密碼不可為空")
    private String password;
}
