package com.miniboos.dto;

import lombok.Data;

@Data
public class LoginResp {
    private String token;
    private Long userId;
    private String role;
    private String nickname;
}
