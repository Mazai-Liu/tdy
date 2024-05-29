package com.example.tdy.mapper;

import com.example.tdy.entity.File;
import com.example.tdy.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */

@Mapper
public interface FileMapper {
    void insert(File file);

    @Select("select * from file where id = #{fileId}")
    File selectById(Integer fileId);
    @Select("select * from video where title like ${searchName} or description like ${searchName} or label like ${searchName} limit #{offset},#{limit}")
    List<Video> getSearchVideo(@Param("searchName") String searchName, @Param("offset") Integer offset, @Param("limit") Integer limit);
}
