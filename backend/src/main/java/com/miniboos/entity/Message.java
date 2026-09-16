package com.miniboos.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long applicationId;
    private Long senderId;
    private String content;
    private Integer isRead;
    private LocalDateTime createdAt;
}
