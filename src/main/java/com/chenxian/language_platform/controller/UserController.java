package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/login")
    public String login(){
        return "user/login&register/login";
    }

    @GetMapping("/user/register")
    public String register(){
        return "user/login&register/register";
    }
    @PostMapping("/user/result")
    public String registerResult(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String userName,
                                 @RequestParam String birth,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String address){
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(email);
        userRegisterRequest.setPassword(password);
        userRegisterRequest.setUserName(userName);
        userRegisterRequest.setBirth(birth);
        userRegisterRequest.setPhoneNumber(phoneNumber);
        userRegisterRequest.setAddress(address);
        Integer userId = userService.register(userRegisterRequest);
        User user = userService.getUserById(userId);
        return "user/login&register/result";
    }
}
