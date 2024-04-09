package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
@Data
public class Favorite {
    private Integer id;
    private String name;
    private Integer size;
    private Integer open;
    private Integer userId;
    private Integer state;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
