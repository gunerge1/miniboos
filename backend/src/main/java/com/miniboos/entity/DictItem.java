package com.miniboos.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DictItem {
    private Long id;
    private String dictType;
    private String code;
    private String label;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
