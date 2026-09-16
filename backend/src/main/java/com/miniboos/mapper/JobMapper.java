package com.miniboos.mapper;

import com.miniboos.dto.JobVO;
import com.miniboos.entity.Job;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface JobMapper {

    @Insert("INSERT INTO jobs(company_id, title, category, city, education, salary_min, salary_max, description) VALUES(#{companyId}, #{title}, #{category}, #{city}, #{education}, #{salaryMin}, #{salaryMax}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Job job);

    @Select("SELECT * FROM jobs WHERE id=#{id}")
    Job findById(Long id);

    @Update("UPDATE jobs SET status=#{status} WHERE id=#{id}")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM jobs WHERE company_id=#{companyId} ORDER BY created_at DESC")
    List<Job> listByCompany(Long companyId);

    @Select("SELECT COUNT(*) FROM jobs")
    long countAll();

    // 公开职位列表（筛选+意向匹配CASE排序）——XML实现，见mapper/JobMapper.xml
    List<JobVO> listPublic(@Param("category") String category, @Param("city") String city,
                           @Param("kw") String kw, @Param("offset") int offset, @Param("size") int size,
                           @Param("expectCategory") String expectCategory, @Param("expectCity") String expectCity,
                           @Param("expectSalaryMin") Integer expectSalaryMin, @Param("expectSalaryMax") Integer expectSalaryMax);

    long countPublic(@Param("category") String category, @Param("city") String city, @Param("kw") String kw);
}
