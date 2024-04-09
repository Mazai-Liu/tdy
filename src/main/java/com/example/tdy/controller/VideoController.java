package com.example.tdy.controller;

import com.example.tdy.constant.SystemConstant;
import com.example.tdy.constant.VideoConstant;
import com.example.tdy.entity.Video;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.result.R;
import com.example.tdy.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("")
    public R uploadVideo(@RequestBody Video video) throws BaseException {
        videoService.uploadVideo(video);
        return R.ok(VideoConstant.WAIT_AUDIT);
    }

    @PostMapping("/history/{videoId}")
    public void getVideoByUserId(@PathVariable Integer videoId) {
        videoService.addHistory(videoId);
    }

    @GetMapping("/history")
    public R<Map<String, List<Video>>> getHistory(BasePage basePage) {
        Map<String, List<Video>> result = videoService.getHistory(basePage);
        return R.ok(result);
    }
}
