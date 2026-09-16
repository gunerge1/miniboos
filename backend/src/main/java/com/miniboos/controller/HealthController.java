package com.miniboos.controller;

import com.miniboos.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查：keep-alive靶子（ADR-001），免鉴权
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Result<Map<String, Object>> health() {
        return Result.ok(Map.of("status", "UP", "time", LocalDateTime.now().toString()));
    }
}
