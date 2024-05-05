package com.example.tdy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@Data
public class User {
    private Integer id;
    private String phone;
    private String email;
    private String password;
    private Integer avatar;
    private String nickname;
    private String description;
    private Integer gender;
    private Integer state;
    private Integer defaultFavorite;
    private Integer follow;
    private Integer fans;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Boolean each;
}
