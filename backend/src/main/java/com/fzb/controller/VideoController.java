package com.fzb.controller;

import com.fzb.pojo.*;
import com.fzb.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class VideoController {
    @Autowired
    VideoService videoService;

    /**
     * 查询所有视频的id、标题和类型
     * @return
     */
    @GetMapping("/videolistalgorithm")
    public Result videolistAlgorithm() {
        log.info("admin查询所有视频id、标题和类型");
        PageBean pageBean = videoService.videolist();
        return Result.success(pageBean);
    }

    /**
     * 查询每个视频的评论情感偏向和该类型视频情感偏向
     * @param id
     * @return
     */
    @GetMapping("/videodataalgorithm")
    public Result videodataAlgorithm(Integer id){
        log.info("admin查询视频{}评论情感偏向和该类型视频情感偏向",id);
        VideoAlgorithm videoData = videoService.videoAlgorithmData(id);
        return Result.success(videoData);
    }

    @PostMapping("/view")
    public Result view(@RequestParam String username,@RequestParam Integer videoId){
        log.info("用户{}点击播放视频，id={},当前观看次数+1",username,videoId);
        videoService.addview(username,videoId);
        return Result.success();
    }

    @GetMapping("/videoplaying")
    public Result videoplaying(@RequestParam String username,@RequestParam Integer videoId) {
        log.info("用户{}点击播放视频，id={},当前观看次数+1",username,videoId);
        SearchVideoResult playingvideo = videoService.getPlayingvideoById(username,videoId);
        videoService.addview(username,videoId);
        return Result.success(playingvideo);
    }

    @GetMapping("/searchVideoResults")
    public Result searchVideo(@RequestParam String username,@RequestParam String keyword) {
        log.info("视频查询关键词：{}",keyword);
        List<SearchVideoResult> searchvideos = videoService.searchvideos(username, keyword);
        return Result.success(searchvideos);
    }

    @PostMapping("/video/updatecategory")
    public Result updateCategory(Integer id, String category) {
        log.info("视频分类");
        videoService.updatecategory(id,category);
        return Result.success();
    }

    /**
     * 视频收藏
     * @param videoId
     * @param username
     * @return
     */
    @PostMapping("/collect")
    public Result collect(@RequestParam Integer videoId, @RequestParam String username) {
        videoService.checkCollect(videoId,username);
        return Result.success();
    }

    /**
     * 视频点赞
     * @param videoId
     * @param username
     * @return
     */
    @PostMapping("/like")
    public Result like(@RequestParam Integer videoId, @RequestParam String username) {
        videoService.checkLike(videoId,username);
        return Result.success();
    }

    /**
     * 下架视频，将视频状态设置为3
     * @param id
     * @return
     */
    @PostMapping("/videos/updateprivate")
    public Result updatePrivate(@RequestParam Integer id) {
        videoService.updateprivate(id);
        return Result.success();
    }

    /**
     * 上架视频，将视频状态设置为2
     * @param id
     * @return
     */
    @PostMapping("/videos/updatepublic")
    public Result updatePublic(@RequestParam Integer id) {
        videoService.updatepublic(id);
        return Result.success();
    }

    @GetMapping("/getmycollects")
    public Result getmycollects(@RequestParam String username) {
        log.info("查询我的收藏");
        List<LikeVideo> mycollects = videoService.getmycollects(username);
        return Result.success(mycollects);
    }

    @GetMapping("/getmylikes")
    public Result getmylikes(@RequestParam String username) {
        log.info("查询我的喜欢");
        List<LikeVideo> mylikes = videoService.getmylikes(username);
        return Result.success(mylikes);
    }

    @GetMapping("/getmyworks")
    public Result getmyworks(@RequestParam String username){
        log.info("查询我的作品");
        List<Video> myworks = videoService.getmyworks(username);
        return Result.success(myworks);
    }

    /**
     * 私密视频分页查询
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/videosremoval")
    public Result page3(@RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(defaultValue = "") Integer authorId,@RequestParam(defaultValue = "") String videoTitle,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime begin,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        log.info("分页查询，参数：{},{},{},{},{},{}",page,pageSize,authorId,videoTitle,begin,end);
        PageBean pageBean = videoService.page3(page,pageSize,authorId,videoTitle,begin,end);
        return Result.success(pageBean);
    }

    /**
     * 分页查询状态为1，审核中的视频
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/videoscheck")
    public Result page2(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(defaultValue = "") Integer authorId,@RequestParam(defaultValue = "") String videoTitle,
                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime begin,
                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        log.info("分页查询，参数：{},{},{},{},{},{}",page,pageSize,authorId,videoTitle,begin,end);
        PageBean pageBean = videoService.page2(page,pageSize,authorId,videoTitle,begin,end);
        return Result.success(pageBean);
    }

    /**
     * 分页查询所有
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/videos")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(defaultValue = "") Integer authorId,@RequestParam(defaultValue = "") String videoTitle,
                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime begin,
                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        log.info("分页查询，参数：{},{},{},{},{},{}",page,pageSize,authorId,videoTitle,begin,end);
        PageBean pageBean = videoService.page(page,pageSize,authorId,videoTitle,begin,end);
        return Result.success(pageBean);
    }

    //随心刷接口
    @GetMapping("/random")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam String username) {
        log.info("随心刷视频查询");
        PageBean pageBean = videoService.page4(page,pageSize,username);
        return Result.success(pageBean);
    }

    //精选接口
    @GetMapping("/select")
    public Result page2(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam String username) {
        log.info("精选视频查询");
        PageBean pageBean = videoService.page5(page,pageSize,username);
        return Result.success(pageBean);
    }

    @PostMapping("/createvideo")
    public Result createVideo(@RequestParam String videoUrl,@RequestParam String coverUrl,@RequestParam String videoTitle,@RequestParam String username) {
        log.info("视频创作");

        videoService.InsertByUsername(videoUrl,coverUrl,videoTitle,username);
        return Result.success();
    }
}


