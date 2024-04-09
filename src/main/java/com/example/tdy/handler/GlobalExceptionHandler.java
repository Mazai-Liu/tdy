package com.example.tdy.handler;

import com.example.tdy.exception.BaseException;
import com.example.tdy.result.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BaseException.class})
    public R globalException(Exception e) {
        return R.error(e.getMessage());
    }
}
