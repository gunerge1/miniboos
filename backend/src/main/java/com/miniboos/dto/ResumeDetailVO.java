package com.miniboos.dto;

import com.miniboos.entity.ResumeExperience;
import lombok.Data;

import java.util.List;

@Data
public class ResumeDetailVO {
    private Long id;
    private String name;
    private String photo;
    private String expectCategory;
    private String expectCity;
    private Integer expectSalaryMin;
    private Integer expectSalaryMax;
    private String intro;
    private Integer published;
    private List<ResumeExperience> experiences;
}
