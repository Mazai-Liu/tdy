package com.example.tdy.mapper;

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
public interface VideoMapper {
    void insert(Video video);

    void update(Video video);

    List<Video> selectByUserId(Integer userId);

    List<Video> selectByIds(@Param("ids") List<Integer> ids);

    @Select("select * from video where id = #{videoId}")
    Video selectById(Integer videoId);

    @Select("select id,likes,shares,favorites,browses,comments,title,create_time from video where open = 1 and audit_status = 1")
    List<Video> selectALl();
}
