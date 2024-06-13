package com.example.tdy.service;

import com.example.tdy.entity.Video;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/5/5
 */
public interface FeedService {

    void initFollowFeed(Integer userId, List<Integer> follows);

    List<Integer> followFeed(Integer userId, Long lastTime);
}
