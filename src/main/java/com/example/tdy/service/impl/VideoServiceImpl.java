package com.example.tdy.service.impl;

import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.VideoConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.entity.User;
import com.example.tdy.entity.Video;
import com.example.tdy.enums.AuditStatus;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.UserMapper;
import com.example.tdy.mapper.VideoMapper;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.service.FileService;
import com.example.tdy.service.UserService;
import com.example.tdy.service.VideoService;
import com.example.tdy.utils.FileUtil;
import com.example.tdy.utils.GenerateIdUtil;
import com.example.tdy.utils.RedisUtil;
import com.example.tdy.vo.UserVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void uploadVideo(Video video) throws BaseException {
        final Integer userId = BaseContext.getCurrentId();

        final Integer videoId = video.getId();
        // 修改视频
        if (videoId != null) {
            // TODO 新、旧url不能一致
            if (false) {
                throw new BaseException(ExceptionConstant.UPDATE_VIDEO_ERROR);
            }

            //...

            // 保存
            // videoMapper.update(video);
        }
        // 新增视频
        else {
            video.setUserId(userId);
            video.setAuditStatus(AuditStatus.ING.getCode());
            video.setLabel(video.getLabel());

            // 如果没有封面，则设置默认
            if(ObjectUtils.isEmpty(video.getCover())) {
                video.setCover(fileService.setDefaultCover(video.getUrl(), userId));
            }

            // set YV
            video.setLv(GenerateIdUtil.generateLvId());

            // 填充视频时长
            // TODO
            video.setDuration(FileUtil.getDuration(video.getUrl()));

            // 填充时间
            video.setCreateTime(LocalDateTime.now());
            video.setUpdateTime(LocalDateTime.now());

            videoMapper.insert(video);
        }


    }

    @Override
    public PageResult<Video> getVideoByUserId(Integer userId, BasePage basePage) {
        PageResult<Video> videoPageResult = new PageResult<>();
        // TODO 查缓存

        // 查库
//        PageHelper.startPage(basePage.getPage(), basePage.getLimit());

        List<Video> videoList = videoMapper.selectByUserId(userId);
//        Page<Video> page = (Page<Video>) videoList;

        User user= userMapper.selectByUserId(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        videoList.forEach(video -> {
            video.setUser(userVO);
        });

        videoPageResult.setRecords(videoList);
        videoPageResult.setTotal(videoList.size());


        return videoPageResult;
    }

    @Override
    public void addHistory(Integer videoId) {
        Integer currentId = BaseContext.getCurrentId();
        String key = RedisConstant.VIDEO_HISTORY + currentId;

        stringRedisTemplate.opsForZSet().add(key, videoId + "", new Date().getTime());
        stringRedisTemplate.expire(key, RedisConstant.BROWSE_HISTORY_TIMEOUT, RedisConstant.BROWSE_HISTORY_TIMEOUT_UNIT);
    }



    @Override
    public Map<String, List<Video>> getHistory(BasePage basePage) {
        Integer currentId = BaseContext.getCurrentId();
        String key = RedisConstant.VIDEO_HISTORY + currentId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.zSetGetByPage(key, basePage.getPage(), basePage.getLimit());
        if(typedTuples == null)
            return null;

        Map<String, List<Video>> result = new HashMap<>();
        // 视频
        List<Integer> videoIds = typedTuples.stream().map(tuple -> Integer.parseInt(tuple.getValue().toString())).collect(Collectors.toList());
        Map<Integer, Video> videos = videoMapper.selectByIds(videoIds).stream().collect(Collectors.toMap(Video::getId, video -> video));

        // 时间，按天分割
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        typedTuples.forEach(tuple -> {
            final Date date = new Date(tuple.getScore().longValue());
            final String format = simpleDateFormat.format(date);
            if (!result.containsKey(format)) {
                result.put(format, new ArrayList<>());
            }

            Video video = videos.get(Integer.parseInt(tuple.getValue()));
            video.setUser(userService.getUserVoById(video.getUserId()));

            result.get(format).add(video);
        });

        return result;
    }
}
