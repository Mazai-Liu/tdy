package com.example.tdy.service;

import com.example.tdy.entity.Video;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */
public interface VideoService {
    void uploadVideo(Video video) throws BaseException;

    PageResult<Video> getVideoByUserId(Integer userId, BasePage basePage);

    /**
     * 添加视频浏览历史记录，过期时间为7天
     * 使用redis zset实现，同时记录时间和保证id不重复
     * @param videoId
     */
    void addHistory(Integer videoId);

    /**
     * 获取用户浏览历史记录
     * @param basePage
     * @return
     */
    Map<String, List<Video>> getHistory(BasePage basePage);

    void favorite(Integer fid, Integer vid);

    List<Video> getByFavoriteId(Integer fid);
}
