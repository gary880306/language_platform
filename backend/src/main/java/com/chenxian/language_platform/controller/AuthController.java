package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.EmailForm;
import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import com.chenxian.language_platform.util.EmailService;
import com.chenxian.language_platform.util.JwtUtil;
import com.chenxian.language_platform.util.MyEncryptionComponent;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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

/**
 * 認證 API 控制器
 * 處理登入、註冊、密碼重設、驗證碼
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyEncryptionComponent myEncryptionComponent;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    /** 驗證碼圖片 */
    @GetMapping("/captcha")
    public void getCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            char c = random.nextBoolean()
                    ? (char) (random.nextInt(26) + 'A')
                    : (char) (random.nextInt(26) + 'a');
            codeBuilder.append(c);
        }
        String code = codeBuilder.toString();
        session.setAttribute("code", code);

        BufferedImage img = new BufferedImage(80, 30, BufferedImage.TYPE_INT_BGR);
        Graphics g = img.getGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 80, 30);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.drawString(code, 10, 23);
        g.setColor(Color.BLUE);
        for (int i = 0; i < 10; i++) {
            g.drawLine(random.nextInt(80), random.nextInt(30),
                    random.nextInt(80), random.nextInt(30));
        }
        response.setContentType("image/png");
        ImageIO.write(img, "PNG", response.getOutputStream());
    }

    /** 用戶登入 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest req,
                                   BindingResult bindingResult,
                                   @RequestParam(required = false) String code,
                                   HttpSession session) throws Exception {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                            (m1, m2) -> m1 + ", " + m2));
            return ResponseEntity.badRequest().body(errors);
        }

        // 驗證碼檢查（可選，前端可傳可不傳）
        if (code != null && !code.equals(session.getAttribute("code") + "")) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "驗證碼錯誤"));
        }

        User user = userService.getUserByEmail(req.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "無此用戶"));
        }
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "該帳號已被停權"));
        }

        String encrypted = myEncryptionComponent.encrypt(req.getPassword());
        if (!user.getPassword().equals(encrypted)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "帳號或密碼錯誤"));
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getLevelId());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getUserId(),
                "email", user.getEmail(),
                "userName", user.getUserName(),
                "levelId", user.getLevelId()
        ));
    }

    /** 管理員登入 */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> body) throws Exception {
        String email = body.get("email");
        String password = body.get("password");

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "無此使用者"));
        }

        String encrypted = myEncryptionComponent.encrypt(password);
        if (!user.getPassword().equals(encrypted)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "帳號密碼錯誤"));
        }

        if (user.getLevelId() != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "無權限登入後台"));
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getLevelId());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getUserId(),
                "email", user.getEmail(),
                "userName", user.getUserName(),
                "levelId", user.getLevelId()
        ));
    }

    /** 用戶登出（前端清除 Token 即可，後端回傳確認） */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }

    /** 用戶註冊 */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest req,
                                      BindingResult bindingResult) throws Exception {
        Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                if ("phoneNumber".equals(error.getField())
                        && error.getDefaultMessage().contains("格式錯誤")
                        && StringUtils.isEmpty(req.getPhoneNumber())) {
                    return;
                }
                errors.put(error.getField(), error.getDefaultMessage());
            });
            if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        }

        if (userService.getUserByEmail(req.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email已被註冊"));
        }

        req.setPassword(myEncryptionComponent.encrypt(req.getPassword()));
        req.setAuthType("local");
        userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "註冊成功"));
    }

    /** 忘記密碼：發送重設信 */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid EmailForm emailForm,
                                             BindingResult bindingResult) throws MessagingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email 格式錯誤"));
        }
        User user = userService.getUserByEmail(emailForm.getEmail());
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "無此用戶"));
        }
        String token = userService.generateResetToken(emailForm.getEmail());
        // 重設連結指向 Vue 前端頁面
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendResetPasswordLink(emailForm.getEmail(), resetUrl);
        return ResponseEntity.ok(Map.of("message", "重設密碼信已發送"));
    }

    /** 重設密碼 */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) throws Exception {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        String confirm = body.get("confirmPassword");

        if (newPassword == null || newPassword.isBlank() || confirm == null || confirm.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "欄位不可為空"));
        }
        if (!newPassword.equals(confirm)) {
            return ResponseEntity.badRequest().body(Map.of("message", "密碼不一致"));
        }
        if (!userService.checkTokenValidity(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token 無效或已過期"));
        }
        boolean ok = userService.resetPasswordWithToken(token, newPassword);
        if (ok) return ResponseEntity.ok(Map.of("message", "密碼重設成功"));
        return ResponseEntity.internalServerError().body(Map.of("message", "密碼重設失敗"));
    }

    /** 取得當前登入用戶資訊 */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(jakarta.servlet.http.HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String email = (String) request.getAttribute("email");
        Integer levelId = (Integer) request.getAttribute("levelId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "未登入"));
        }
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "email", email,
                "levelId", levelId,
                "userName", user != null ? user.getUserName() : ""
        ));
    }
}
