package com.miniboos.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * HR视角·投递列表项（联表：投递+简历，带意向匹配标记——技术设计文档：字段级匹配）
 * matchScore：category命中+3 / city命中+2 / 薪资区间重叠+1，前端按分数打"意向匹配"标
 */
@Data
public class CandidateVO {
    private Long applicationId;
    private Long userId;
    private String candidateName;
    private String expectCategory;
    private String expectCategoryLabel;
    private String expectCityLabel;
    private Integer expectSalaryMin;
    private Integer expectSalaryMax;
    private Integer matchScore;
    private String status;
    private LocalDateTime appliedAt;
}
