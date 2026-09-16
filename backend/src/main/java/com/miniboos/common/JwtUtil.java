package com.miniboos.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具：无状态会话（技术设计文档第6节关键决策）
 * secret来自环境变量JWT_SECRET（.env/Render环境变量），绝不写死在代码里（Day 3铁律）
 */
@Component
public class JwtUtil {

    private static final long EXPIRE_MS = 7 * 24 * 3600 * 1000L; // 7天

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .signWith(key)
                .compact();
    }

    /** 解析失败（伪造/过期）返回null，由拦截器统一转401 */
    public CurrentUser parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
            return new CurrentUser(Long.valueOf(claims.getSubject()), claims.get("role", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    public record CurrentUser(Long userId, String role) {
    }
}
