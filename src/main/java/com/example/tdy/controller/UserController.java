package com.example.tdy.controller;

import com.example.tdy.dto.UerModelDTO;
import com.example.tdy.dto.UpdateFavoriteDto;
import com.example.tdy.entity.Favorite;
import com.example.tdy.entity.Type;
import com.example.tdy.entity.User;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.result.R;
import com.example.tdy.service.FavoriteService;
import com.example.tdy.service.InterestPushService;
import com.example.tdy.service.UserService;
import org.apache.ibatis.annotations.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@RestController
@RequestMapping("/customer")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private InterestPushService interestPushService;

    @PostMapping("/follows")
    public R follow(Integer followId) throws BaseException {
        userService.follow(followId);
        return R.ok();
    }

    @GetMapping("/fans")
    public R<PageResult<User>> getFans(Integer userId, BasePage basePage) {
        logger.info("获取粉丝，{}", userId);
        PageResult<User> pageResult = userService.getFans(userId, basePage);
        return R.ok(pageResult);
    }

    @GetMapping("/follows")
    public R<PageResult<User>> getFollows(Integer userId, BasePage basePage) {
        logger.info("获取关注，{}", userId);
        PageResult<User> pageResult = userService.getFollows(userId, basePage);
        return R.ok(pageResult);
    }

    @GetMapping("/getInfo")
    public R<User> getInfo() {
        User user = userService.getById();
        return R.ok(user);
    }

    @GetMapping("/getInfo/{id}")
    public R<User> getInfo(@PathVariable Integer id) {
        User user = userService.getById(id);
        return R.ok(user);
    }


    @GetMapping("/favorites")
    public R<List<Favorite>> getFavorites() {
        List<Favorite> favorites = favoriteService.getFavoritesByUserId();
        return R.ok(favorites);
    }

    @GetMapping("/favorites/{id}")
    public R<Favorite> getFavorite(@PathVariable Integer id) {
        Favorite favorite = favoriteService.getFavoriteById(id);
        return R.ok(favorite);
    }

    @PostMapping("/favorites")
    public R updateFavorite(@RequestBody @Validated UpdateFavoriteDto updateFavoriteDto) {
        favoriteService.updateById(updateFavoriteDto);
        return R.ok();
    }

    @DeleteMapping("/favorites/{ids}")
    public R deleteFavorite(@PathVariable("ids") List<Integer> ids) {
        System.out.println(ids);
        favoriteService.deleteByIds(ids);
        return R.ok();
    }

    @PostMapping("/subscribe")
    public R subscribe(String types) {
        userService.subscribe(types);
        return R.ok();
    }

    @GetMapping("/subscribe")
    public R<List<Type>> getSubscribe() {
        List<Type> types = userService.getSubscribe();
        return R.ok(types);
    }

    @GetMapping("/noSubscribe")
    public R<List<Type>> getNoSubscribe() {
        List<Type> types = userService.getNoSubscribe();
        return R.ok(types);
    }

    @PostMapping("/updateUserModel")
    public R updateUserModel(@RequestBody UerModelDTO userModelDto) {
        interestPushService.updateUserModel(userModelDto);
        return R.ok();
    }
}
