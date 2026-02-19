package com.chenxian.language_platform.filter;

import com.chenxian.language_platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 認證過濾器 (OCP: 公開路徑透過 application.properties 設定，新增路徑無需修改程式碼)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 公開路徑從設定檔讀取 (OCP: 對擴展開放、對修改關閉)
     * 新增公開路徑只需在 application.properties 的 jwt.public-paths 增加即可
     */
    @Value("${jwt.public-paths:/api/auth/login,/api/auth/register,/api/courses,/chatroom-websocket}")
    private String publicPathsConfig;

    private List<String> getPublicPaths() {
        return Arrays.asList(publicPathsConfig.split(","));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 放行公開路徑
        for (String publicPath : getPublicPaths()) {
            if (path.startsWith(publicPath.trim())) {
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
