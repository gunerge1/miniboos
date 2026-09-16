package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.common.JwtUtil;
import com.miniboos.dto.LoginResp;
import com.miniboos.dto.req.AuthReq;
import com.miniboos.entity.User;
import com.miniboos.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public void register(AuthReq.Register req) {
        if (userMapper.findByPhone(req.getPhone()) != null) {
            throw BizException.conflict("该手机号已注册");
        }
        User user = new User();
        user.setPhone(req.getPhone());
        user.setPasswordHash(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setNickname(req.getNickname() == null || req.getNickname().isBlank()
                ? "用户" + req.getPhone().substring(7) : req.getNickname());
        userMapper.insert(user);
    }

    public LoginResp login(AuthReq.Login req) {
        User user = userMapper.findByPhone(req.getPhone());
        if (user == null || !encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw BizException.unauthorized("手机号或密码错误");
        }
        if (user.getStatus() != 1) {
            throw BizException.forbidden("账号已被禁用，请联系平台");
        }
        LoginResp resp = new LoginResp();
        resp.setToken(jwtUtil.generate(user.getId(), user.getRole()));
        resp.setUserId(user.getId());
        resp.setRole(user.getRole());
        resp.setNickname(user.getNickname());
        return resp;
    }
}
