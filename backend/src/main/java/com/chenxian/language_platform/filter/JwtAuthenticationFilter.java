package com.chenxian.language_platform.filter;

import com.chenxian.language_platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 公開路徑（不需要 JWT）
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/captcha",
            "/api/auth/google",
            "/api/auth/github",
            "/api/courses",
            "/api/images",
            "/chatroom-websocket",
            "/secure/oidc/callback/google",
            "/login/github",
            "/payment/return"   // ECPay 伺服器端付款回調（不帶 JWT）
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 放行公開路徑
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // OPTIONS 預檢放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"未登入或 Token 已過期，請重新登入\"}");
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Token 無效\"}");
            return;
        }

        // 將用戶資訊放入請求屬性供 Controller 使用
        Claims claims = jwtUtil.parseToken(token);
        request.setAttribute("userId", Integer.parseInt(claims.getSubject()));
        request.setAttribute("email", claims.get("email", String.class));
        request.setAttribute("levelId", ((Number) claims.get("levelId")).intValue());

        // 管理員路由檢查
        if (path.startsWith("/api/admin")) {
            Integer levelId = ((Number) claims.get("levelId")).intValue();
            if (levelId != 2) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"無管理員權限\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
