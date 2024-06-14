package com.example.tdy;

import com.alibaba.fastjson.JSON;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.entity.HotVideo;
import com.example.tdy.entity.Video;
import com.example.tdy.mapper.VideoMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.ZoneOffset;
import java.util.*;

@SpringBootTest
class TdyApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    VideoMapper videoMapper;

    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//    ObjectMapper om = new ObjectMapper();
//
//    {
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//    }

    @Test
    void contextLoads() {
        List<Video> videos = videoMapper.selectALl();
        String key = RedisConstant.SYSTEM_STOCK;


        HashMap<String, List<String>> map = new HashMap<>();

        videos.forEach(video -> {
            List<String> labels = video.buildLabel();
            labels.forEach(label -> {
                String id = String.valueOf(video.getId());
                if(map.containsKey(label)) {
                    map.get(label).add(id);
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(id);
                    map.put(label, list);
                }
            });
        });
        System.out.println(map);

        long start = System.currentTimeMillis();

        // 不使用pipeline
        // 执行时长：504 毫秒
//        map.forEach((label, ids) -> {
//            ids.forEach(id -> {
//                stringRedisTemplate.opsForSet().add(key + label, id);
//            });
//        });

        // 使用pipeline
        // 执行时长：498 毫秒?
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            map.forEach((label, ids) -> {
                ids.forEach(id -> {
                    connection.sAdd((key + label).getBytes(), id.getBytes());
                });
            });

            return null;
        });


        long end = System.currentTimeMillis();

        System.out.printf("执行时长：%d 毫秒.", (end - start));
    }

    @Test
    void serializeTest() throws JsonProcessingException {
        HotVideo hotVideo = new HotVideo(12d, 1, "test");

        stringRedisTemplate.opsForZSet().add("test:", JSON.toJSONString(hotVideo)
                , hotVideo.getHot());

        final Set<ZSetOperations.TypedTuple<String>> zSet = stringRedisTemplate.opsForZSet().
                reverseRangeWithScores("test:", 0, -1);

        for (ZSetOperations.TypedTuple<String> objectTypedTuple : zSet) {
            final HotVideo hv;
            try {
                String value = objectTypedTuple.getValue();
                System.out.println(value);
                hv = JSON.parseObject(value, HotVideo.class);
                System.out.println(hv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
