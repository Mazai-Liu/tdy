package com.example.tdy.mapper;

import com.example.tdy.dto.CommentAddDto;
import com.example.tdy.dto.CommentListDto;
import com.example.tdy.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Mapper
public interface CommentMapper {


    List<Comment> selectByDto(CommentListDto dto);

    void insert(Comment comment);

    @Update("update comment set reply_count = reply_count + #{count} where id = #{cid}")
    void replyPlus(@Param("cid") Integer cid, @Param("count") int count);

    @Delete("update comment set is_delete = 1 where id = #{cid}")
    void delete(Integer cid);
}
