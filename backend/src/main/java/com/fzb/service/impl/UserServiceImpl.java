package com.fzb.service.impl;

import com.fzb.mapper.UserBehaviorMapper;
import com.fzb.mapper.UserMapper;
import com.fzb.pojo.*;
import com.fzb.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    /**
     * 查询所有用户的内容标签偏好和评论情感分析偏好
     * @return
     */
    @Override
    public UserAlgorithm userAlgorithmData(Integer id) {
        List<AlgorithmChart> categoryList = userBehaviorMapper.categoryList(id);
        List<AlgorithmChart> sentimentList = userBehaviorMapper.sentimentList(id);
        long total = categoryList.size() + sentimentList.size();
        UserAlgorithm userAlgorithm = new UserAlgorithm(total,categoryList,sentimentList);
        return userAlgorithm;
    }

    /**
     * 查询所有用户
     * @return
     */
    @Override
    public PageBean userlist(){
        List<UserIdAndUsername> userList = userMapper.userList();
        long total = userList.size();
        PageBean pageBean = new PageBean(total, userList);
        return pageBean;
    }

    /**
     * 根据关键词查询用户
     * @param username
     * @param keyword
     * @return
     */
    public List<SearchUserResult> searchusers(String username, String keyword){
        List<SearchUserResult> searchuserlist = userMapper.list2(username,keyword);
        return searchuserlist;
    }


    /**
     * 关注触发切换
     * @param authorUsername
     * @param username
     */
    public void checkFollow(String authorUsername, String username){
        if(userMapper.checkFollow(authorUsername,username)){//关注了就取消
            log.info("{}已经关注{}，现取消关注",username,authorUsername);
            userMapper.deletefollow(authorUsername,username);

        }else{//没关注就增加
            log.info("{}未关注{}，先添加关注",username,authorUsername);
            userMapper.insertfollow(authorUsername,username);
        }
    }

    /**
     * 新增用户
     * @param user
     */
    @Override
    public void insertUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    /**
     * 员工登录
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return userMapper.getByUsernameAndPassword(user);
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public User checkUser(String username) {
        return userMapper.getByUsername(username);
    }

    /**
     * 用户信息查询
     * @param page
     * @param pageSize
     * @param name
     * @param gender
     * @param begin
     * @param end
     * @return
     */
    @Override
    public PageBean page(Integer page, Integer pageSize, String name, Short gender, LocalDateTime begin, LocalDateTime end){
        //1.设置分页参数
        PageHelper.startPage(page,pageSize);

        //2.执行查询
        List<User> userList = userMapper.list(name, gender, begin, end);
        Page<User> p = (Page<User>) userList;

        //3.封装pageBean对象
        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public UserInfo getUserInfo(String username) {
        return userMapper.getUserInfo(username);
    }

    @Override
    public UserInfo getUserInfo2(String username, String myusername) {
        return userMapper.getUserInfo2(username, myusername);
    }

    /**
     * 更新用户头像
     * @param username
     * @param image
     */
    @Override
    public void updateImage(String username, String image){
        LocalDateTime updateTime = LocalDateTime.now();
        userMapper.updateImage(username,image,updateTime);
    }

    /**
     * 更新用户性别
     * @param username
     * @param gender
     */
    @Override
    public void updateGender(String username, Integer gender){
        LocalDateTime updateTime = LocalDateTime.now();
        userMapper.updateGender(username,gender,updateTime);
    }

    /**
     * 更新用户签名
     * @param username
     * @param signature
     */
    @Override
    public void updateSignature(String username, String signature){
        LocalDateTime updateTime = LocalDateTime.now();
        userMapper.updateSignature(username,signature,updateTime);
    }


    /**
     * 更新用户信息
     * @param id
     * @param name
     * @param gender
     * @param image
     */
    @Override
    public void updateUser(Integer id, String name, Short gender, String image) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setGender(gender);
        user.setImage(image);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    /**
     * 查询自己的关注
     * @param username
     * @return
     */
    @Override
    public List<followUser> getmyfollows(String username){
        List<followUser> myfollowslist = userMapper.getFollowList(username);
        return myfollowslist;
    }

    /**
     * 查询自己的粉丝
     * @param username
     * @return
     */
    @Override
    public List<followUser> getmyfans(String username){
        List<followUser> myfanslist = userMapper.getFansList(username);
        return myfanslist;
    }
}
