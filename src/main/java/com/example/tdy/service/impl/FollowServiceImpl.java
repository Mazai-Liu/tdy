package com.example.tdy.service.impl;

import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.entity.Follow;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.FollowMapper;
import com.example.tdy.result.BasePage;
import com.example.tdy.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private FollowMapper followMapper;

    @Override
    public List<Integer> getFollows(Integer userId, BasePage basePage) {
        // 从缓存中拿
        String key = RedisConstant.USER_FOLLOW + userId;
        Set<String> data = stringRedisTemplate.opsForZSet().range(key, 0, -1);

        List<Integer> result = null;
        // 没有关注 || 缓存里没有
        if(ObjectUtils.isEmpty(data)) {
            // 从库里查
            List<Follow> follows;
            follows = followMapper.getByUserId(userId);

            // 库里不为空则加入缓存 TODO redis管道优化
            if(!ObjectUtils.isEmpty(follows)) {
                result = follows.stream().map(Follow::getFollowId).collect(Collectors.toList());
                follows.forEach(follow ->
                        stringRedisTemplate.opsForZSet().add(key, follow.getFollowId() + "", new Date().getTime())
                );
            }
        } else {
            result = data.stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public List<Integer> getFans(Integer followId, BasePage basePage) {
        // 从缓存中拿
        String key = RedisConstant.USER_FANS + followId;
        Set<String> data = stringRedisTemplate.opsForZSet().range(key, 0, -1);

        List<Integer> result = null;
        // 同上个方法
        if(ObjectUtils.isEmpty(data)) {
            List<Follow> follows;
            follows = followMapper.getByFollowId(followId);
            if(!ObjectUtils.isEmpty(follows)) {
                result = follows.stream().map(Follow::getUserId).collect(Collectors.toList());
                follows.forEach(follow ->
                        stringRedisTemplate.opsForZSet().add(key, follow.getUserId() + "", new Date().getTime())
                );
            }
        } else {
            result = data.stream().map(Integer::parseInt).collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public boolean isFollow(Integer currentId, Integer followId) {
        // 从缓存中拿
        String key = RedisConstant.USER_FOLLOW + currentId;
        Set<String> data = stringRedisTemplate.opsForZSet().range(key, 0, -1);

        return data.contains(String.valueOf(followId));
    }

    @Override
    public void cancelFollow(Integer currentId, Integer followId) {
        // 删库
        followMapper.deleteByUserId(currentId, followId);

        // 删缓存
        // 删关注
        String key = RedisConstant.USER_FOLLOW + currentId;
        stringRedisTemplate.opsForZSet().remove(key, followId);
        // 删粉丝
        key = RedisConstant.USER_FANS + followId;
        stringRedisTemplate.opsForZSet().remove(key, currentId);
    }

    @Override
    public void follow(Integer currentId, Integer followId) throws BaseException {
        if(currentId.equals(followId)) {
            throw new BaseException(ExceptionConstant.NO_FOLLOW_YOURSELF);
        }

        // 写库
        Follow follow = new Follow();
        follow.setUserId(currentId);
        follow.setFollowId(followId);
        follow.setCreateTime(LocalDateTime.now());
        followMapper.insert(follow);

        // 写缓存
        // 写关注
        String key = RedisConstant.USER_FOLLOW + currentId;
        double score = System.currentTimeMillis();
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(followId), score);
        stringRedisTemplate.expire(key, RedisConstant.FOLLOW_FANS_TIMEOUT, RedisConstant.FOLLOW_FANS_TIMEOUT_UNIT);

        // 写粉丝
        key = RedisConstant.USER_FANS + followId;
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(currentId), score);
    }
}
