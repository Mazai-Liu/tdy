package com.example.tdy.service.impl;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.service.FeedService;
import com.example.tdy.utils.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author Mazai-Liu
 * @time 2024/5/5
 */

@Service
public class FeedServiceImpl implements FeedService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void initFollowFeed(Integer userId, List<Integer> followIds) {
        // 用户收件箱
        String key = RedisConstant.USER_INBOX + userId;

        // 七天内时间
        Date now = new Date();
        Date limit = DateUtil.addDateDays(now, -7);

        Set<ZSetOperations.TypedTuple<String>> set = stringRedisTemplate.opsForZSet().rangeWithScores(key, -1, 1);
        if (!ObjectUtils.isEmpty(set)) {
            // 收件箱有数据，则获取时间窗口内的新视频
            Double oldTime = set.iterator().next().getScore();
            init(userId,oldTime.longValue(),new Date().getTime(),followIds);
        } else {
            // 收件箱无数据，则获取关注人发件箱七天内的视频
            init(userId,limit.getTime(), now.getTime(),followIds);
        }
    }

    public void init(Integer userId, Long min, Long max, List<Integer> followIds) {
        String t1 = RedisConstant.USER_OUTBOX;
        String t2 = RedisConstant.USER_INBOX;

        // 获取所有关注人的发件箱
        final List<Object> result = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Integer followId : followIds) {
                connection.zRevRangeByScoreWithScores((t1 + followId).getBytes(), min, max, 0, 50);
            }
            return null;
        });

        final ObjectMapper objectMapper = new ObjectMapper();
        final HashSet<Integer> ids = new HashSet<>();

        // 放入收件箱
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            // 遍历每个关注的人的发件箱
            for (Object o : result) {
                Set<RedisZSetCommands.Tuple> tuples = (Set<RedisZSetCommands.Tuple>) o;
                // 不为空
                if (!ObjectUtils.isEmpty(tuples)) {
                    // 逐个放入收件箱
                    for (RedisZSetCommands.Tuple tuple : tuples) {
                        final Object value = tuple.getValue();
                        // 放入返回集合
                        ids.add(Integer.valueOf(value.toString()));

                        final byte[] key = (t2 + userId).getBytes();
                        try {
                            // 放入收件箱
                            connection.zAdd(key, tuple.getScore(), objectMapper.writeValueAsBytes(value));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        connection.expire(key, RedisConstant.INBOX_TIMEOUT);
                    }
                }
            }
            return null;
        });
    }
}
