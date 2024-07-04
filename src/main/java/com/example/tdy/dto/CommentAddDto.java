package com.example.tdy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Data
public class CommentAddDto {
    @NotNull
    private Integer videoId;

    private Integer rootId = 0;

    @NotNull
    private Integer userId;
    private Integer parentId = 0;

    @NotBlank
    private String content;

    private Integer cid;

    // 被回复的二级评论 的id。为0则说明是以一级评论，或回复一级评论的评论
    private Integer replyToReplyId = 0;

    // 被回复的用户id
    private Integer replyToUserid;

    // 被回复的用户名
    private String replyToUsername;
}
