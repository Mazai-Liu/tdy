package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */

@Data
public class Type {
    private Integer id;
    private String name;
    private String description;
    private Integer open;
    private String label;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
