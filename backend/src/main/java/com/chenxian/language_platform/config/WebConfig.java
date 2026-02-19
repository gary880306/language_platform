package com.chenxian.language_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 設定
 * 前後端分離架構下，驗證改由 JwtAuthenticationFilter 處理，
 * 不再使用 Session-based LoginInterceptor。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS 已透過 CorsConfig 的 CorsFilter Bean 處理
    // JWT 驗證已透過 JwtAuthenticationFilter 處理
}
