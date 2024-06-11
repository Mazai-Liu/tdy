package com.example.tdy.inteceptor;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mazai-Liu
 * @time 2024/3/24
 */

@Component
public class TokenInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 校验token
        String token = request.getHeader(SystemConstant.TOKEN_HEADER);

        String key = RedisConstant.TOKEN_PREFIX + token;
        // userid
        String data = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(data)) {
            // 前端有点问题，所以先把这块关了
            // response.setStatus(HttpStatus.UNAUTHORIZED.value());
            // return false;
            return true;
        }

        // 设置ThreadLocal
        Integer id = Integer.parseInt(data);
        BaseContext.setCurrentId(id);

        logger.info("用户登录: {}", id);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }
}
