package com.example.tdy.service.impl;

import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.dto.CommentAddDto;
import com.example.tdy.dto.CommentDelDto;
import com.example.tdy.dto.CommentListDto;
import com.example.tdy.entity.Comment;
import com.example.tdy.entity.Video;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.CommentMapper;
import com.example.tdy.mapper.VideoMapper;
import com.example.tdy.result.PageResult;
import com.example.tdy.service.CommentService;
import com.example.tdy.service.UserService;
import com.example.tdy.service.VideoService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired

    private UserService userService;

    public PageResult<Comment> list(CommentListDto commentListDto) {
        PageResult<Comment> pageResult = new PageResult<>();

        List<Comment> comments = commentMapper.selectByDto(commentListDto);

        Integer videoId = commentListDto.getVideoId();

        buildReplies(videoId, comments);



        Video video = videoMapper.selectById(commentListDto.getVideoId());

        pageResult.setRecords(comments);
        pageResult.setTotal(video.getComments());

        return pageResult;
    }




    private void buildReplies(Integer videoId, List<Comment> comments) {
        comments.forEach(comment -> {
//            comment.setUserVO(userService.getUserVoById(comment.getUserId()));
            comment.setUserVO(userService.getUserVoById(3));
            if(comment.getReplyCount() != 0) {
                Integer rootId = comment.getId();

                comment.setReplies(commentMapper.selectByDto(new CommentListDto(videoId, rootId, 0, 2)));

                comment.getReplies().forEach(reply -> {
                    reply.setUserVO(userService.getUserVoById(3));
//                    reply.setUserVO(userService.getUserVoById(comment.getUserId()));
                });

                comment.setMore(comment.getReplyCount() - comment.getReplies().size());
            }
        });
    }

    private void checkBeforeAdd(CommentAddDto commentAddDto) throws BaseException {
        Integer commentUserid = commentAddDto.getUserId();
//        Integer currentId = BaseContext.getCurrentId();
//        if(currentId == null ||!commentUserid.equals(currentId)) {
//            // 未登录或水平越权评论
//            throw new BaseException(SystemConstant.COMMENT_NOAUTH);
//        }

        // 自己评论自己
        Integer replyToId = commentAddDto.getReplyToUserid();
        if(replyToId != null && replyToId.intValue() == commentUserid) {
            throw new BaseException(SystemConstant.COMMENT_SELF);
        }
    }

    @Transactional
    public void add(CommentAddDto commentAddDto) throws BaseException {

        checkBeforeAdd(commentAddDto);

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddDto, comment);

        // 如果是 回复某评论 而不是 回复视频，则设置parentId?
        Integer cid = comment.getReplyToCid();
        if(cid != null) {
            // 回复评论，设置parent，回复数+1
            comment.setParentId(cid);
            commentMapper.replyPlus(cid, 1);

            // 回复的是根级评论，即rootId是0。
            Integer rootId = comment.getRootId();
            if(rootId == 0) {
                comment.setRootId(cid);
            } else {
                // 回复的不是根级评论，根的评论也要+1
                comment.setRootId(rootId);
                commentMapper.replyPlus(rootId, 1);
            }

        }

        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        // 视频的评论数+1
        videoMapper.plusComments(comment.getVideoId());
    }

    @Override
    public void delete(CommentDelDto commentDelDto) {
        // TODO 是自己的评论，或者是删除自己视频下的评论

        commentMapper.delete(commentDelDto.getCid());
    }

}
