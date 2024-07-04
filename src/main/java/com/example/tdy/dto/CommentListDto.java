package com.example.tdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mazai-Liu
 * @time 2024/7/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentListDto {
    private Integer videoId;
    private Integer rootId = 0;

    private Integer offset;
    private Integer count;



    public CommentListDto(Integer videoId, Integer rootId) {
        this.videoId = videoId;
        this.rootId = rootId;
    }

}
