package com.example.tdy.mapper;

import com.example.tdy.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Select("select * from video")
    List<Video> selectALl();
    @Update("update video set favorites = favorites + 1 where type_id = #{fid} and id = #{vid}")
    void updateVideoFavoritesById(@Param("fid") Integer fid,@Param("vid") Integer vid);
    @Update("update video set likes = likes + 1 where user_id = #{uid} and id = #{vid}")
    void updateVideoLikesById(@Param("uid") Integer uid, @Param("vid") Integer vid);
}
