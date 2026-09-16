package com.miniboos.dto;

import lombok.Data;

/**
 * Admin数据看板（PRD #18）
 */
@Data
public class StatsVO {
    private long companyCount;
    private long jobCount;
    private long applicationCount;
    private long resumeCount;
    private long candidateCount;
}
