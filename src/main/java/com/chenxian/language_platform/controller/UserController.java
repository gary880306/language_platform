package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.EmailForm;
import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import com.chenxian.language_platform.util.EmailService;
import com.chenxian.language_platform.util.MyEncryptionComponent;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.FieldError;



import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private MyEncryptionComponent myEncryptionComponent;
    @Autowired
    private EmailService emailService;

    @GetMapping("/getCode")
    private void getCodeImage(HttpSession session, HttpServletResponse response) throws IOException {
        logger.info("Generating code image");
        // 產生一個驗證碼 code
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            char randomCode;
            if (random.nextBoolean()) {
                // 生成一個大寫字母 (65-90)
                randomCode = (char) (random.nextInt(26) + 'A');
            } else {
                // 生成一個小寫字母 (97-122)
                randomCode = (char) (random.nextInt(26) + 'a');
            }
            codeBuilder.append(randomCode);
        }

        String code = codeBuilder.toString();
        session.setAttribute("code", code);
        // Java 2D 產生圖檔
        // 1. 建立圖像暫存區
        BufferedImage img = new BufferedImage(80, 30, BufferedImage.TYPE_INT_BGR);
        // 2. 建立畫布
        Graphics g = img.getGraphics();
        // 3. 設定顏色
        g.setColor(Color.GRAY);
        // 4. 塗滿背景
        g.fillRect(0, 0, 80, 30);
        // 5. 設定顏色
        g.setColor(Color.BLACK);
        // 6. 設定自型
        g.setFont(new Font("新細明體", Font.PLAIN, 30));
        // 7. 繪字串
        g.drawString(code, 10, 23); // code, x, y
        // 8. 干擾線
        g.setColor(Color.BLUE);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(80);
            int y1 = random.nextInt(30);
            int x2 = random.nextInt(80);
            int y2 = random.nextInt(30);
            g.drawLine(x1, y1, x2, y2);
        }

        // 設定回應類型
        response.setContentType("image/png");

        // 將影像串流回寫給 client
        ImageIO.write(img, "PNG", response.getOutputStream());
    }

    // 用戶登入頁面
    @GetMapping("/user/loginPage")
    public String loginPage() {
        return "user/login/login";
    }

    // 用戶登入功能實作
    @PostMapping("/user/login")
    public String login(@ModelAttribute @Valid UserLoginRequest userLoginRequest,
                        BindingResult bindingResult,
                        @RequestParam String code,
                        HttpSession session, Model model) throws Exception {
        logger.info("Processing login for email: {}", userLoginRequest.getEmail());
        if (bindingResult.hasErrors()) {
            Map<String, String> formErrors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (msg1, msg2) -> String.join(", ", msg1, msg2)));
            model.addAttribute("formErrors", formErrors);
            return "user/login/login";
        }

        // 比對驗證碼
        if (!code.equals(session.getAttribute("code") + "")) {
            session.invalidate(); // session 過期失效
            model.addAttribute("loginMessage", "驗證碼錯誤");
            return "user/login/login";
        }

        // 使用接收到的密碼查找資料庫是否有此用戶資訊
        User user = userService.getUserByEmail(userLoginRequest.getEmail());
        // 若有此用戶的邏輯
        if (user != null) {
            // 檢查用戶是否被停權
            if (!user.isActive()) {
                session.invalidate(); // 使 session 過期失效
                model.addAttribute("loginMessage", "該帳號已被停權");
                return "user/login/login"; // 將失敗消息渲染到登入頁面
            }
            String encryptedPassword = myEncryptionComponent.encrypt(userLoginRequest.getPassword());

            // 比對密碼成功的邏輯
            if (user.getPassword().equals(encryptedPassword)) {
                session.setAttribute("user", user); // 設置 session
                return "redirect:/enjoyLearning/courses"; // 重定向到主頁面
                // 比對密碼失敗的邏輯
            } else {
                session.invalidate(); // 使 session 過期失效
                model.addAttribute("loginMessage", "帳號或密碼錯誤");
                return "user/login/login"; // 將失敗消息渲染到登入頁面
            }
            // 資料庫無此用戶的邏輯
        } else {
            session.invalidate(); // 使 session 過期失效
            model.addAttribute("loginMessage", "無此用戶");
            return "user/login/login"; // 將失敗消息渲染到登入頁面
        }
    }


    // 管理員登入頁面
    @GetMapping("/admin/login")
    public String loginBackendPage() {
        return "user/login/loginBackend";
    }

    // 管理員登入功能實作
    @PostMapping("/admin/loginBackend")
    public String loginBackend(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session, Model model) throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(email);
        userLoginRequest.setPassword(password);

        // 使用接收到的密碼查找資料庫是否有此用戶資訊
        User user = userService.getUserByEmail(userLoginRequest.getEmail());
        // 有此用戶邏輯
        if (user != null) {
            // 比對密碼成功邏輯
            if (user.getPassword().equals(password)) {
                if (user.getLevelId() == 2) {
                    session.setAttribute("user", user); // 將 user 物件放入到 session 變數中
                    return "redirect:/admin/managementCourses"; // OK, 導向後台首頁
                }
                session.invalidate(); // session 過期失效
                model.addAttribute("loginMessage", "無權限登入後台");
                return "/user/login/loginBackend";
                // 比對密碼失敗邏輯
            } else {
                session.invalidate(); // session 過期失效
                model.addAttribute("loginMessage", "帳號或密碼錯誤");
                return "/user/login/loginBackend"; // 將失敗訊息渲染到登入頁面
            }
            // 資料庫無此用戶邏輯
        } else {
            session.invalidate(); // session 過期失效
            model.addAttribute("loginMessage", "無此使用者");
            return "/user/login/loginBackend"; // 將失敗訊息渲染到登入頁面
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
    public String register() {
        return "user/login/register";
    }

    // 註冊功能實作
    @PostMapping("/user/registerResult")
    public String registerResult(@Valid UserRegisterRequest userRegisterRequest,
                                 BindingResult bindingResult, Model model) throws Exception{
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            bindingResult.getFieldErrors().forEach(error -> {
                if ("phoneNumber".equals(error.getField()) && error.getDefaultMessage().contains("格式錯誤")) {
                    if (!StringUtils.isEmpty(userRegisterRequest.getPhoneNumber())) {
                        errors.put(error.getField(), error.getDefaultMessage());
                    }
                } else {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
            });

            logger.error("Registration failed for user: {}, Errors: {}", userRegisterRequest.getEmail(), errors);
            model.addAttribute("formErrors", errors);
            return "user/login/register";
        }

        // 驗證 Email 是否已被註冊
        User user = userService.getUserByEmail(userRegisterRequest.getEmail());
        if (user != null) {
            model.addAttribute("emailRegistered", "Email已被註冊");
            return "/user/login/register"; // 將失敗訊息渲染到註冊頁面
        }

        // 註冊密碼加密
        String encryptedPassword = myEncryptionComponent.encrypt(userRegisterRequest.getPassword());

        userRegisterRequest.setPassword(encryptedPassword);
        userRegisterRequest.setAuthType("local");
        // 使用 register 方法將會員資料存到資料庫
        userService.register(userRegisterRequest);
        logger.info("Registration successful for user: {}", userRegisterRequest.getEmail());
        // 導向登入成功頁面
        return "user/login/registerResult";
    }

    @GetMapping("/user/forgetPassword")
    public String enterEmail(Model model) {
        model.addAttribute("emailForm", new EmailForm());
        return "user/login/forgetPassword";
    }

    @PostMapping("/sendResetPasswordLink")
    public String generateAndSendResetToken(@Valid EmailForm emailForm, BindingResult bindingResult, Model model) throws MessagingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {

            return "user/login/forgetPassword";
        }
        String email = emailForm.getEmail();
        // 驗證用戶是否存在
        User user = userService.getUserByEmail(email);
        if (user != null) {
            // 生成重置密碼令牌
            String token = userService.generateResetToken(email);

            // 建立重置密碼鏈接
            String resetUrl = "http://localhost:8080/user/resetPassword?token=" + token;

            // 發送重置郵件
            emailService.sendResetPasswordLink(email, resetUrl);

            return "user/login/sendEmailResult";
        }
        model.addAttribute("errorMessage", "無此用戶");
        return "user/login/forgetPassword";
    }

    // 用戶點擊 email 中的 url 之後跳轉到修改密碼畫面
    @GetMapping("/user/resetPassword")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // 驗證令牌的有效性
        boolean isValid = userService.checkTokenValidity(token);
        if (isValid) {
            // 令牌有效，顯示修改密碼的表單
            model.addAttribute("token", token);
            return "user/login/changePassword";
        } else {
            // 令牌無效，顯示錯誤頁面或提示
            return "user/login/timeInvalid";
        }

    }

    // 創建新密碼功能
    @PostMapping("/user/resetPassword")
    public String resetPassword(@RequestParam("token") String token,@RequestParam("newPassword") String newPassword,@RequestParam("newPasswordChecked") String newPasswordChecked,Model model) throws Exception {
        // 检查新密码或确认密码是否为空
        if (newPassword == null || newPassword.trim().isEmpty() ||
                newPasswordChecked == null || newPasswordChecked.trim().isEmpty()) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "欄位不可為空");
            return "user/login/changePassword"; // 返回到密码重置表单页面
        }

        if (!newPassword.equals(newPasswordChecked)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "密碼不一致");
            return "user/login/changePassword"; // 返回到密码重置表单页面
        }

        boolean isResetSuccessful = userService.resetPasswordWithToken(token,newPassword);
        if (isResetSuccessful) {
            // 密碼重置成功，導向成功頁面
            return "user/login/changeSuccess"; // 請確保你有相應的視圖定義
        } else {
            // 密碼重置失敗，導向錯誤頁面，可以向模型添加錯誤消息
            model.addAttribute("errorMessage", "密碼重置失敗，請檢查令牌的有效性");
            return "user/login/login"; // 請確保你有相應的視圖定義
        }
    }
}