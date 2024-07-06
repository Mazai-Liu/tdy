package com.example.tdy.service.impl;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.entity.LikeVideo;
import com.example.tdy.mapper.LikeMapper;
import com.example.tdy.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private InterestPushServiceImpl interestPushService;

    @Override
    public boolean judgeLikeState(Integer uid,Integer vid) {
        LikeVideo likeVideo = likeMapper.getLikeByUserId(uid,vid);
        if (likeVideo==null)
            return true;
       return false;
    }
    @Override
    public void addLike(Integer uid, Integer vid) {
        LikeVideo likeVideo = new LikeVideo();
        likeVideo.setVideoId(vid);
        likeVideo.setUserId(uid);
        likeVideo.setCreateTime(LocalDateTime.now());
        likeVideo.setUpdateTime(LocalDateTime.now());
        likeMapper.insertIntoLike(likeVideo);

        // 更新用户模型
        String key = RedisConstant.USER_MODEL + BaseContext.getCurrentId();
        interestPushService.updateModel(key, vid, SystemConstant.LIKE_PLUS_MODEL);
    }

    @Override
    public void cancelLike(Integer uid, Integer vid) {
        likeMapper.deleteLikeById(uid,vid);
    }
}
