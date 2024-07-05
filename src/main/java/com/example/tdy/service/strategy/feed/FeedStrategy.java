package com.example.tdy.service.strategy.feed;

import com.example.tdy.entity.Video;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/6/11
 */
public interface FeedStrategy {

    /**
     * 初始化用户的推送流
     * @param userId 用户id
     * @param followIds 用户关注的人的id
     */
    void initFollowFeed(Integer userId, List<Integer> followIds);

    /**
     * 当视频发布时，不同的推送策略需要进行不同的处理
     * @param video 发布的新视频
     */
    void onVideoPublish(Video video);

}
