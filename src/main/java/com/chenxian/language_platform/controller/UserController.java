package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    // 登入頁面
    @GetMapping("/user/loginPage")
    public String loginPage(){
        return "user/login&register/login";
    }

    // 登入功能實作
    @PostMapping("/user/login")
    public String  login(@RequestParam @Valid String email,
                         @RequestParam @Valid String password,
                         HttpSession session, Model model){
        // 接收輸入的帳號密碼
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(email);
        userLoginRequest.setPassword(password);
        // 使用接收到的密碼查找資料庫是否有此用戶資訊
        User user  = userService.getUserByEmail(userLoginRequest.getEmail());

        // 有此用戶邏輯
        if(user != null) {
            // 比對密碼成功邏輯
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", user); // 設置 session
                return "redirect:/enjoyLearning/courses"; // 重導到主頁面
            // 比對密碼失敗邏輯
            }else{
                session.invalidate(); // session 過期失效
                model.addAttribute("loginMessage", "帳號或密碼錯誤");
                return "user/login&register/login"; // 將失敗訊息渲染到登入頁面
            }
        // 資料庫無此用戶邏輯
        }else {
            session.invalidate(); // session 過期失效
            model.addAttribute("loginMessage", "帳號或密碼錯誤");
            return "user/login&register/login"; // 將失敗訊息渲染到登入頁面
        }

    }

    // 登出功能實作
    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // session 過期失效
        return "redirect:/user/loginPage"; // 重導到登入頁面
    }

    // 註冊頁面
    @GetMapping("/user/register")
    public String register(){
        return "user/login&register/register";
    }

    // 註冊功能實作
    @PostMapping("/user/registerResult")
    public String registerResult(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String userName,
                                 @RequestParam String birth,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String address,
                                 Model model){
        // 驗證 Email 是否已被註冊
        User user = userService.getUserByEmail(email);
        if(user != null){
            model.addAttribute("emailRegistered", "Email已被註冊");
            return "/user/login&register/register"; // 將失敗訊息渲染到註冊頁面
        }

        // 成功則創建新的用戶資料
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(email);
        userRegisterRequest.setPassword(password);
        userRegisterRequest.setUserName(userName);
        userRegisterRequest.setBirth(birth);
        userRegisterRequest.setPhoneNumber(phoneNumber);
        userRegisterRequest.setAddress(address);
        // 使用 register 方法將會員資料存到資料庫
        userService.register(userRegisterRequest);
        // 導向登入成功頁面
        return "user/login&register/result";
    }
}
