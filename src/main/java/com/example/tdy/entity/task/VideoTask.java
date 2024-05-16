package com.example.tdy.entity.task;

import com.example.tdy.entity.Video;
import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */

@Data
public class VideoTask {
    // 新视频
    private Video video;

    // 老视频
    private Video oldVideo;

    // 是否是新增
    private Boolean isAdd;

    // 老状态 : 0 公开  1 私密
    private Boolean oldState;

    private Boolean newState;
}
