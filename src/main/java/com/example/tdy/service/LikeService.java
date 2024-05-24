package com.example.tdy.service;

import com.example.tdy.entity.LikeVideo;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    //判断是否点赞
   public boolean judgeLikeState (Integer uid,Integer vid);
   //点赞
   public void addLike(Integer uid,Integer vid);

    void cancelLike(Integer uid, Integer vid);
}
