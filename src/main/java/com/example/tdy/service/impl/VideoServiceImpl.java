package com.example.tdy.service.impl;

import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.entity.FavoriteVideo;
import com.example.tdy.entity.HotVideo;
import com.example.tdy.entity.User;
import com.example.tdy.entity.Video;
import com.example.tdy.enums.AuditStatus;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.FavoriteMapper;
import com.example.tdy.mapper.UserMapper;
import com.example.tdy.mapper.VideoMapper;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.service.FileService;
import com.example.tdy.service.InterestPushService;
import com.example.tdy.service.UserService;
import com.example.tdy.service.VideoService;
import com.example.tdy.utils.FileUtil;
import com.example.tdy.utils.GenerateIdUtil;
import com.example.tdy.utils.RedisUtil;
import com.example.tdy.vo.UserVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
    private InterestPushService interestPushService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FavoriteMapper favoriteMapper;

    final private ObjectMapper objectMapper = new ObjectMapper();

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

            // 加入用户发件箱
            // TODO 审核后加入
            redisUtil.addOutbox(userId, video);
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
        // 获取当前用户的浏览记录
        Integer currentId = BaseContext.getCurrentId();
        String key = RedisConstant.VIDEO_HISTORY + currentId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.zSetGetByPage(key, basePage.getPage(), basePage.getLimit());
        if(typedTuples == null)
            return null;

        Map<String, List<Video>> result = new HashMap<>();
        // 获取到视频
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

    @Override
    public void favorite(Integer fid, Integer vid) {
        FavoriteVideo record = new FavoriteVideo();
        record.setVideoId(vid);
        record.setFavoriteId(fid);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        favoriteMapper.insertFavoriteVideo(record);
    }

    @Override
    public List<Video> getByFavoriteId(Integer fid) {
        List<Integer> videoIds = favoriteMapper.getFavoriteVideoByFavoriteId(fid).stream().
                map(FavoriteVideo::getVideoId).
                collect(Collectors.toList());

        return videoMapper.selectByIds(videoIds);
    }

    @Override
    public List<Video> pushVideos(Integer userId) {
        User user = null;
        if(userId != null) {
            user = userMapper.selectByUserId(userId);
        }

        // 推送
        Collection<Integer> videoIds = interestPushService.listByUserModel(user);

        // 获取视频
        List<Video> videos = videoMapper.selectByIds((List<Integer>) videoIds);

        // 封装userVO
        // TODO 可能还需要补充url
        videos.forEach(video -> {
            video.setUser(userService.getUserVoById(video.getUserId()));
        });

        return videos;
    }

    @Override
    public List<Video> getSimilarVideo(Video video) {
        if(video == null || StringUtils.isEmpty(video.getLabel())) {
            return new ArrayList<>();
        }

        // 获取标签
        List<String> labels = video.buildLabel();

        // 通过标签获取视频
        Set<Integer> videoIds = (Set<Integer>) interestPushService.listByLabels(labels);

        // 去掉当前视频
        videoIds.remove(video.getId());

        // 获取视频
        List<Video> videos = null;
        if(!videoIds.isEmpty()) {
            videos = videoMapper.selectByIds(new ArrayList<>(videoIds));

            // 封装userVO
            // TODO 可能还需要补充url
            videos.forEach(v -> {
                v.setUser(userService.getUserVoById(v.getUserId()));
            });

        }

        return videos;
    }

    @Override
    public List<Video> getHotVideo() {
        Calendar calendar = Calendar.getInstance();
        // 该月第几天
        int today = calendar.get(Calendar.DATE);

        final HashMap<String, Integer> map = new HashMap<>();
        // 优先推送今日的
        map.put(RedisConstant.HOT_VIDEO + today, 10);
        map.put(RedisConstant.HOT_VIDEO + (today - 1), 3);
        map.put(RedisConstant.HOT_VIDEO + (today - 2), 2);

        // redis执行获取返回值
        final List<Object> hotVideoIds = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            map.forEach((k, v) -> {
                connection.sRandMember(k.getBytes(), v);
            });
            return null;
        });
        if (ObjectUtils.isEmpty(hotVideoIds)) {
            return new ArrayList<>();
        }


        final ArrayList<Integer> videoIds = new ArrayList<>();
        // 会返回结果有null，做下校验
        for (Object videoId : hotVideoIds) {
            if (!ObjectUtils.isEmpty(videoId)) {
                videoIds.add((Integer) videoId);
            }
        }
        if (ObjectUtils.isEmpty(videoIds)){
            return new ArrayList<>();
        }


        final List<Video> videos = videoMapper.selectByIds(videoIds);
        // 和浏览记录做交集? 不需要做交集，热门视频和兴趣推送不一样

        // 封装userVO
        // TODO 可能还需要补充url
        videos.forEach(v -> {
            v.setUser(userService.getUserVoById(v.getUserId()));
        });

        return videos;

    }

    @Override
    public List<HotVideo> getHotVideoRank() {
        // 从Redis中取出
        // value是热度视频的json，scores是热度
        final Set<ZSetOperations.TypedTuple<String>> zSet = stringRedisTemplate.opsForZSet().reverseRangeWithScores(RedisConstant.HOT_VIDEO_RANK, 0, -1);
        final ArrayList<HotVideo> hotVideos = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> objectTypedTuple : zSet) {
            final HotVideo hotVideo;
            try {
                hotVideo = objectMapper.readValue(objectTypedTuple.getValue().toString(), HotVideo.class);
                hotVideo.setHot((double) objectTypedTuple.getScore().intValue());
                hotVideo.hotFormat();
                hotVideos.add(hotVideo);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return hotVideos;
    }

}
