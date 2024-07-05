package com.example.tdy.aspect;

import com.example.tdy.annotation.AccessLimit;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.exception.BaseException;
import com.example.tdy.service.strategy.limit.AccessLimitStrategy;
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
    private AccessLimitStrategy limitStrategy;

    @Before("@annotation(accessLimit)")
    public void before(JoinPoint joinPoint, AccessLimit accessLimit) throws Throwable {
        limitStrategy.doLimit(accessLimit);
    }
}
