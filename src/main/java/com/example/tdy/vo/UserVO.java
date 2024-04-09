package com.example.tdy.vo;

import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/4/9
 */

@Data
public class UserVO {
    private Integer id;
    private Integer avatar;
    private String nickname;
    private Integer sex;
    private Integer follows;
    private Integer fans;
    private String description;
}
