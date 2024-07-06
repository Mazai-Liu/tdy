package com.example.tdy.mapper;

import com.example.tdy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
@Mapper
public interface UserMapper {

    void update(User user);

    void insert(User user);

    @Select("select * from user where email = #{email}")
    User selectByEmail(String email);

    List<User> selectByUser(User user);

    @Update("update user set default_favorite = #{defaultFavorite}")
    void setDefaultFavorite(User user);

    List<User> selectByUserIds(@Param("ids") List<Integer> userIds);

    @Select("select * from user where id = #{userId}")
    User selectByUserId(Integer userId);

    @Update("update user set avatar = #{fileId} where id = #{userId}")
    void setAvatarFileId(@Param("fileId") Integer fileId, @Param("userId") Integer userId);
}
