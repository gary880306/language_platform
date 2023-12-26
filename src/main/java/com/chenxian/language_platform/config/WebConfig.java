package com.chenxian.language_platform.config;

import com.chenxian.language_platform.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 注入 HandlerInterceptor 實作
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 註冊攔截器並設定攔截路徑
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 指定攔截的路徑
                .excludePathPatterns("/user/loginPage", "/user/login", "/user/logout", "/user/register", "/user/registerResult","/admin/login"); // 排除特定路徑
    }
}
