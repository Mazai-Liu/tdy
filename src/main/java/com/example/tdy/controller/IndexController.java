package com.example.tdy.controller;

import com.example.tdy.context.BaseContext;
import com.example.tdy.entity.HotVideo;
import com.example.tdy.entity.Type;
import com.example.tdy.entity.Video;
import com.example.tdy.result.BasePage;
import com.example.tdy.result.PageResult;
import com.example.tdy.result.R;
import com.example.tdy.service.TypeService;
import com.example.tdy.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Vector;

/**
 * @author Mazai-Liu
 * @time 2024/4/7
 */

@RestController
@RequestMapping("/index")
@Validated
public class IndexController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private TypeService typeService;

    @GetMapping("/video/user")
    public R<PageResult<Video>> getVideoByUserId(@NotNull Integer userId, BasePage basePage) {
        PageResult<Video> pageResult = videoService.getVideoByUserId(userId, basePage);

        return R.ok(pageResult);
    }

    @GetMapping("/types")
    public R<List<Type>> getTypes() {
        List<Type>  types = typeService.getTypes();
        return R.ok(types);
    }

    @GetMapping("/video/hot")
    public R<List<Video>> getHotVideo() {
        List<Video> videos = videoService.getHotVideo();
        return R.ok(videos);
    }

    @GetMapping("/video/hot/rank")
    public R<List<HotVideo>> getHotVideoRank() {
        List<HotVideo> videos = videoService.getHotVideoRank();
        return R.ok(videos);
    }

    @GetMapping("/video/similar")
    public R<List<Video>> getSimilarVideo(@NotNull Video video) {
        List<Video> videos = videoService.getSimilarVideo(video);
        return R.ok(videos);
    }

    @GetMapping("/pushVideos")
    public R<List<Video>> pushVideos(HttpServletRequest request){
        Integer userId = BaseContext.getCurrentId();
        List<Video> videos = videoService.pushVideos(userId);
        return R.ok(videos);
    }
    @GetMapping("/search")
    public R<PageResult<Video>> searchVideos(@NotBlank @RequestParam("searchName") String searchName, @RequestParam("page") Integer page, @RequestParam("limit") Integer limit){
        PageResult<Video> videos = videoService.getSearchVideo(searchName,page,limit);
        return R.ok(videos);
    }
    @GetMapping("/video/type/{typeId}")
    public R<PageResult<Video>> type(@PathVariable("typeId") @NotNull Integer typeId,@RequestParam("page")Integer page,@RequestParam("limit")Integer limit){
        PageResult<Video> pageResult =  videoService.getTypeVideo(typeId,page,limit);
        return R.ok(pageResult);
    }

    @GetMapping("/video/{videoId}")
    public R<Video> type(@PathVariable("videoId") @NotNull Integer videoId){
        Video video =  videoService.getVideoById(videoId);
        return R.ok(video);
    }
}
