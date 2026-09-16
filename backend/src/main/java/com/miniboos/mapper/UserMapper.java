package com.miniboos.mapper;

import com.miniboos.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE phone=#{phone}")
    User findByPhone(String phone);

    @Select("SELECT * FROM users WHERE id=#{id}")
    User findById(Long id);

    @Insert("INSERT INTO users(phone, password_hash, role, nickname) VALUES(#{phone}, #{passwordHash}, #{role}, #{nickname})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE users SET status=#{status} WHERE id=#{id}")
    void updateStatus(@Param("id") Long id, @Param("status") int status);

    @Select("SELECT COUNT(*) FROM users WHERE role='CANDIDATE'")
    long countCandidates();
}
