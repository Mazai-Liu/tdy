package com.example.tdy.entity;

import com.example.tdy.dto.CommentListDto;
import com.example.tdy.vo.UserVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Data
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer videoId;
    // 一级评论的id
    private Integer rootId;

    // 如果回复小于3，则显示出来
    private List<Comment> replies;
    // 还有几条回复
    private Integer more;
    private Integer isTop = 0;

    private String content;
    private UserVO userVO;

    private Integer likes = 0;
    private Integer dislikes = 0;

    private Integer parentId = 0;
    private Integer replyCount = 0;

    private Integer isDelete = 0;

    private Integer replyToUserid;
    private Integer replyToCid;
    private String replyToUsername;

    private LocalDateTime createTime;
}
