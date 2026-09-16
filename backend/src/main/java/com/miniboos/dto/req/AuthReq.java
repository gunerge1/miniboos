package com.miniboos.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

public class AuthReq {

    @Data
    public static class Register {
        @NotBlank @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
        private String phone;
        @NotBlank @Pattern(regexp = "^.{6,20}$", message = "密码6-20位")
        private String password;
        @NotBlank @Pattern(regexp = "^(CANDIDATE|HR)$", message = "角色只能是牛人或HR")
        private String role;
        private String nickname;
    }

    @Data
    public static class Login {
        @NotBlank private String phone;
        @NotBlank private String password;
    }
}
