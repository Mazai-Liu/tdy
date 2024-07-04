package com.example.tdy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Data
public class CommentDelDto {
    @NotNull
    private Integer cid;

    @NotNull
    private Integer userId;
}
