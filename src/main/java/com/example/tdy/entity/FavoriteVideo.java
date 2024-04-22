package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */
@Data
public class FavoriteVideo {
    private Integer id;
    private Integer favoriteId;
    private Integer videoId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
