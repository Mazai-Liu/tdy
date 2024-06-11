package com.example.tdy.service.strategy;

import com.example.tdy.entity.Video;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/6/11
 */
public interface FeedStrategy {

    void initFollowFeed(Integer userId, List<Integer> followIds);

    List<Integer> followFeed(Integer userId, Long lastTime);
}
