package com.example.tdy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShareMapper {
    @Select("select * from video where id = #{vid}")
    public void  getVideoById( @Param("vid") Integer vid);
}
