package com.example.tdy.mapper;

import com.example.tdy.entity.Follow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Mapper
public interface FollowMapper {

    @Delete("delete from follow where user_id = #{userId} and follow_id = #{followId}")
    void deleteByUserId(Integer userId, Integer followId);

    @Insert("insert into follow(user_id, follow_id, create_time) values(#{userId}, #{followId}, #{createTime})")
    void insert(Follow follow);

    @Select("select * from follow where user_id = #{userId}")
    List<Follow> getByUserId(Integer userId);

    @Select("select * from follow where follow_id = #{followId}")
    List<Follow> getByFollowId(Integer followId);
}
