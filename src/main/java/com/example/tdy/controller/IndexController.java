package com.example.tdy.controller;

import com.example.tdy.entity.Video;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.result.R;
import com.example.tdy.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mazai-Liu
 * @time 2024/4/7
 */

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/video/user")
    public R<PageResult<Video>> getVideoByUserId(Integer userId, BasePage basePage) {
        PageResult<Video> pageResult = videoService.getVideoByUserId(userId, basePage);

        return R.ok(pageResult);
    }
}
