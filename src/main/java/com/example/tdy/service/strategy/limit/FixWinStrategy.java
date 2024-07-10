package com.example.tdy.service.strategy.limit;

import com.example.tdy.annotation.AccessLimit;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Mazai-Liu
 * @time 2024/7/5
 */

@Component
@ConditionalOnProperty(value = "tdy.limit-strategy", havingValue = "fix")
public class FixWinStrategy implements AccessLimitStrategy{

    Logger logger = LoggerFactory.getLogger(FixWinStrategy.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void doLimit(AccessLimit accessLimit) throws BaseException {
        Integer userId = BaseContext.getCurrentId();
        String method = accessLimit.method();
        String key = RedisConstant.ACCESS_LIMIT_SLIDWIN + method + ":" + userId;

        int count = accessLimit.count();
        TimeUnit timeUnit = accessLimit.timeUnit();
        int time = accessLimit.time();

        String s = stringRedisTemplate.opsForValue().get(key);
        if(s != null && Integer.parseInt(s) >= count) {
            throw new BaseException(SystemConstant.VISITOR_LIMIT);
        }
        logger.info("当前用户: " + userId + ", 窗口内请求数: " + s);
        if(s == null) {
            stringRedisTemplate.opsForValue().set(key, "1", time, timeUnit);
        } else {
            stringRedisTemplate.opsForValue().increment(key);
        }
    }
}
