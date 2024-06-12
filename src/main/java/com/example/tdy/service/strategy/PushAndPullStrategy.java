package com.example.tdy.service.strategy;

import com.example.tdy.entity.Video;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/6/12
 */
@Component
@ConditionalOnProperty(value = "tdy.feed-strategy", havingValue = "push&pull")
public class PushAndPullStrategy implements FeedStrategy {
    @Override
    public void initFollowFeed(Integer userId, List<Integer> followIds) {

    }

    @Override
    public void onVideoPublish(Video video) {
        // 对于活跃的粉丝，采用推模式

        // 对于离线的，采取拉模式，即等其上线再执行拉
    }
}
