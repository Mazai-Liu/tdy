package com.example.tdy.service.impl;

import com.example.tdy.context.BaseContext;
import com.example.tdy.entity.User;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.UserMapper;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.service.FollowService;
import com.example.tdy.service.UserService;
import com.example.tdy.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowService followService;


    @Override
    public User getById() {
        Integer currentId = BaseContext.getCurrentId();
        User user = getById(currentId);
        user.setPassword("*******");
        return user;
    }

    @Override
    public User getById(Integer id) {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        return userMapper.selectByUserIds(ids).get(0);
    }

    @Override
    public PageResult<User> getFollows(Integer userId, BasePage basePage) {
        System.out.println(1);
        PageResult<User> page = new PageResult<>();
        // 获取所有关注的人
        List<Integer> followsIds = followService.getFollows(userId, basePage);
        if(ObjectUtils.isEmpty(followsIds)) {
            return page;
        }

        // 获取粉丝
        List<Integer> fansIds = followService.getFans(userId, basePage);
        if(ObjectUtils.isEmpty(fansIds)) {
            return page;
        }

        System.out.println(2);

        // 获取交集
        Set<Integer> set = new HashSet<>(followsIds);
        List<Integer> intersection = new ArrayList<>();
        for (Integer id : fansIds) {
            if(set.contains(id)) {
                intersection.add(id);
            }
        }

        System.out.println(3);

        // 获取所有follows
        List<User> users = userMapper.selectByUserIds(followsIds);

        // 对于交集，设置互关属性
        users.stream().forEach(user -> {
            if(intersection.contains(user.getId()))
                user.setEach(true);
        });

        System.out.println(4);

        // 获取分页数据
        users = getPaged(users, basePage);

        // 封装结果
        page.setRecords(users);
        page.setTotal(users.size());

        return page;
    }

    public PageResult<User> getFans(Integer userId, BasePage basePage) {
        PageResult<User> page = new PageResult<>();
        // 获取粉丝
        List<Integer> fansIds = followService.getFans(userId, basePage);
        if(ObjectUtils.isEmpty(fansIds)) {
            return page;
        }

        // 获取所有关注的人
        List<Integer> followsIds = followService.getFollows(userId, basePage);
        if(ObjectUtils.isEmpty(followsIds)) {
            return page;
        }

        // 获取交集
        Set<Integer> set = new HashSet<>(followsIds);
        List<Integer> intersection = new ArrayList<>();
        for (Integer id : fansIds) {
            if(set.contains(id)) {
                intersection.add(id);
            }
        }

        // 获取所有fans
        List<User> users = userMapper.selectByUserIds(fansIds);

        // 对于交集，设置互关属性
        users.stream().forEach(user -> {
            if(intersection.contains(user.getId()))
                user.setEach(true);
        });

        // 获取分页数据
        users = getPaged(users, basePage);

        // 封装结果
        page.setRecords(users);
        page.setTotal(users.size());

        return page;
    }

    private static List<User> getPaged(List<User> users, BasePage basePage) {
        if(users.size() < basePage.getLimit()) {
            basePage.setLimit(users.size());
        }
        // 获取分页数据
        return users.subList((basePage.getPage() - 1) * basePage.getLimit(),
                (basePage.getPage() - 1) * basePage.getLimit() + basePage.getLimit());
    }

    @Override
    public void follow(Integer followId) throws BaseException {
        Integer currentId = BaseContext.getCurrentId();
        // 已关注，则取消关注
        if(followService.isFollow(currentId, followId)) {
            followService.cancelFollow(currentId, followId);
            return;
        }
        // 否则，关注
        followService.follow(currentId, followId);
    }

    public UserVO getUserVoById(Integer userId) {
        User user = userMapper.selectByUserId(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}

























