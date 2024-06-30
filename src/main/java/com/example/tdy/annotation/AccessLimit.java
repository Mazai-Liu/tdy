package com.example.tdy.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mazai-Liu
 * @time 2024/6/15
 */

/**
 * 控制访问速率注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {
    int time();
    int count();
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
