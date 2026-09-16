package com.miniboos.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话留言视图（带发送者身份：牛人/HR谁在说话）
 */
@Data
public class MessageVO {
    private Long id;
    private Long senderId;
    private String senderRole;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}
