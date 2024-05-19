package com.example.tdy;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.entity.User;
import com.example.tdy.entity.Video;
import com.example.tdy.entity.audit.VideoAudit;
import com.example.tdy.entity.resp.audit.BodyJson;
import com.example.tdy.mapper.UserMapper;
import com.example.tdy.mapper.VideoMapper;
import com.example.tdy.utils.QiniuUtil;
import com.google.gson.Gson;
import com.qiniu.http.Client;
import com.qiniu.storage.model.FileInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class TdyApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    VideoMapper videoMapper;

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
//        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            map.forEach((label, ids) -> {
//                ids.forEach(id -> {
//                    connection.sAdd((key + label).getBytes(), id.getBytes());
//                });
//            });
//
//            return null;
//        });


        long end = System.currentTimeMillis();

        System.out.printf("执行时长：%d 毫秒.", (end - start));
    }

}
