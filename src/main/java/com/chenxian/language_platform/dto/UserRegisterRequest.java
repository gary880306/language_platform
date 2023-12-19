package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String userName;
    @NotBlank
    private String birth;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String address;
}
