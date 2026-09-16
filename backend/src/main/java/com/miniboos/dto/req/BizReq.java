package com.miniboos.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class BizReq {

    @Data
    public static class Company {
        @NotBlank(message = "企业名称必填") private String name;
        @NotBlank(message = "行业必填") private String industry;
        private String licenseNo;
    }

    @Data
    public static class Job {
        @NotBlank(message = "职位名必填") private String title;
        @NotBlank(message = "岗位方向必填") private String category;
        @NotBlank(message = "城市必填") private String city;
        private String education;
        private Integer salaryMin;
        private Integer salaryMax;
        @NotBlank(message = "JD必填") private String description;
    }

    @Data
    public static class Resume {
        private String name;
        private String photo; // Base64，前端压缩≤200KB，后端兜底校验长度
        private String expectCategory;
        private String expectCity;
        private Integer expectSalaryMin;
        private Integer expectSalaryMax;
        private String intro;
        private Integer published;
    }

    @Data
    public static class Experience {
        @NotBlank(message = "项目名必填") private String projectName;
        private String startDate;
        private String endDate;
        private String description;
        private Integer sort;
    }

    @Data
    public static class Apply {
        @NotNull(message = "职位id必填") private Long jobId;
    }

    @Data
    public static class Message {
        @NotBlank(message = "留言内容必填") private String content;
    }
}
