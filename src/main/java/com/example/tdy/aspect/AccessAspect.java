package com.example.tdy.aspect;

import com.example.tdy.annotation.AccessLimit;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.context.BaseContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Mazai-Liu
 * @time 2024/6/15
 */
@Aspect
@Component
public class AccessAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Before("@annotation(accessLimit)")
    public void before(JoinPoint joinPoint, AccessLimit accessLimit) throws Throwable {
        Integer userId = BaseContext.getCurrentId();

        String key = RedisConstant.ACCESS_LIMIT + userId;
        int count = accessLimit.count();
        TimeUnit timeUnit = accessLimit.timeUnit();
        int time = accessLimit.time();

        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            String s = stringRedisTemplate.opsForValue().get(key);
            if(Integer.parseInt(s) >= count) {
                throw new RuntimeException("访问过于频繁");
            }
            stringRedisTemplate.opsForValue().increment(key);

        } else {
            stringRedisTemplate.opsForValue().set(key, "1", time, timeUnit);
        }
    }
}
