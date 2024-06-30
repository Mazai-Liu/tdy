package com.example.tdy.service.impl;

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
    }

    @Override
    public void cancelLike(Integer uid, Integer vid) {
        likeMapper.deleteLikeById(uid,vid);
    }
}
