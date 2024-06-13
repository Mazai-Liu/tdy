package com.example.tdy.service.strategy;

import com.example.tdy.entity.Video;
import com.example.tdy.service.FollowService;
import com.example.tdy.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FollowService followService;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public void initFollowFeed(Integer userId, List<Integer> followIds) {
        // 推模式不用初始化
        return;
    }

    @Override
    public void onVideoPublish(Video video) {
        // 推模式中，发布的视频要推送到关注者的收件箱
        Integer userId = video.getUserId();
        // 关注者id
        List<Integer> follows = followService.getFollows(userId, null);

        redisUtil.addInbox(follows, video);
    }

}
