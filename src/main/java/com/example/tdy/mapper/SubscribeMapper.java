package com.example.tdy.mapper;

import com.example.tdy.entity.Subscribe;
import com.example.tdy.entity.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */

@Mapper
public interface SubscribeMapper {
    void insertBatch(@Param("subs") List<Subscribe> subscribes);

    @Select("select * from subscribe where user_id = #{userId}")
    List<Subscribe> selectByUserId(Integer userId);


}
