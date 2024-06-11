package com.example.tdy.service.impl;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.entity.Video;
import com.example.tdy.service.FeedService;
import com.example.tdy.service.strategy.FeedStrategy;
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

    @Autowired
    private FeedStrategy feedStrategy;

    @Override
    public void initFollowFeed(Integer userId, List<Integer> followIds) {
        System.out.println("init:" + feedStrategy.getClass());
        feedStrategy.initFollowFeed(userId, followIds);
    }

    @Override
    public List<Integer> followFeed(Integer userId, Long lastTime) {
        System.out.println("get ids:" + feedStrategy.getClass());
        return feedStrategy.followFeed(userId, lastTime);
    }


}
