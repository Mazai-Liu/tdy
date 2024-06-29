package com.example.tdy.service;

import com.example.tdy.dto.UerModelDTO;
import com.example.tdy.entity.Favorite;
import com.example.tdy.entity.Subscribe;
import com.example.tdy.entity.Type;
import com.example.tdy.entity.User;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.vo.UserVO;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */
public interface UserService {
    UserVO getById();

    PageResult<User> getFollows(Integer userId, BasePage basePage);

    PageResult<User> getFans(Integer userId, BasePage basePage);

    void follow(Integer followId) throws BaseException;

    UserVO getById(Integer id);

    UserVO getUserVoById(Integer userId);

    void subscribe(String types);

    List<Type> getSubscribe();

    List<Type> getNoSubscribe();

}
