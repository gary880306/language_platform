package com.chenxian.language_platform.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {
    private Integer userId;
    private String email;
    private String password;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;
    private Date createdDate;
    private Date lastModifiedDate;
    private Integer levelId;
    private List<Service> services;
    private boolean isActive;
    private String authType; // 授權來源
    private String authId; // 授權Id

}
