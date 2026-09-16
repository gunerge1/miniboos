package com.miniboos.config;

import com.miniboos.common.BizException;
import com.miniboos.common.JwtUtil;
import com.miniboos.common.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 角色鉴权拦截器（技术设计文档第7节：越权防护）
 * 白名单放行 → 解析JWT → @RequireRole角色门槛校验
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // CORS预检直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 白名单：注册/登录/健康检查/字典读取
        String uri = request.getRequestURI();
        if (uri.equals("/api/auth/register") || uri.equals("/api/auth/login")
                || uri.equals("/api/health") || uri.startsWith("/api/dicts")) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw BizException.unauthorized("未登录或凭证缺失");
        }
        JwtUtil.CurrentUser user = jwtUtil.parse(auth.substring(7));
        if (user == null) {
            throw BizException.unauthorized("登录已过期，请重新登录");
        }
        // 账号状态由各业务接口自查（禁用账号的JWT最多再活7天）

        if (handler instanceof HandlerMethod method) {
            RequireRole anno = method.getMethodAnnotation(RequireRole.class);
            if (anno == null) {
                anno = method.getBeanType().getAnnotation(RequireRole.class);
            }
            if (anno != null && Arrays.stream(anno.value()).noneMatch(r -> r.equals(user.role()))) {
                throw BizException.forbidden("角色无权访问：" + user.role());
            }
        }
        request.setAttribute("currentUser", user);
        return true;
    }
}
