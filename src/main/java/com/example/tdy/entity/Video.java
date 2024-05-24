package com.example.tdy.entity;

import com.example.tdy.utils.QiniuUtil;
import com.example.tdy.vo.UserVO;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */

@Data
public class Video {
    private Integer id;
    private String title;
    private String description;
    private String label;
    private String url;
    private String cover;
    private String lv;
    private String duration;

    private UserVO user;
    /**
     * 浏览时间，用于浏览记录
     */
    private LocalDateTime time;

    /**
     * 公开状态：公开1、私密0，默认私密
     */
    private Integer open = 0;
    private Integer userId = 0;
    private Integer browses = 0;
    private Integer likes = 0;
    private Integer comments = 0;
    private Integer favorites = 0;
    private Integer shares = 0;

    /**
     * 审核状态：0审核中，1审核通过，2审核不通过
     */
    private Integer auditStatus;
    private String auditMsg;

    private Integer typeId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public List<String> buildLabel(){
        if (ObjectUtils.isEmpty(this.label)) return Collections.EMPTY_LIST;
        return Arrays.asList(this.label.split(","));
    }

    // 和get方法分开，避免发生歧义
    public String buildVideoUrl(){
        return QiniuUtil.CNAME + "/" + this.url;
    }

    public String buildCoverUrl(){
        return QiniuUtil.CNAME + "/" + this.cover;
    }
}
