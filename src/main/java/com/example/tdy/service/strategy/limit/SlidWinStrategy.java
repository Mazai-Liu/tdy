package com.example.tdy.service.strategy.limit;

import com.example.tdy.annotation.AccessLimit;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Mazai-Liu
 * @time 2024/7/5
 */
@Component
@ConditionalOnProperty(value = "tdy.limit-strategy", havingValue = "slid", matchIfMissing = true)
public class SlidWinStrategy implements AccessLimitStrategy {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doLimit(AccessLimit accessLimit) throws BaseException {
        Integer userId = BaseContext.getCurrentId();
        String key = RedisConstant.ACCESS_LIMIT_SLIDWIN + userId;

        int count = accessLimit.count();
        TimeUnit timeUnit = accessLimit.timeUnit();
        int time = accessLimit.time();

        long currentTimeMillis = System.currentTimeMillis();
        long timeMill = timeUnit.toMillis(time);

        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, currentTimeMillis - timeMill);
        Long size = stringRedisTemplate.opsForZSet().size(key);
        System.out.println("当前窗口内请求数: " + size);
        if (size != null && count < size) {
            throw new BaseException(SystemConstant.VISITOR_LIMIT);
        }

        stringRedisTemplate.opsForZSet().add(key, UUID.randomUUID().toString(), currentTimeMillis);
    }
}