package com.example.tdy.mapper;

import com.example.tdy.entity.FavoriteVideo;
import com.example.tdy.entity.LikeVideo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    @Select("select * from like_video where user_id = #{uid} and video_id = #{vid}")
    LikeVideo getLikeByUserId(@Param("uid") Integer uid, @Param("vid") Integer vid);

    void insertIntoLikeById(Integer uid, Integer vid);
    @Insert("insert into like_video(user_id, video_id, create_time, update_time) " +
            "values(#{userId}, #{videoId}, #{createTime}, #{updateTime})")
    void insertIntoLike(LikeVideo likeVideo);
    @Delete("delete from like_video where user_id=#{uid} and video_id=#{vid}")
    void deleteLikeById(@Param("uid") Integer uid, @Param("vid") Integer vid);
}
