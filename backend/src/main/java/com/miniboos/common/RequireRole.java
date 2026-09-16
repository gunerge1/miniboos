package com.miniboos.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色门槛注解：标在Controller方法上，由JwtInterceptor执行校验
 * 用法：@RequireRole({"HR"}) / @RequireRole({"CANDIDATE","HR"})（会话双方共用的接口）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}
