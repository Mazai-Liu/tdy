package com.example.tdy.mapper;

import com.example.tdy.entity.Favorite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
