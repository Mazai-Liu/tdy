package com.example.tdy.service.strategy.limit;

import com.example.tdy.annotation.AccessLimit;
import com.example.tdy.exception.BaseException;

/**
 * @author Mazai-Liu
 * @time 2024/7/5
 */
public interface AccessLimitStrategy {
    void doLimit(AccessLimit accessLimit) throws BaseException;
}
