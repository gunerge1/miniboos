package com.miniboos.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 牛人视角·我的投递列表项（联表：投递+职位+企业）
 */
@Data
public class ApplicationVO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String status;
    private LocalDateTime createdAt;
}
