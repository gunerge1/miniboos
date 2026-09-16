package com.miniboos.mapper;

import com.miniboos.dto.MessageVO;
import com.miniboos.entity.Message;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MessageMapper {

    // 会话留言（带发送者身份）——XML
    List<MessageVO> listByApplication(@Param("applicationId") Long applicationId);

    @Insert("INSERT INTO messages(application_id, sender_id, content) VALUES(#{applicationId}, #{senderId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Message message);

    @Update("UPDATE messages SET is_read=1 WHERE application_id=#{applicationId} AND sender_id!=#{readerId}")
    void markRead(@Param("applicationId") Long applicationId, @Param("readerId") Long readerId);
}
