package com.example.tdy.service.impl;

import com.example.tdy.dto.CommentAddDto;
import com.example.tdy.entity.Comment;
import com.example.tdy.exception.BaseException;
import com.example.tdy.service.CommentService;
import com.example.tdy.service.UserService;
import com.example.tdy.utils.CozeUtil;
import com.example.tdy.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mazai-Liu
 * @time 2024/7/4
 */
@Service
public class CozeServiceImpl {

    Logger logger = LoggerFactory.getLogger(CozeServiceImpl.class);
    @Autowired
    private ThreadPoolUtil threadPoolUtil;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CozeUtil cozeUtil;

    @Autowired
    private UserService userService;

    public void botReply(Comment comment) throws BaseException {
        threadPoolUtil.submit(() -> {
            CommentAddDto commentAddDto = new CommentAddDto();
            BeanUtils.copyProperties(comment, commentAddDto);

            commentAddDto.setUserId(3);

            commentAddDto.setContent(cozeUtil.getBotReply(comment));

            commentAddDto.setCid(comment.getId());
            commentAddDto.setReplyToUserid(comment.getUserId());
            commentAddDto.setReplyToUsername(userService.getUserVoById(comment.getUserId()).getNickname());

            try {
                logger.info("开始添加评论");
                commentService.add(commentAddDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
