package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "Email不能為空")
    @Email(message = "Email格式不正確")
    private String email;

    @NotBlank(message = "密碼不能為空")
    private String password;

    @NotBlank(message = "用戶名不能為空")
    private String userName;

    @NotBlank(message = "生日不能為空")
    private String birth;

    @NotBlank(message = "電話號碼不能為空")
    @Size(min = 10, max = 10, message = "電話號碼格式錯誤")
    private String phoneNumber;

    @NotBlank(message = "地址不能為空")
    private String address;
    private String authType; // 授權來源
    private String authId; // 授權Id
    public UserRegisterRequest(){};

    public UserRegisterRequest(String email, String password, String username, String birth, String phoneNumber, String address, String authType, String authId) {
        this.email = email;
        this.password = password;
        this.userName = username;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.authType = authType;
        this.authId = authId;
    }
}
