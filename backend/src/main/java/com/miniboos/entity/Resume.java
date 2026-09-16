package com.miniboos.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resume {
    private Long id;
    private Long userId;
    private String name;
    private String photo;
    private String expectCategory;
    private String expectCity;
    private Integer expectSalaryMin;
    private Integer expectSalaryMax;
    private String intro;
    private Integer published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
