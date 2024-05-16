package com.example.tdy.enums;

import org.springframework.stereotype.Component;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */

public enum ContentType {
    Video("视频"),
    Title("标题"),
    Image("图片"),
    Description("描述"),
    COVER("封面"),
    DEFAULT("默认");

    public String getDesc() {
        return desc;
    }

    final String desc;

    ContentType(String desc) {
        this.desc = desc;
    }
}
