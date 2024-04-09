package com.example.tdy.constant;

import java.util.concurrent.TimeUnit;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
public class RedisConstant {
    public static final String VERIFY_PREFIX = "verify_code:";
    public static final Integer VERIFY_TIMEOUT = 10;

    public static final String EMAIL_PREFIX = "email_code:";
    public static final Integer MAIL_TIMEOUT = 10;



    public static final String TOKEN_PREFIX = "login:token:";
    public static final long TOKEN_TIMEOUT = 7;
    public static final TimeUnit TOKEN_TIMEOUT_UNIT = TimeUnit.DAYS;
    public static final String USER_FOLLOW = "user:follow:";

    public static final String USER_FANS = "user:fans:";
    public static final String VIDEO_HISTORY = "video:history:";
    public static final long BROWSE_HISTORY_TIMEOUT = 7;
    public static final TimeUnit BROWSE_HISTORY_TIMEOUT_UNIT = TimeUnit.DAYS;
}
