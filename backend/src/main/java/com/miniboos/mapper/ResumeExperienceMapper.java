package com.miniboos.mapper;

import com.miniboos.entity.ResumeExperience;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ResumeExperienceMapper {

    @Select("SELECT * FROM resume_experiences WHERE resume_id=#{resumeId} ORDER BY sort, id")
    List<ResumeExperience> listByResumeId(Long resumeId);

    @Insert("INSERT INTO resume_experiences(resume_id, project_name, start_date, end_date, description, sort) VALUES(#{resumeId}, #{projectName}, #{startDate}, #{endDate}, #{description}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ResumeExperience exp);

    @Update("UPDATE resume_experiences SET project_name=#{projectName}, start_date=#{startDate}, end_date=#{endDate}, description=#{description}, sort=#{sort} WHERE id=#{id}")
    void update(ResumeExperience exp);

    @Delete("DELETE FROM resume_experiences WHERE id=#{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM resume_experiences WHERE id=#{id}")
    ResumeExperience findById(Long id);
}
