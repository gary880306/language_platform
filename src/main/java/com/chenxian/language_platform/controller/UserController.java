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

    // 用戶登入頁面
    @GetMapping("/user/loginPage")
    public String loginPage(){
        return "user/login&register/login";
    }

    // 用戶登入功能實作
    @PostMapping("/user/login")
    public String login(@RequestParam @Valid String email,
                        @RequestParam @Valid String password,
                        HttpSession session, Model model){
        // 接收輸入的帳號密碼
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(email);
        userLoginRequest.setPassword(password);
        // 使用接收到的密碼查找資料庫是否有此用戶資訊
        User user = userService.getUserByEmail(userLoginRequest.getEmail());
        // 若有此用戶的邏輯
        if (user != null) {
            // 檢查用戶是否被停權
            if (!user.isActive()) {
                session.invalidate(); // 使 session 過期失效
                model.addAttribute("loginMessage", "該帳號已被停權");
                return "user/login&register/login"; // 將失敗消息渲染到登入頁面
            }

            // 比對密碼成功的邏輯
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", user); // 設置 session
                return "redirect:/enjoyLearning/courses"; // 重定向到主頁面
                // 比對密碼失敗的邏輯
            } else {
                session.invalidate(); // 使 session 過期失效
                model.addAttribute("loginMessage", "帳號或密碼錯誤");
                return "user/login&register/login"; // 將失敗消息渲染到登入頁面
            }
            // 資料庫無此用戶的邏輯
        } else {
            session.invalidate(); // 使 session 過期失效
            model.addAttribute("loginMessage", "無此用戶");
            return "user/login&register/login"; // 將失敗消息渲染到登入頁面
        }
    }


    // 管理員登入頁面
    @GetMapping("/admin/login")
    public String loginBackendPage(){
        return "user/login&register/loginBackend";
    }

    // 管理員登入功能實作
    @PostMapping("/admin/loginBackend")
    public String loginBackend(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session, Model model) throws Exception {
        // 比對驗證碼
//		if(!code.equals(session.getAttribute("code")+"")) {
//			session.invalidate(); // session 過期失效
//			model.addAttribute("loginMessage", "驗證碼錯誤");
//			return "group_buy/login";
//		}
        // 根據 username 查找 user 物件
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
                if(user.getLevelId() == 2) {
                    session.setAttribute("user", user); // 將 user 物件放入到 session 變數中
                    return "redirect:/admin/managementCourses"; // OK, 導向後台首頁
                }
                session.invalidate(); // session 過期失效
                model.addAttribute("loginMessage", "無權限登入後台");
                return "/user/login&register/loginBackend";
                // 比對密碼失敗邏輯
            }else{
                session.invalidate(); // session 過期失效
                model.addAttribute("loginMessage", "帳號或密碼錯誤");
                return "/user/login&register/loginBackend"; // 將失敗訊息渲染到登入頁面
            }
            // 資料庫無此用戶邏輯
        }else {
            session.invalidate(); // session 過期失效
            model.addAttribute("loginMessage", "無此使用者");
            return "/user/login&register/loginBackend"; // 將失敗訊息渲染到登入頁面
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
