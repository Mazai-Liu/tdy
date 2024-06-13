package com.example.tdy.service.impl;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.service.FeedService;
import com.example.tdy.service.strategy.FeedStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mazai-Liu
 * @time 2024/5/5
 */

@Service
public class FeedServiceImpl implements FeedService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private FeedStrategy feedStrategy;


    @Override
    public void initFollowFeed(Integer userId, List<Integer> followIds) {
        feedStrategy.initFollowFeed(userId, followIds);
    }

    @Override
    public List<Integer> followFeed(Integer userId, Long lastTime) {
        return getFollowFeed(userId, lastTime);
    }

    private List<Integer> getFollowFeed(Integer userId, Long lastTime) {
        // 推拉模式或者结合模式都是要从收件箱力取，可以默认实现
        String key = RedisConstant.USER_INBOX + userId;
        Set<String> videoIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(key,
                0, lastTime == null ? new Date().getTime() : lastTime, lastTime == null ? 0 : 1, 5);
        if(ObjectUtils.isEmpty(videoIds)) {
            // 可能只是缓存中没有了,缓存只存储7天内的关注视频,继续往后查看关注的用户太少了,不做考虑 - feed流必然会产生的问题
            return new ArrayList<>();
        }

        List<Integer> ids = videoIds.stream().map(Integer::parseInt).collect(Collectors.toList());

        return ids;
    }


}
