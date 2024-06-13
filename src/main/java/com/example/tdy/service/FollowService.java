package com.example.tdy.service;

import com.example.tdy.exception.BaseException;
import com.example.tdy.result.BasePage;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */
public interface FollowService {
    List<Integer> getFollows(Integer userId, BasePage basePage);

    List<Integer> getFans(Integer userId, BasePage basePage);

    boolean isFollow(Integer currentId, Integer followId);

    void cancelFollow(Integer currentId, Integer followId);

    void follow(Integer currentId, Integer followId) throws BaseException;
}
