package com.example.tdy.mapper;

import com.example.tdy.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */

@Mapper
public interface FileMapper {
    void insert(File file);

    @Select("select * from file where id = #{fileId}")
    File selectById(Integer fileId);
}
