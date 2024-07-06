package com.example.tdy.service;

import com.example.tdy.dto.CommentAddDto;
import com.example.tdy.dto.CommentDelDto;
import com.example.tdy.dto.CommentListDto;
import com.example.tdy.entity.Comment;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.PageResult;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
public interface CommentService {
    PageResult<Comment> list(CommentListDto commentListDto);

    void add(CommentAddDto commentAddDto) throws BaseException;

    void delete(CommentDelDto commentDelDto);

    void like(Integer commentId);
}
