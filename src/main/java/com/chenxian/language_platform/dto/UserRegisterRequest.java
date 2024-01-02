package com.chenxian.language_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class UserRegisterRequest {
    @NotBlank
    @Email
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
