package com.miniboos.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 职位列表/详情视图（联表：职位+企业+字典label翻译）
 */
@Data
public class JobVO {
    private Long id;
    private String title;
    private String category;
    private String categoryLabel;
    private String city;
    private String cityLabel;
    private String education;
    private String educationLabel;
    private Integer salaryMin;
    private Integer salaryMax;
    private String description;
    private String status;
    private Long companyId;
    private String companyName;
    private String companyIndustryLabel;
    private LocalDateTime createdAt;
}
