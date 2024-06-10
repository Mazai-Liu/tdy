package com.example.tdy.service.impl;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.dto.UerModelDTO;
import com.example.tdy.entity.User;
import com.example.tdy.entity.Video;
import com.example.tdy.mapper.VideoMapper;
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
    private VideoMapper videoMapper;

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

            if(entries == null || entries.isEmpty()) {

            } else {
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

                List<Object> list = redisUtil.sRandom(labels);

                // 获取videoIds
                Set<Integer> ids = list.stream().filter(Objects::nonNull).
                        map(id -> Integer.parseInt(id.toString())).
                        collect(Collectors.toSet());

                // 去重
                String key2 = RedisConstant.HISTORY_VIDEO;
                // 封装keys
                Collection<String> keys = new ArrayList<>();
                for (Integer id : ids) {
                    keys.add((key2 + id + ":" + userId));
                }

                List<Object> simpIds = redisUtil.getValues(keys);
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

    @Override
    public void updateUserModel(UerModelDTO userModelDto) {
        Integer currentId = BaseContext.getCurrentId();

        Integer videoId = userModelDto.getId();
        Video video = videoMapper.selectById(videoId);
        List<String> labels = video.buildLabel();

        String key = RedisConstant.USER_MODEL + currentId;

        // key为label，value为分数
        Map<Object, Object> modelMap = stringRedisTemplate.opsForHash().entries(key);
        Double score = userModelDto.getScore();

        labels.forEach(label -> {
            if(modelMap.containsKey(label)) {
                stringRedisTemplate.opsForHash().increment(key, label, score);
            } else {
                stringRedisTemplate.opsForHash().put(key, label, score.toString());
            }
        });
    }


    public Integer randomVideoId(Integer sex) {
        String key = RedisConstant.SYSTEM_STOCK + (sex == 1 ? "美女" : "宠物");
        final Object o = stringRedisTemplate.opsForSet().randomMember(key);
        return o == null ? null : Integer.parseInt(o.toString());
    }

    private String[] getProbabilityArray(Map<Object, Object> entries) {
        // key: 标签  value：概率数
        Map<String, Integer> probabilityMap = new HashMap<>();

        int size = entries.size();
        AtomicInteger num = new AtomicInteger();
        // 计算概率数
        entries.forEach((k, v) -> {
            double value = Double.parseDouble((String) v);
            if(value < 0)
                value = 0f;
            int p = (int) ((value + size) / size);
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
