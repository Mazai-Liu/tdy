package com.example.tdy.controller;

import com.example.tdy.constant.SystemConstant;
import com.example.tdy.constant.VideoConstant;
import com.example.tdy.context.BaseContext;
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
    @PostMapping("/favorites/{fid}/{vid}")
    public R favorite(@PathVariable Integer fid, @PathVariable Integer vid) {
        videoService.favorite(fid, vid);
        return R.ok("已收藏");
    }

    @GetMapping("/favorite/{fid}")
    public R<List<Video>> getFavorite(@PathVariable Integer fid) {
        List<Video> videos = videoService.getByFavoriteId(fid);
        return R.ok(videos);
    }

    /**
     * 从收件箱获取关注的人的视频，。拉模式（从关注的人的发件箱 拉到 自己的收件箱）
     * @param lastTime  上次推送的时间
     * @return
     */
    @GetMapping("/follow/feed")
    public R<List<Video>> followFeed(@RequestParam(required = false) Long lastTime) {
        Integer userId = BaseContext.getCurrentId();

        List<Video> videos = videoService.followFeed(userId, lastTime);
        return R.ok(videos);
    }

    /**
     * 初始化收件箱
     * @return
     */
    @PostMapping("/init/follow/feed")
    public R initFollowFeed(){
        Integer userId = BaseContext.getCurrentId();
        videoService.initFollowFeed(userId);
        return R.ok();
    }
}
