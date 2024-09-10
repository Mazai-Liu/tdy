package com.example.tdy.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.entity.*;
import com.example.tdy.entity.task.VideoTask;
import com.example.tdy.enums.AuditStatus;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.*;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.result.R;
import com.example.tdy.service.*;
import com.example.tdy.service.audit.VideoPublishAuditServiceImpl;
import com.example.tdy.utils.FileUtil;
import com.example.tdy.utils.GeneratorUtil;
import com.example.tdy.utils.RedisUtil;
import com.example.tdy.vo.UserVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private TypeMapper typeMapper;
    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FollowService followService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private InterestPushService interestPushService;

    @Autowired
    private InterestPushServiceImpl interestPushServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private VideoPublishAuditServiceImpl videoPublishAuditService;

    final private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void uploadVideo(Video video) throws BaseException {
        final Integer userId = BaseContext.getCurrentId();

        final Integer videoId = video.getId();
        boolean isAdd = true;
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
            // 初始化内容，找机会简化
            // TODO 和创建、修改时间一起，用自定义注解统一注入？
            video.setComments(0);
            video.setLikes(0);
            video.setFavorites(0);
            video.setShares(0);
            video.setBrowses(0);
            video.setOpen(1);


            // 如果没有封面，则设置默认
            if(ObjectUtils.isEmpty(video.getCover())) {
                video.setCover(fileService.setDefaultCover(Integer.parseInt(video.getUrl()), userId));
            }

            // set YV
            video.setLv(GeneratorUtil.generateLvId());

            // 填充视频时长
            String realUrl = fileService.getFileById(Integer.parseInt(video.getUrl())).getFileKey();
            logger.info("填充时长：{}", realUrl);
            video.setDuration(FileUtil.getDuration(realUrl));

            // 填充时间
            video.setCreateTime(LocalDateTime.now());
            video.setUpdateTime(LocalDateTime.now());

            videoMapper.insert(video);

            logger.info("开始视频审核");
            final VideoTask videoTask = new VideoTask();
            videoTask.setOldVideo(video);
            videoTask.setVideo(video);
            videoTask.setIsAdd(isAdd);
            videoTask.setOldState(isAdd ? true : video.getOpen() == 1);
            videoTask.setNewState(true);
            videoPublishAuditService.audit(videoTask);
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
        redisUtil.setExpire(key, RedisConstant.BROWSE_HISTORY_TIMEOUT, RedisConstant.BROWSE_HISTORY_TIMEOUT_UNIT);
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
    public Integer favorite(Integer fid, Integer vid) {
        boolean judged = favoriteService.judgeFavoriteVideoState(fid, vid);
        if(judged==false){
            favoriteService.addFavoriteVideo(fid,vid);

        }else {
            favoriteService.cancelFavoriteVideo(fid,vid);
        }

        favoriteMapper.plusFavorites(vid, judged ? -1 : 1);

        return judged ? 0 : 1;
    }
    @Override
    public List<Video> getByFavoriteId(Integer fid) {
        List<Integer> videoIds = favoriteMapper.getFavoriteVideoByFavoriteId(fid).stream().
                map(FavoriteVideo::getVideoId).
                collect(Collectors.toList());

        return getVideoByIds(videoIds);
    }

    @Override
    public List<Video> pushVideos(Integer userId) {
        User user = null;
        if(userId != null) {
            user = userMapper.selectByUserId(userId);
        }

        // 推送
        Collection<Integer> videoIds = interestPushService.listByUserModel(user);

        if(ObjectUtils.isEmpty(videoIds))
            return new ArrayList<>();

        return getVideoByIds(new ArrayList<>(videoIds));
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
            videos = getVideoByIds(new ArrayList<>(videoIds));

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
        final List<Object> hotVideoIds = redisUtil.getRandomByMap(map);

        if (ObjectUtils.isEmpty(hotVideoIds)) {
            return new ArrayList<>();
        }

        final ArrayList<Integer> videoIds = new ArrayList<>();
        // 会返回结果有null，做下校验
        for (Object o : hotVideoIds) {
            ArrayList<Integer> dayVideoIds = (ArrayList<Integer>) o;
            videoIds.addAll(dayVideoIds);
        }
        if (ObjectUtils.isEmpty(videoIds)){
            return new ArrayList<>();
        }

        // 和浏览记录做交集? 不需要做交集，热门视频和兴趣推送不一样

        return getVideoByIds(videoIds);

    }

    @Override
    public List<Video> getVideo(Integer userId) {
        return videoMapper.selectByUserId(userId);
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
                hotVideo = JSON.parseObject(objectTypedTuple.getValue(), HotVideo.class);
                hotVideo.setHot((double) objectTypedTuple.getScore().intValue());
                hotVideo.hotFormat();
                hotVideos.add(hotVideo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hotVideos;
    }

    @Override
    public List<Video> followFeed(Integer userId, Long lastTime) {
        List<Integer> videoIds = feedService.followFeed(userId, lastTime);

        return getVideoByIds(videoIds);
    }

    @Override
    public void initFollowFeed(Integer userId) {
        List<Integer> followIds = followService.getFollows(userId, null);
        feedService.initFollowFeed(userId, followIds);
    }

    @Override
    public Integer like(Integer vid, Integer uid) {
        //判断是否点赞
        boolean judged = likeService.judgeLikeState(uid,vid);
        //实现点赞功能
        //为true 说明没点赞
        if (judged == true) {
            likeService.addLike(uid,vid);
        }else {//已经点过了
            likeService.cancelLike(uid,vid);
        }

        videoMapper.plusLike(vid, judged ? 1 : -1);

        return judged ? 1 : 0;
    }

    @Override
    public void share(Integer vid) {
        videoMapper.share(vid);

        // 更新用户模型
        String key = RedisConstant.USER_MODEL + BaseContext.getCurrentId();
        interestPushServiceImpl.updateModel(key, vid, SystemConstant.SHARE_PLUS_MODEL);
    }

    @Override
    public PageResult<Video> getSearchVideo(String searchName,Integer page,Integer limit) {

        PageResult<Video> pageResult = new PageResult<>();
        List<Video> videoList = videoMapper.getSearchVideo(searchName,(page-1)*limit,limit);
        pageResult.setRecords(videoList);
        pageResult.setTotal(videoList.size());

        Integer currentId = BaseContext.getCurrentId();

        // 放入搜索记录
        stringRedisTemplate.opsForZSet().add(RedisConstant.SEARCH_HISTORY + currentId, searchName, System.currentTimeMillis());


        return pageResult;
    }

    @Override
    public PageResult<Video> getTypeVideo(Integer type ,Integer page,Integer limit) {
        PageResult<Video> pageResult = new PageResult<>();
        List<Video> videos = typeMapper.selectByTypeId(type,(page-1)*limit,limit);

        // 封装userVO
        videos.forEach(v -> {
            v.setUser(userService.getUserVoById(v.getUserId()));
        });

        pageResult.setRecords(videos);
        pageResult.setTotal(videos.size());
        return pageResult;
    }

    @Override
    public List<Video> getAllOkVideo() {

        return videoMapper.selectALl();
    }

    @Override
    public List<Video> getPatchOkVideo(int start, int count) {
        return videoMapper.selectPatch(start, count);
    }

    @Override
    public Video getVideoById(Integer videoId) {
        Video video = videoMapper.selectById(videoId);
        video.setUser(userService.getUserVoById(video.getUserId()));
        return video;
    }

    @Override
    public List<String> getSearchHistory() {
        Integer currentId = BaseContext.getCurrentId();
        Set<String> strings = stringRedisTemplate.opsForZSet().reverseRange(RedisConstant.SEARCH_HISTORY + currentId, 0, -1);

        return ObjectUtils.isEmpty(strings) ?  new ArrayList<>() : new ArrayList<>(strings);
    }


    private List<Video> getVideoByIds(List<Integer> videoIds) {
        if(videoIds == null || videoIds.isEmpty())
            return new ArrayList<>();

        // TODO 顺序？
        List<Video> videos = videoMapper.selectByIds(videoIds);

        videos.forEach(video -> {
            video.setUser(userService.getUserVoById(video.getUserId()));
        });

        return videos;
    }
}
