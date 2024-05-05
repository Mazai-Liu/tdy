package com.example.tdy.utils;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mazai-Liu
 * @time 2024/4/9
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void addOutbox(Integer userId, Video video) {
        String key = RedisConstant.USER_OUTBOX + userId;
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(video.getId()),
                video.getCreateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }

    public List<Object> sRandom(List<String> keys){
        final List<Object> list = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String key : keys) {
                connection.sRandMember(key.getBytes());
            }
            return null;
        });
        // 可能会有null
        final List result = new ArrayList();
        for (Object aLong : list) {
            if (aLong!=null){
                result.add(aLong);
            }
        }
        return result;
    }

    public Set<ZSetOperations.TypedTuple<String>> zSetGetByPage(String key, int pageNum, int pageSize) {
        try {
            if (stringRedisTemplate.hasKey(key)) {
                long start = (pageNum - 1) * pageSize;
                long end = pageNum * pageSize - 1;
                Long size = stringRedisTemplate.opsForZSet().size(key);
                if (end > size) {
                    end = -1;
                }

                return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key,start,end);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
