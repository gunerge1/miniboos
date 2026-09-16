package com.miniboos.mapper;

import com.miniboos.entity.DictItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DictItemMapper {

    @Select("SELECT * FROM dict_items WHERE dict_type=#{type} AND status=1 ORDER BY sort, id")
    List<DictItem> listEnabledByType(String type);

    @Select("<script>SELECT * FROM dict_items <where> <if test='type!=null and type!=\"\"'>dict_type=#{type}</if> </where> ORDER BY dict_type, sort, id</script>")
    List<DictItem> listAll(@Param("type") String type);

    @Insert("INSERT INTO dict_items(dict_type, code, label, sort) VALUES(#{dictType}, #{code}, #{label}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DictItem item);

    @Update("<script>UPDATE dict_items <set> <if test='label!=null'>label=#{label},</if> <if test='sort!=null'>sort=#{sort},</if> <if test='status!=null'>status=#{status},</if> </set> WHERE id=#{id}</script>")
    void update(DictItem item);

    @Delete("DELETE FROM dict_items WHERE id=#{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM dict_items WHERE id=#{id}")
    DictItem findById(Long id);
}
