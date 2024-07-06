package com.example.tdy.handler;

import com.example.tdy.exception.BaseException;
import com.example.tdy.result.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = {BaseException.class, ConstraintViolationException.class})
    public R globalException(BaseException e) {
        logger.error("{}", e.getMessage());
        e.printStackTrace();
        return R.error(e.getMessage());
    }


}
