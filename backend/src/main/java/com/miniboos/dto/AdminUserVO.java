package com.miniboos.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Admin用户列表项（手机号脱敏——PRD"只管系统不管运营明细"落地）
 */
@Data
public class AdminUserVO {
    private Long id;
    private String phoneMasked;
    private String role;
    private String nickname;
    private Integer status;
    private LocalDateTime createdAt;
}
