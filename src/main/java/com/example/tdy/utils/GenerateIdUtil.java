package com.example.tdy.utils;

import java.util.UUID;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */
public class GenerateIdUtil {

    public static String generateLvId() {
        // TODO 设计一个lvId
        return "LV" + UUID.randomUUID().toString().replace("-","").substring(0 ,8);
    }
}
