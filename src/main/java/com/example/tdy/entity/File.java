package com.example.tdy.entity;

import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */

@Data
public class File {
    private Integer id;
    private String fileKey;
    private String type;
    private String format;
    private Long size;
    private Integer userId;
}
