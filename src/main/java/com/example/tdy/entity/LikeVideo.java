package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class LikeVideo {
    private Integer id;
    private Integer userId;
    private Integer videoId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
