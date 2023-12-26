package com.chenxian.language_platform.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 設置 HTTP 響應頭以防止快取
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();
        // 檢查 session 中是否有 user 物件（用戶已經登入）
        if (session.getAttribute("user") != null) {
            return true; // 放行
        }

        // 獲取請求的 URL
        String requestURI = request.getRequestURI();

        // 檢查是否已經在登入頁面，防止無限重定向
        if (requestURI.endsWith("/loginPage") || requestURI.endsWith("/loginBackend")) {
            return true; // 放行，防止無限重定向
        }

        if (requestURI.startsWith("/admin")) {
            // 重定向到後台登入表單頁面
            response.sendRedirect(request.getServletContext().getContextPath() + "/admin/loginPage");
        } else {
            // 重定向到前台登入頁面
            response.sendRedirect(request.getServletContext().getContextPath() + "/user/loginPage");
        }
        return false; // 不放行
    }
}