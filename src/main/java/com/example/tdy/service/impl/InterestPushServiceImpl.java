package com.example.tdy.service.impl;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.entity.User;
import com.example.tdy.service.InterestPushService;
import com.example.tdy.service.TypeService;
import com.example.tdy.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Mazai-Liu
 * @time 2024/4/29
 */
@Service
public class InterestPushServiceImpl implements InterestPushService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TypeService typeService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Collection<Integer> listByUserModel(User user) {
        // 创建结果集
        Set<Integer> videoIds = new HashSet<>(10);
        if(user != null) {
            Integer userId = user.getId();
            String key = RedisConstant.USER_MODEL + userId;
            // 获取各标签的概率。key标签，value得分
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
            if(!entries.isEmpty()) {
                // 获取到标签概率数组
                String[] probability = getProbabilityArray(entries);

                // 按照概率选取标签
                Integer sex = user.getGender();
                final Random randomObject = new Random();
                final ArrayList<String> labels = new ArrayList<>();
                // 随机获取X个视频
                for (int i = 0; i < 8; i++) {
                    String labelName = probability[randomObject.nextInt(probability.length)];
                    labels.add(labelName);
                }

                // 通过pipeline加速获取视频ids
                List<Object> list = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                    for (String labelName : labels) {
                        String k = RedisConstant.SYSTEM_STOCK + labelName;
                        connection.sRandMember(k.getBytes());
                    }
                    return null;
                });
                // 获取videoIds
                Set<Integer> ids = list.stream().filter(Objects::nonNull).map(id -> Integer.parseInt(id.toString())).collect(Collectors.toSet());

                // 去重
                String key2 = RedisConstant.HISTORY_VIDEO;
                List<Object> simpIds = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                    for (Integer id : ids) {
                        connection.get((key2 + id + ":" + userId).getBytes());
                    }
                    return null;
                });
                simpIds = simpIds.stream().filter(o->!ObjectUtils.isEmpty(o)).collect(Collectors.toList());;
                if (!ObjectUtils.isEmpty(simpIds)){
                    for (Object simpId : simpIds) {
                        final Long l = Long.valueOf(simpId.toString());
                        ids.remove(l);
                    }
                }

                videoIds.addAll(ids);

                // 随机挑选一个视频,根据性别: 男：美女 女：宠物
                final Integer aLong = randomVideoId(sex);
                if (aLong!=null){
                    videoIds.add(aLong);
                }

                return videoIds;
            }
        }
        // 游客
        // 随机获取10个标签
        final List<String> labels = typeService.random10Labels();
        final ArrayList<String> labelNames = new ArrayList<>();

        int size = labels.size();
        final Random random = new Random();
        // 获取随机的标签
        for (int i = 0; i < 10; i++) {
            final int randomIndex = random.nextInt(size);
            labelNames.add(RedisConstant.SYSTEM_STOCK + labels.get(randomIndex));
        }

        // 获取videoId
        final List<Object> list = redisUtil.sRandom(labelNames);
        if (!ObjectUtils.isEmpty(list)){
            videoIds = list.stream().filter(id ->!ObjectUtils.isEmpty(id)).map(id -> Integer.valueOf(id.toString())).collect(Collectors.toSet());
        }

        return videoIds;
    }

    @Override
    public Collection<Integer> listByLabels(List<String> labels) {
        // 封装标签keys
        List<String> keys = labels.stream().map(label -> RedisConstant.SYSTEM_STOCK + label).collect(Collectors.toList());
        // 获取各标签视频，每个标签随机数量个视频
        List<Object> list = redisUtil.sRandom(keys);
        Set<Integer> videoIds = new HashSet<>();

        if (!ObjectUtils.isEmpty(list)){
            videoIds = list.stream().filter(id ->!ObjectUtils.isEmpty(id)).map(id -> Integer.valueOf(id.toString())).collect(Collectors.toSet());
        }
        return videoIds;
    }



    public Integer randomVideoId(Integer sex) {
        String key = RedisConstant.SYSTEM_STOCK + (sex == 1 ? "美女" : "宠物");
        final Object o = stringRedisTemplate.opsForSet().randomMember(key);
        return Integer.parseInt(o.toString());
    }

    private String[] getProbabilityArray(Map<Object, Object> entries) {
        // key: 标签  value：概率数
        Map<String, Integer> probabilityMap = new HashMap<>();

        int size = entries.size();
        AtomicInteger num = new AtomicInteger();
        // 计算概率数
        entries.forEach((k, v) -> {
            int p = (int) (((double) v + size) / size);
            num.getAndAdd(p);
            probabilityMap.put(k.toString(), p);
        });

        String[] res = new String[num.get()];
        final AtomicInteger index = new AtomicInteger(0);
        // 初始化数组
        probabilityMap.forEach((label, p) -> {
            int i = index.get();
            int limit = i + p;
            while (i < limit) {
                res[i++] = label;
            }
            index.set(limit);
        });

        return res;
    }
}
