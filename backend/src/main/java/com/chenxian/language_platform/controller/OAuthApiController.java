package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import com.chenxian.language_platform.util.JwtUtil;
import com.chenxian.language_platform.util.OAuth2Service;
import com.chenxian.language_platform.util.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * OAuth2 第三方登入 REST API
 * Google / GitHub
 */
@RestController
@RequestMapping("/api/auth")
public class OAuthApiController {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${google.oauth2.client-id}")
    private String googleClientId;

    @Value("${google.oauth2.client-secret}")
    private String googleClientSecret;

    @Value("${google.oauth2.redirect-uri}")
    private String googleRedirectUri;

    // ==================== Google OAuth2 ====================

    /**
     * Google OAuth2 callback
     * 前端將收到的 code 傳到此端點
     */
    @PostMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestBody Map<String, String> body) {
        try {
            String code = body.get("code");
            if (code == null || code.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "授權碼不可為空"));
            }

            User user = oAuth2Service.handleGoogleCallback(code);
            if (user == null) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Google 登入失敗"));
            }

            String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getLevelId());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", user.getUserId(),
                    "email", user.getEmail(),
                    "userName", user.getUserName(),
                    "levelId", user.getLevelId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Google 登入發生錯誤"));
        }
    }

    // ==================== GitHub OAuth2 ====================

    /**
     * GitHub OAuth2 callback
     * 前端將收到的 code 傳到此端點
     */
    @PostMapping("/github/callback")
    public ResponseEntity<?> githubCallback(@RequestBody Map<String, String> body) {
        try {
            String code = body.get("code");
            if (code == null || code.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "授權碼不可為空"));
            }

            User user = oAuthService.handleGitHubCallback(code);
            if (user == null) {
                return ResponseEntity.internalServerError().body(Map.of("message", "GitHub 登入失敗"));
            }

            String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getLevelId());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", user.getUserId(),
                    "email", user.getEmail(),
                    "userName", user.getUserName(),
                    "levelId", user.getLevelId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "GitHub 登入發生錯誤"));
        }
    }

    /** 取得 Google OAuth2 授權 URL */
    @GetMapping("/google/url")
    public ResponseEntity<?> getGoogleAuthUrl() {
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + googleClientId
                + "&redirect_uri=" + googleRedirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile";
        return ResponseEntity.ok(Map.of("url", url));
    }
}
