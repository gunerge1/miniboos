package com.miniboos.mapper;

import com.miniboos.dto.ApplicationVO;
import com.miniboos.dto.CandidateVO;
import com.miniboos.entity.Application;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ApplicationMapper {

    @Insert("INSERT INTO applications(job_id, user_id) VALUES(#{jobId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Application application);

    @Select("SELECT COUNT(*) FROM applications WHERE job_id=#{jobId} AND user_id=#{userId}")
    long countByJobAndUser(@Param("jobId") Long jobId, @Param("userId") Long userId);

    @Select("SELECT * FROM applications WHERE id=#{id}")
    Application findById(Long id);

    @Select("SELECT COUNT(*) FROM applications")
    long countAll();

    // 状态流转：状态+对应时间戳一起写（终局分析钩子）
    @Update("UPDATE applications SET status='VIEWED', viewed_at=NOW() WHERE id=#{id}")
    void markViewed(Long id);

    @Update("UPDATE applications SET status='INTERVIEW', interview_at=NOW() WHERE id=#{id}")
    void markInterview(Long id);

    @Update("UPDATE applications SET status='OFFER', offer_at=NOW() WHERE id=#{id}")
    void markOffer(Long id);

    @Update("UPDATE applications SET status='REJECTED', rejected_at=NOW() WHERE id=#{id}")
    void markRejected(Long id);

    // 牛人视角·我的投递（联表）——XML
    List<ApplicationVO> listByUser(@Param("userId") Long userId);

    // HR视角·某职位投递列表（意向匹配打分排序）——XML
    List<CandidateVO> listCandidatesByJob(@Param("jobId") Long jobId);
}
