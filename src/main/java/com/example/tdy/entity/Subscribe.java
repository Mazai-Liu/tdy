package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */

@Data
public class Subscribe {
    private Integer id;
    private Integer userId;
    private Integer typeId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Subscribe(Integer userId, Integer typeId) {
        this.userId = userId;
        this.typeId = typeId;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
