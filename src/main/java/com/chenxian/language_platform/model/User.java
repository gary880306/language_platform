package com.chenxian.language_platform.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private boolean isActive;
    private String authType;
    private String authId;
    
}

