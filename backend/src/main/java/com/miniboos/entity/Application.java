package com.miniboos.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Application {
    private Long id;
    private Long jobId;
    private Long userId;
    private String status;
    private LocalDateTime viewedAt;
    private LocalDateTime interviewAt;
    private LocalDateTime offerAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;
}
