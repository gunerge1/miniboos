package com.miniboos.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Job {
    private Long id;
    private Long companyId;
    private String title;
    private String category;
    private String city;
    private String education;
    private Integer salaryMin;
    private Integer salaryMax;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
