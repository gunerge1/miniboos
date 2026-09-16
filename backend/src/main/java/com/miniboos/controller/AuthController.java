package com.miniboos.controller;

import com.miniboos.common.Result;
import com.miniboos.dto.LoginResp;
import com.miniboos.dto.req.AuthReq;
import com.miniboos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody AuthReq.Register req) {
        authService.register(req);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody AuthReq.Login req) {
        return Result.ok(authService.login(req));
    }
}
