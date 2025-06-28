package com.fzb.service.impl;

import com.fzb.mapper.VideoMapper;
import com.fzb.pojo.*;
import com.fzb.service.VideoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;

    @Override
    public void updateprivate(Integer id){
        videoMapper.updateprivate(id);
    }

    @Override
    public void updatepublic(Integer id){
        videoMapper.updatepublic(id);
    }

    /**
     * 根据id获取searchVideoResult格式视频数据
     * @param videoId
     * @return
     */
    public SearchVideoResult getPlayingvideoById(String username, Integer videoId){
        SearchVideoResult playingvideo = videoMapper.getPlayingvideoById(username, videoId);
        return playingvideo;
    }

    /**
     * 搜索视频结果
     * @param username
     * @param keyword
     * @return
     */
    public List<SearchVideoResult> searchvideos(String username, String keyword){
        List<SearchVideoResult> searchvideoslist = videoMapper.list9(username,keyword);
        return searchvideoslist;
    }

    /**
     * 查询自己的作品
     * @param username
     * @return
     */
    @Override
    public List<Video> getmyworks(String username){
        List<Video> myworkslist = videoMapper.list4(username);
        return myworkslist;
    }

    /**
     * 查询自己的喜欢
     * @param username
     * @return
     */
    @Override
    public List<LikeVideo> getmylikes(String username){
        List<LikeVideo> mylikeslist = videoMapper.list5(username);
        return mylikeslist;
    }

    /**
     * 查询自己的收藏
     * @param username
     * @return
     */
    @Override
    public List<LikeVideo> getmycollects(String username){
        List<LikeVideo> mycollectslist = videoMapper.list7(username);
        return mycollectslist;
    }

    @Override
    public PageBean page5(Integer page, Integer pageSize, String username) {
        PageHelper.startPage(page,pageSize);

        //2.执行查询
        List<RandomVideo> videoList = videoMapper.list8(username);
        Page<RandomVideo> p = (Page<RandomVideo>) videoList;

        //3.封装pageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }


    @Override
    public PageBean page4(Integer page, Integer pageSize, String username) {
        PageHelper.startPage(page,pageSize);

        //2.执行查询
        List<RandomVideo> videoList = videoMapper.list6(username);
        Page<RandomVideo> p = (Page<RandomVideo>) videoList;

        //3.封装pageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 私密视频查询
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean page3(Integer page, Integer pageSize, Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end) {
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);

        //2.执行查询
        List<Video> videoList = videoMapper.list3(authorId, videoTitle, begin, end);
        Page<Video> p = (Page<Video>) videoList;

        //3.封装pageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 审核中视频分页查询
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean page2(Integer page, Integer pageSize, Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end) {
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);

        //2.执行查询
        List<Video> videoList = videoMapper.list2(authorId, videoTitle, begin, end);
        Page<Video> p = (Page<Video>) videoList;

        //3.封装pageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 视频信息查询
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean page(Integer page, Integer pageSize, Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end) {
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);

        //2.执行查询
        List<Video> videoList = videoMapper.list(authorId, videoTitle, begin, end);
        Page<Video> p = (Page<Video>) videoList;

        //3.封装pageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public void InsertByUsername(String videoUrl, String coverUrl, String videoTitle, String username) {
        Video video = new Video();
        video.setVideoUrl(videoUrl);
        video.setCoverUrl(coverUrl);
        video.setVideoTitle(videoTitle);
        video.setLikeBase(0);
        video.setStatus((short)1);
        video.setCreateTime(LocalDateTime.now());
        video.setUpdateTime(LocalDateTime.now());
        videoMapper.insertByUsername(video,username);
    }

    /**
     * 喜欢触发切换
     * @param videoId
     * @param username
     */
    public void checkLike(Integer videoId, String username){
        if(videoMapper.checkLike(videoId,username)){//点赞了就取消
            log.info("{}已经点赞视频{}",username,videoId);
            videoMapper.deletelike(videoId,username);

        }else{//没点赞就增加
            log.info("{}未点赞视频{}",username,videoId);
            videoMapper.insertlike(videoId,username);
        }
    }

    /**
     * 收藏触发切换
     * @param videoId
     * @param username
     */
    public void checkCollect(Integer videoId, String username){
        if(videoMapper.checkCollect(videoId,username)){//收藏了就取消
            log.info("{}已经收藏视频{}",username,videoId);
            videoMapper.deletecollect(videoId,username);

        }else{//没收藏就增加
            log.info("{}未收藏视频{}",username,videoId);
            videoMapper.insertcollect(videoId,username);
        }
    }

    /**
     * 视频分类
     * @param id
     * @param category
     */
    public void updatecategory(Integer id, String category){
        videoMapper.updatecategory(id,category);
    }

    /**
     * 根据用户名和视频ID增加点击次数
     * @param username
     * @param videoId
     */
    public void addview(String username, Integer videoId){
        if(videoMapper.checkView(videoId,username)){
            log.info("{}观看过视频{}",username,videoId);
        }else{
            log.info("{}未观看过视频{}",username,videoId);
        }
        videoMapper.insertView(videoId,username);
    }

    /**
     * 查询所有视频
     * @return
     */
    @Override
    public PageBean videolist(){
        List<VideoIdTitleType> videoList = videoMapper.videoList();
        long total = videoList.size();
        PageBean pageBean = new PageBean(total, videoList);
        return pageBean;
    }

    /**
     * 查询每个视频的评论情感偏向和同类型视频情感偏向
     * @param id
     * @return
     */
    @Override
    public VideoAlgorithm videoAlgorithmData(Integer id){
        List<AlgorithmChart> videoList = videoMapper.videoSentimentList(id);
        List<AlgorithmChart> typeList = videoMapper.typeSentimentList(id);
        long total = videoList.size() + typeList.size();
        VideoAlgorithm videoAlgorithm = new VideoAlgorithm(total,videoList,typeList);
        return videoAlgorithm;
    }
}
