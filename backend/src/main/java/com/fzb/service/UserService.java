package com.fzb.service;

import com.fzb.pojo.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理
 */
public interface UserService {

    /**
     * 新增用户
     * @param user
     */
    public void insertUser(User user);


    /**
     *员工登录
     * @param user
     * @return
     */
    public User login(User user);

    /**
     * 检查用户名是否存在
     * @param
     * @return
     */
    public User checkUser(String username);

    /**
     *分页查询
     * @param page
     * @param pageSize
     * @param name
     * @param gender
     * @param begin
     * @param end
     * @return
     */
    public PageBean page(Integer page, Integer pageSize, String name, Short gender, LocalDateTime begin, LocalDateTime end);

    /**
     * 根据username查询图像、关注、粉丝
     * @param username
     * @return
     */
    public UserInfo getUserInfo(String username);

    /**
     * 根据username查询图像、关注、粉丝并查询关系
     * @param username
     * @param myusername
     * @return
     */
    public UserInfo getUserInfo2(String username, String myusername);

    /**
     * 更新用户头像
     * @param username
     * @param image
     */
    public void updateImage(String username, String image);

    /**
     * 更新用户性别
     * @param username
     * @param gender
     */
    public void updateGender(String username, Integer gender);

    /**
     * 更新用户签名
     * @param username
     * @param signature
     */
    public void updateSignature(String username, String signature);

    /**
     * 更新用户信息
     * @param id
     * @param name
     * @param gender
     * @param image
     */
    public void updateUser(Integer id, String name, Short gender, String image);

    /**
     * 关注切换
     * @param authorUsername
     * @param username
     */
    public void checkFollow(String authorUsername, String username);

    /**
     * 查询所有的我的关注
     * @param username
     * @return
     */
    public List<followUser> getmyfollows(String username);

    /**
     * 查询所有的我的粉丝
     * @param username
     * @return
     */
    public List<followUser> getmyfans(String username);

    /**
     * 根据关键词查询用户
     * @param username
     * @param keyword
     * @return
     */
    public List<SearchUserResult> searchusers(String username, String keyword);

    /**
     * 查询所有用户id和用户名
     * @return
     */
    public PageBean userlist();

    /**
     * 查询所有用户的内容标签偏好和评论情感分析偏好
     * @return
     */
    public UserAlgorithm userAlgorithmData(Integer id);
}
