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

    @Select("select id,likes,shares,favorites,browses,comments,title,create_time from video where open = 1 and audit_status = 1")
    List<Video> selectALl();

    @Select("select * from video ")
    List<Video> selectALlTest();

    @Select("select id,likes,shares,favorites,browses,comments,title,create_time from video" +
            " where open = 1 and audit_status = 1" +
            " limit #{start}, #{count}")
    List<Video> selectPatch(@Param("start") int start, @Param("count") int count);


    @Update("update video set comments = comments + 1 where id = #{videoId}")
    void plusComments(Integer videoId);

    @Select("select * from video " +
            "where title like concat('%'',${searchName}, ''%') " +
            "or description like concat('%'',${searchName}, ''%') " +
            "or label like concat('%'',${searchName}, ''%') " +
            "limit #{offset},#{limit}")
    List<Video> getSearchVideo(@Param("searchName") String searchName, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Update("update video set shares = shares + 1 where id = #{vid}")
    void share(Integer vid);

    @Update("update video set likes = likes + #{count} where id = #{vid}")
    void plusLike(@Param("vid") Integer vid, @Param("count") int count);
}
