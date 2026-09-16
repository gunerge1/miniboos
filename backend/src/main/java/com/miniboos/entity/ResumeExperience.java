package com.miniboos.entity;

import lombok.Data;

@Data
public class ResumeExperience {
    private Long id;
    private Long resumeId;
    private String projectName;
    private String startDate;
    private String endDate;
    private String description;
    private Integer sort;
}
