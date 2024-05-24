package com.example.tdy.mapper;

import com.example.tdy.entity.Favorite;
import com.example.tdy.entity.FavoriteVideo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
@Mapper
public interface FavoriteMapper {

    @Insert("insert into favorite(name, size, open, user_id) " +
            "values(#{name}, #{size}, #{open}, #{userId})")
    void insert(Favorite favorite);

    @Select("select * from favorite where user_id = #{userId}")
    List<Favorite> selectByUserId(Integer userId);

    @Select("select * from favorite where id = #{id}")
    Favorite selectById(Integer id);

    void update(Favorite favorite);

    void deleteByIds(List<Integer> ids);

    @Insert("insert into favorite_video(favorite_id, video_id, create_time, update_time) " +
            "values(#{favoriteId}, #{videoId}, #{createTime}, #{updateTime})")
    void insertFavoriteVideo(FavoriteVideo record);

    @Select("select * from favorite_video where favorite_id = #{fid}")
    List<FavoriteVideo> getFavoriteVideoByFavoriteId(Integer fid);
    @Select("select * from favorite_video where favorite_id =#{fid} and video_id = #{vid} ")
    FavoriteVideo  getFavoriteVideoById(@Param("fid") Integer fid, @Param("vid") Integer vid);
    @Delete("delete from favorite_video where favorite_id =#{fid} and video_id = #{vid} ")
    void deleteFavoriteVideoById(@Param("fid") Integer fid, @Param("vid") Integer vid);
}
