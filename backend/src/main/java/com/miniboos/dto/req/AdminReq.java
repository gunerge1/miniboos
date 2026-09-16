package com.miniboos.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class AdminReq {

    /** 企业/职位审核 */
    @Data
    public static class Audit {
        @NotBlank @jakarta.validation.constraints.Pattern(regexp = "^(APPROVED|REJECTED)$", message = "审核结论不合法")
        private String result;
        private String reason; // 驳回时必填（service校验）
    }

    /** 用户禁用/启用 */
    @Data
    public static class UserStatus {
        @NotNull private Integer status; // 1启用/0禁用
    }

    /** 字典项新增/修改 */
    @Data
    public static class Dict {
        @NotBlank(message = "字典类型必填") private String dictType;
        @NotBlank(message = "code必填") private String code;
        @NotBlank(message = "显示名必填") private String label;
        private Integer sort;
        private Integer status;
    }

    /** 投递状态流转（HR操作） */
    @Data
    public static class AppStatus {
        @NotBlank @jakarta.validation.constraints.Pattern(regexp = "^(VIEWED|INTERVIEW|OFFER|REJECTED)$", message = "状态不合法")
        private String status;
    }
}
