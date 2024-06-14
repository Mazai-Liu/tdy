package com.example.tdy.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author Mazai-Liu
 * @time 2024/6/14
 */

@Component
public class ThreadPoolUtil {
    private static final int maximumPoolSize = 8;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(5, maximumPoolSize,
            60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }

    public <T> void submit(Runnable runnable, T result) {
        executor.submit(runnable, result);
    }

    public <T> void submit(Callable<T> callback) {
        executor.submit(callback);
    }
}
