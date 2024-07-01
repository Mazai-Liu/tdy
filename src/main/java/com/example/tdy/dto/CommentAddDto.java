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


    // 被回复评论的id
    private Integer replyToCid;
    // 被回复的用户id
    private Integer replyToUserid;
    // 被回复的用户名
    private String replyToUsername;
}
