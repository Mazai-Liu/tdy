package com.example.tdy.service.strategy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/6/11
 */
@Component
@ConditionalOnProperty(value = "tdy.feed-strategy", havingValue = "push")
public class PushStrategy implements FeedStrategy {
    @Override
    public void initFollowFeed(Integer userId, List<Integer> followIds) {
        // 推模式不用初始化
        return;
    }

    @Override
    public List<Integer> followFeed(Integer userId, Long lastTime) {
        return null;
    }
}
