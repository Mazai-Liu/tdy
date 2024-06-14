package com.example.tdy.schedule;


import com.alibaba.fastjson.JSON;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.entity.HotVideo;
import com.example.tdy.entity.Video;
import com.example.tdy.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Mazai-Liu
 * @time 2024/6/13
 */
@Component
public class HotVideoSchedule {
    Logger logger = LoggerFactory.getLogger(HotVideoSchedule.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final int WEIGHT_BROWSE = 1;
    private static final int WEIGHT_LIKE = 1 << 1;

    private static final int WEIGHT_FAVORITE = 1 << 2;

    private static final int WEIGHT_SHARE = 1 << 3;

    private static final int WEIGHT_COMMENT = 1 << 4;

    private static final double HOT_THRESHOLD = 1;
    // 假设视频半衰期是3天
    private static final long HALF_LIFE_T = 3 * 24 * 60 * 60 * 1000;

    private static final int RANK_NUM = 100;


//    @Scheduled(cron = "0 0/1 * * * ?")
    public void hotVideoRank() {
        logger.info("定时任务， 获取热门视频");
        // 每隔三小时获取库中视频计算热度值 = 半衰期公式，放入redis的热门视频以及热门视频排序中
        // TODO 优化效率

        // 获取所有可访问视频
        List<Video> videos = videoService.getAllOkVideo();

        ArrayList<Integer> hotVideoIds = new ArrayList<>();
        // 获取每个视频的热度值，大于阈值就放入热门视频中
        videos.forEach(video -> {
            Integer like = video.getLikes();
            Integer comment = video.getComments();
            Integer share = video.getShares();
            Integer favorite = video.getFavorites();
            Integer browse = video.getBrowses();

            LocalDateTime createTime = video.getCreateTime();
            Long t = System.currentTimeMillis() - createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

            double hot = halfLife(like * WEIGHT_LIKE + comment * WEIGHT_COMMENT + share * WEIGHT_SHARE
                    + favorite * WEIGHT_FAVORITE + browse * WEIGHT_BROWSE, t);
            if (hot > HOT_THRESHOLD) {
                hotVideoIds.add(video.getId());
            }
        });

        if(!ObjectUtils.isEmpty(hotVideoIds)) {
            // 加入redis热度榜中
            String key = RedisConstant.HOT_VIDEO + LocalDateTime.now().getDayOfMonth();
            int n = hotVideoIds.size();
            String[] array = hotVideoIds.stream().map(String::valueOf).collect(Collectors.toList()).toArray(new String[n]);

            stringRedisTemplate.opsForSet().add(key, array);
            stringRedisTemplate.expire(key, RedisConstant.HOT_VIDEO_TIMEOUT, RedisConstant.HOT_VIDEO_TIMEOUT_UNIT);
        }
    }

    private double halfLife(Integer score, Long t) {
        // res = score * (1/2)^(t / T)

        return score * Math.pow(0.5, (double) t / HALF_LIFE_T);
    }


//    @Scheduled(cron = "0 0/1 * * * ?")
    public void hotRank() {
        logger.info("定时任务， 获取热门视频排行榜");

        // 小根堆
        TopK topK = new TopK(10);

        // TODO 优化效率
        // 获取所有可访问视频
        List<Video> videos = videoService.getAllOkVideo();

        videos.forEach(video -> {
            Integer like = video.getLikes();
            Integer comment = video.getComments();
            Integer share = video.getShares();
            Integer favorite = video.getFavorites();
            Integer browse = video.getBrowses();

            LocalDateTime createTime = video.getCreateTime();
            Long t = System.currentTimeMillis() - createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();

            double hot = halfLife(like * WEIGHT_LIKE + comment * WEIGHT_COMMENT + share * WEIGHT_SHARE
                    + favorite * WEIGHT_FAVORITE + browse * WEIGHT_BROWSE, t);

            System.out.println(hot);

            topK.add(new HotVideo(hot, video.getId(), video.getTitle()));
        });

        Double minHot = topK.getMin().getHot();
        final byte[] key = RedisConstant.HOT_VIDEO_RANK.getBytes();

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            PriorityQueue<HotVideo> queue = topK.getQueue();
            for (HotVideo hotVideo : queue) {
                final Double hot = hotVideo.getHot();
                try {
                    hotVideo.setHot(null);
                    // 不这样写铁报错！序列化问题

                    connection.zAdd(key, hot, JSON.toJSONString(hotVideo).getBytes());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
        stringRedisTemplate.opsForZSet().removeRangeByScore(RedisConstant.HOT_VIDEO_RANK, minHot,0);
    }
}
