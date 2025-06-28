package com.fzb.service;

import com.fzb.pojo.*;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoService {

    /**
     * 视频结果搜索
     * @param username
     * @param keyword
     * @return
     */
    public List<SearchVideoResult> searchvideos(String username, String keyword);

    /**
     * 根据用户名创建视频
     * @param username
     */
    public void InsertByUsername(String videoUrl, String coverUrl, String videoTitle, String username);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    public PageBean page(Integer page, Integer pageSize, Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end);

    /**
     * 精选视频分页查询
     * @param page
     * @param pageSize
     * @return
     */
    public PageBean page5(Integer page, Integer pageSize, String username);

    /**
     * 随心刷分页查询
     * @param page
     * @param pageSize
     * @return
     */
    public PageBean page4(Integer page, Integer pageSize, String username);


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
    public PageBean page2(Integer page, Integer pageSize, Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end);

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
    public PageBean page3(Integer page, Integer pageSize, Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end);

    /**
     * 查询所有的我的作品
     * @param username
     * @return
     */
    public List<Video> getmyworks(String username);

    /**
     * 查询所有我的喜欢
     * @param username
     * @return
     */
    public List<LikeVideo> getmylikes(String username);

    /**
     * 查询所有我的收藏
     * @param username
     * @return
     */
    public List<LikeVideo> getmycollects(String username);

    /**
     * 上架视频
     * @param id
     */
    public void updatepublic(Integer id);

    /**
     * 下架视频
     * @param id
     */
    public void updateprivate(Integer id);

    /**
     * 喜欢切换
     * @param videoId
     * @param username
     */
    public void checkLike(Integer videoId, String username);

    /**
     * 收藏切换
     * @param videoId
     * @param username
     */
    public void checkCollect(Integer videoId, String username);

    /**
     * 视频分类
     * @param id
     * @param category
     */
    public void updatecategory(Integer id, String category);

    /**
     * 根据id获取searchVideoResult格式视频数据
     * @param videoId
     * @return
     */
    public SearchVideoResult getPlayingvideoById(String username ,Integer videoId);

    /**
     * 根据用户名和视频ID增加点击次数
     * @param username
     * @param videoId
     */
    public void addview(String username, Integer videoId);

    /**
     * 查询所有视频id、标题和类型
     * @return
     */
    public PageBean videolist();

    /**
     * 查询每个视频的评论情感偏向和该类型视频情感偏向
     * @param id
     * @return
     */
    public VideoAlgorithm videoAlgorithmData(Integer id);
}
