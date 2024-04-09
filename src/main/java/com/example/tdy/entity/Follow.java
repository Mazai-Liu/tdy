package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Data
public class Follow {
    private Integer id;
    private Integer userId;
    private Integer followId;
    private LocalDateTime createTime;
}
