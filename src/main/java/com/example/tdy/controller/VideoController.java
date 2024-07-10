package com.example.tdy.controller;

import com.example.tdy.annotation.AccessLimit;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */

@RestController
@RequestMapping("/video")
@Validated
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("")
    @AccessLimit(count = 1, time = 60, method = "uploadVideo")
    public R uploadVideo(@NotNull @RequestBody Video video) throws BaseException {
        videoService.uploadVideo(video);
        return R.ok(VideoConstant.WAIT_AUDIT);
    }

    @PostMapping("/history/{videoId}")
    public void getVideoByUserId(@NotNull @PathVariable Integer videoId) {
        videoService.addHistory(videoId);
    }

    @GetMapping("/history")
    public R<Map<String, List<Video>>> getHistory(BasePage basePage) {
        Map<String, List<Video>> result = videoService.getHistory(basePage);
        return R.ok(result);
    }
    @PostMapping("/favorites/{fid}/{vid}")
    public R favorite(@NotNull @PathVariable Integer fid, @NotNull  @PathVariable Integer vid) {
        Integer result = videoService.favorite(fid, vid);
        if(result==1) {
            return R.okWithMessage(SystemConstant.OK_FAVORITE);
        }
        return R.okWithMessage(SystemConstant.CANCEL_FAVORITE);
    }

    @GetMapping("/favorites/{fid}")
    public R<List<Video>> getFavorite(@NotNull @PathVariable Integer fid) {
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
        if(userId == null)
            return R.ok();
        videoService.initFollowFeed(userId);
        return R.ok();
    }
    @PostMapping("/star/{vid}")
    public R like(@NotNull @PathVariable Integer vid){
        Integer uid = BaseContext.getCurrentId();
        Integer star =  videoService.like(vid,uid);
        if(star == 1) {
            return R.okWithMessage(SystemConstant.OK_LIKE);
        }else
            return R.okWithMessage(SystemConstant.CANCEL_LIKE);
    }

}
