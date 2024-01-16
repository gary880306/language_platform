package com.chenxian.language_platform.config;

import com.chenxian.language_platform.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 指定攔截的路徑
                .excludePathPatterns("/user/loginPage", "/user/login", "/user/logout", "/user/register", "/user/registerResult", "/user/forgetPassword","user/login&register/forgetPassword", "/sendResetPasswordLink", "user/login&register/sendEmailResult", "user/login&register/changePassword","/user/resetPassword","user/login&register/changeSuccess","/admin/login",
                        "/secure/oidc/login/google", "/secure/oidc/callback/google", "/login/github", "/secure/oauth2/callback/github", "/images/**" ,"/getCode"); // 排除靜態資源路徑
    }
}
