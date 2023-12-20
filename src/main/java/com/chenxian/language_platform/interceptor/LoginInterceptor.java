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
        // 檢查 session 中是否有 user 的物件資料(意味著用戶已經登入)
        if(session.getAttribute("user") != null) {
            return true; // 放行
        }
        // 未登入, 導入到登入頁面
        response.sendRedirect(request.getServletContext().getContextPath() + "/user/loginPage");
        return false; // 不放行
    }
}
