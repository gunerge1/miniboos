package com.miniboos.service;

import com.miniboos.dto.MessageVO;
import com.miniboos.entity.Message;
import com.miniboos.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final ApplicationService applicationService;

    /** 拉取会话（留言板模式：进入自动拉/手动刷新/前端10秒轻轮询）——顺手把对方消息标已读 */
    public List<MessageVO> list(Long appId, Long userId) {
        applicationService.findAsParticipant(appId, userId);
        messageMapper.markRead(appId, userId);
        return messageMapper.listByApplication(appId);
    }

    public void send(Long appId, Long userId, String content) {
        applicationService.findAsParticipant(appId, userId);
        Message msg = new Message();
        msg.setApplicationId(appId);
        msg.setSenderId(userId);
        msg.setContent(content);
        messageMapper.insert(msg);
    }
}
