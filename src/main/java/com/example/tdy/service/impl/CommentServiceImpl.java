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
import com.example.tdy.utils.CozeUtil;
import com.example.tdy.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Service
public class CommentServiceImpl implements CommentService {

    Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ThreadPoolUtil threadPoolUtil;

    @Autowired
    private CozeUtil cozeUtil;


    public static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PageResult<Comment> list(CommentListDto commentListDto) {
        PageResult<Comment> pageResult = new PageResult<>();

        List<Comment> comments = commentMapper.selectByDto(commentListDto);
        // 填充回复和用户信息
        Integer videoId = commentListDto.getVideoId();
        comments = buildComments(videoId, comments);

        // TODO 排序等


        // 查看评论总数，有点小差距问题不大
        Video video = videoMapper.selectById(commentListDto.getVideoId());

        pageResult.setRecords(comments);
        pageResult.setTotal(video.getComments());

        return pageResult;
    }


    private List<Comment> buildComments(Integer videoId, List<Comment> comments) {
        comments.stream().forEach(comment -> {
            // 设置用户信息
           comment.setUser(userService.getUserVoById(comment.getUserId()));
//            comment.setUserVO(userService.getUserVoById(3));

            // 设置回复
            if(comment.getReplyCount() != 0) {
                buildReplies(videoId, comment);
            }

            // 设置时间格式
            buildTime(comment);
        });

        return buildOrder(comments);
    }

    private List<Comment>  buildOrder(List<Comment> comments) {
        return comments.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).collect(Collectors.toList());
    }

    private void buildTime(Comment comment) {
        comment.getCreateTime().format(format);
    }

    private void buildReplies(Integer videoId, Comment comment) {
        // 要以该一级评论的id为rootId查询
        Integer rootId = comment.getId();
        Integer currentId = BaseContext.getCurrentId();

        List<Comment> replies = commentMapper.selectByDto(new CommentListDto(videoId, rootId, 0, 2));
        replies.forEach(reply -> {
          reply.setUser(userService.getUserVoById(reply.getUserId()));
          buildTime(reply);
          if(reply.getUserId().equals(currentId))
              reply.setReplicable(0);
        });
        replies = buildOrder(replies);

        comment.setReplies(replies);
        comment.setMore(comment.getReplyCount() - comment.getReplies().size());
    }

    private void checkBeforeAdd(CommentAddDto commentAddDto) throws BaseException {
        Integer commentUserid = commentAddDto.getUserId();
        Integer currentId = BaseContext.getCurrentId();
//        if(currentId == null || !commentUserid.equals(currentId)) {
//            // 未登录或水平越权评论
//            throw new BaseException(SystemConstant.COMMENT_NOAUTH);
//        }

        // 自己评论自己
        Integer commentedUserId = commentAddDto.getReplyToUserid();
        if(commentedUserId != null && commentAddDto.getRootId() != 0 && commentedUserId.equals(commentUserid)) {
            throw new BaseException(SystemConstant.COMMENT_SELF);
        }
    }

    @Transactional
    public void add(CommentAddDto commentAddDto) throws BaseException {

        checkBeforeAdd(commentAddDto);

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentAddDto, comment);

        Integer toCommentedCid = commentAddDto.getCid();
        Integer toCommentedUserId = commentAddDto.getReplyToUserid();
        comment.setParentId(toCommentedCid);

        Integer rootId = comment.getRootId();
        // root为0两种情况：
        // 1. 回复视频
        // 2. 回复一级评论
        if(rootId == 0) {
            // 情况1
            if(toCommentedUserId == null) {

            }
            // 情况2
            else {
                // 设置新评论的rootId为一级评论的Id
                comment.setRootId(toCommentedCid);
                // 根的回复数+1
                commentMapper.replyPlus(toCommentedCid, 1);
            }
        }
        // rootId不为0。说明回复的是二级评论
        else {
            // 该条评论的回复数+1
            commentMapper.replyPlus(toCommentedCid, 1);
            // 根的回复数+1
            commentMapper.replyPlus(rootId, 1);
            // 设置新评论的rootId为老的
            comment.setRootId(rootId);

            comment.setReplyToReplyId(toCommentedCid);

        }

        comment.setCreateTime(LocalDateTime.now());

        commentMapper.insert(comment);

        // 视频的评论数+1
        videoMapper.plusComments(comment.getVideoId());

        // 触发 bot 回复
        if(isNeedBot(comment)) {
            logger.info("触发 bot 回复");
            botReply(comment);
        }
        else
            logger.info("未触发 bot 回复");
    }


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
                add(commentAddDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean isNeedBot(Comment comment) {
        // 作者评论被回复 或 作者视频被回复，且是非自己评论
        Integer replyToUserid = comment.getReplyToUserid();
        return comment.getUserId() != 3 &&
                ((replyToUserid != null && replyToUserid == 3) || videoMapper.selectById(comment.getVideoId()).getUserId() == 3);
    }



    @Override
    public void delete(CommentDelDto commentDelDto) {
        // TODO 是自己的评论，或者是删除自己视频下的评论

        commentMapper.delete(commentDelDto.getCid());
    }

    @Override
    public void like(Integer commentId) {
        if(commentId != null) {
            commentMapper.like(commentId);
        }
    }

    public Comment getById(Integer cid) {
        return commentMapper.selectById(cid);
    }
}
