package com.example.tdy.controller;

import com.example.tdy.dto.CommentAddDto;
import com.example.tdy.dto.CommentDelDto;
import com.example.tdy.dto.CommentListDto;
import com.example.tdy.entity.Comment;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.PageResult;
import com.example.tdy.result.R;
import com.example.tdy.service.CommentService;
import com.example.tdy.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;


    @GetMapping("/list")
    public R<PageResult<Comment>> list(CommentListDto commentListDto){

        return R.ok(commentService.list(commentListDto));
    }

    @PostMapping("/add")
    public R add(@RequestBody @Validated CommentAddDto commentAddDto) throws BaseException {
        commentService.add(commentAddDto);
        return R.ok();
    }

    @PostMapping("/delete")
    public R delete(@RequestBody @Validated CommentDelDto commentDelDto) throws BaseException {
        commentService.delete(commentDelDto);
        return R.ok();
    }
}
