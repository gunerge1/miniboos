package com.miniboos.mapper;

import com.miniboos.entity.Company;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CompanyMapper {

    @Insert("INSERT INTO companies(hr_user_id, name, industry, license_no) VALUES(#{hrUserId}, #{name}, #{industry}, #{licenseNo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Company company);

    @Select("SELECT * FROM companies WHERE hr_user_id=#{hrUserId}")
    Company findByHrUserId(Long hrUserId);

    @Select("SELECT * FROM companies WHERE id=#{id}")
    Company findById(Long id);

    @Select("<script>SELECT * FROM companies <where> <if test='status!=null and status!=\"\"'>status=#{status}</if> </where> ORDER BY created_at DESC LIMIT #{offset}, #{size}</script>")
    List<Company> listByStatus(@Param("status") String status, @Param("offset") int offset, @Param("size") int size);

    @Select("<script>SELECT COUNT(*) FROM companies <where> <if test='status!=null and status!=\"\"'>status=#{status}</if> </where></script>")
    long countByStatus(@Param("status") String status);

    @Update("UPDATE companies SET status=#{status}, reject_reason=#{rejectReason} WHERE id=#{id}")
    void updateAudit(@Param("id") Long id, @Param("status") String status, @Param("rejectReason") String rejectReason);
}
