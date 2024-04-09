package com.example.tdy.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */
public class LocalCache {
    public static final Map<String, Boolean> CACHE = new ConcurrentHashMap<>();

    public static void set(String key, Boolean value) {
        CACHE.put(key, value);
    }

    public static Boolean get(String key) {
        return CACHE.get(key);
    }

    public static boolean containsKey(String key) {
        return CACHE.containsKey(key);
    }

    public static void remove(String key) {
        CACHE.remove(key);
    }
}
