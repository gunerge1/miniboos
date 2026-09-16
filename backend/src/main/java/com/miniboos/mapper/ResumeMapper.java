package com.miniboos.mapper;

import com.miniboos.entity.Resume;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ResumeMapper {

    @Select("SELECT * FROM resumes WHERE user_id=#{userId}")
    Resume findByUserId(Long userId);

    @Insert("INSERT INTO resumes(user_id, name, photo, expect_category, expect_city, expect_salary_min, expect_salary_max, intro, published) VALUES(#{userId}, #{name}, #{photo}, #{expectCategory}, #{expectCity}, #{expectSalaryMin}, #{expectSalaryMax}, #{intro}, #{published})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Resume resume);

    @Update("<script>UPDATE resumes <set> <if test='name!=null'>name=#{name},</if> <if test='photo!=null'>photo=#{photo},</if> <if test='expectCategory!=null'>expect_category=#{expectCategory},</if> <if test='expectCity!=null'>expect_city=#{expectCity},</if> <if test='expectSalaryMin!=null'>expect_salary_min=#{expectSalaryMin},</if> <if test='expectSalaryMax!=null'>expect_salary_max=#{expectSalaryMax},</if> <if test='intro!=null'>intro=#{intro},</if> <if test='published!=null'>published=#{published},</if> </set> WHERE user_id=#{userId}</script>")
    void update(Resume resume);

    @Select("SELECT COUNT(*) FROM resumes WHERE published=1")
    long countPublished();
}
