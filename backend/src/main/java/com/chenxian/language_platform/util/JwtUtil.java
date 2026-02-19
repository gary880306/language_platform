package com.chenxian.language_platform.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:enjoylearning-secret-key-2024-must-be-at-least-256-bits-long}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration; // 24 hours in ms

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Integer userId, String email, Integer levelId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("levelId", levelId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Integer getUserId(String token) {
        return Integer.parseInt(parseToken(token).getSubject());
    }

    public String getEmail(String token) {
        return (String) parseToken(token).get("email");
    }

    public Integer getLevelId(String token) {
        return ((Number) parseToken(token).get("levelId")).intValue();
    }
}
