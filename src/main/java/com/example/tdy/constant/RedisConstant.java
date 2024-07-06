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
    public static final long FOLLOW_FANS_TIMEOUT = 1;
    public static final TimeUnit FOLLOW_FANS_TIMEOUT_UNIT = TimeUnit.DAYS;


    public static final String USER_MODEL = "user:model:";
    public static final String SYSTEM_STOCK = "system:stock:";


    /**
     * 某视频已推送过用户 history:video:videoId   userId
     */
    public static final String HISTORY_VIDEO = "history:video:";


    public static final String USER_OUTBOX = "user:outbox:";
    public static final String USER_INBOX = "user:inbox:";
    /**
     * 收件箱内容7天过期
     */
    public static final long INBOX_TIMEOUT = 7 * 24 * 60 * 60;
    public static final long DEFAULT_TIMEOUT = 7 * 24 * 60 * 60;

    public static final String HOT_VIDEO = "video:hot:";
    public static final Integer HOT_VIDEO_TIMEOUT = 3;
    public static final TimeUnit HOT_VIDEO_TIMEOUT_UNIT = TimeUnit.DAYS;


    public static final String HOT_VIDEO_RANK = "video:hot:rank:";

    public static final String ACCESS_LIMIT_FIXWIN = "access:limit-fix-win:";
    public static final String ACCESS_LIMIT_SLIDWIN = "access:limit-slid-win:";
    public static final String SEARCH_HISTORY = "search_history:";


}
