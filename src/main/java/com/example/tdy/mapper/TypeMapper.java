package com.example.tdy.mapper;

import com.example.tdy.entity.Subscribe;
import com.example.tdy.entity.Type;
import com.example.tdy.entity.Video;
import com.example.tdy.result.BasePage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */

@Mapper
public interface TypeMapper {
    @Select("select * from type")
    List<Type> selectAll();

    List<Type> selectByIds(@Param("ids") List<Integer> ids);
    @Select("select * from video where type_id = #{id} limit #{offset},#{limit}")
    List<Video> selectByTypeId(@Param("id") Integer id, @Param("offset") Integer offset,@Param("limit") Integer limit);
}
