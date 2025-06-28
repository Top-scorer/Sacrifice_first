package com.fzb.controller;


import com.fzb.pojo.*;
import com.fzb.service.UserService;
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
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 查询所有用户的id和username
     * @return
     */
    @GetMapping("/userlistalgorithm")
    public Result userlistAlgorithm() {
        log.info("admin查询所有用户id和username");
        PageBean pageBean = userService.userlist();
        return Result.success(pageBean);
    }

    /**
     * 根据用户id查询用户整体偏好和对类型偏好
     * @param id
     * @return
     */
    @GetMapping("/userdataalgorithm")
    public Result userdataAlgorithm(Integer id) {
        log.info("admin查询用户{}内容标签整体偏好和评论情感分析偏好",id);
        UserAlgorithm userData = userService.userAlgorithmData(id);
        return Result.success(userData);
    }

    /**
     * 用户查询
     * @param username
     * @param keyword
     * @return
     */
    @GetMapping("/searchUserResults")
    public Result searchUser(@RequestParam String username,@RequestParam String keyword) {
        log.info("用户查询关键词：{}",keyword);
        List<SearchUserResult> searchusers = userService.searchusers(username, keyword);
        return Result.success(searchusers);
    }

    /**
     * 用户关注
     * @param authorUsername
     * @param username
     * @return
     */
    @PostMapping("/follow")
    public Result follow(@RequestParam String authorUsername, @RequestParam String username) {
        userService.checkFollow(authorUsername,username);
        return Result.success();
    }

    @GetMapping("/users")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                   @RequestParam(defaultValue = "10") Integer pageSize,
                   String name, Short gender,
                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime begin,
                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        log.info("分页查询，参数：{},{},{},{},{},{}",page,pageSize,name,gender,begin,end);
        PageBean pageBean = userService.page(page,pageSize,name,gender,begin,end);
        return Result.success(pageBean);
    }

    @GetMapping("/getuser")
    public Result getuser(@RequestParam String username,@RequestParam String myusername) {
        log.info("查询用户信息");
        UserInfo usercard = userService.getUserInfo2(username,myusername);
        return Result.success(usercard);
    }

    @GetMapping("/getmine")
    public Result getmine(@RequestParam String username) {
        log.info("查询自己信息");
        UserInfo userInfo = userService.getUserInfo(username);
        return Result.success(userInfo);
    }

    @PostMapping("/updateimage")
    public Result updateImage(@RequestParam String username,@RequestParam String image) {
        log.info("更新用户头像");
        userService.updateImage(username,image);
        return Result.success();
    }

    @PostMapping("/updategender")
    public Result updateGender(@RequestParam String username,@RequestParam Integer gender) {
        log.info("更新用户性别");
        userService.updateGender(username,gender);
        return Result.success();
    }

    @PostMapping("/updatesignature")
    public Result updateSignature(@RequestParam String username,@RequestParam String signature) {
        log.info("更新用户签名");
        userService.updateSignature(username,signature);
        return Result.success();
    }

    @PostMapping("/users/update")
    public Result updateUser(Integer id, String name, Short gender, String image) {
        log.info("更新用户信息");
        userService.updateUser(id,name,gender,image);
        return Result.success();
    }

    @GetMapping("/getmyfollows")
    public Result getmyfollows(@RequestParam String username) {
        log.info("查询我的关注");
        List<followUser> myfollows = userService.getmyfollows(username);
        return Result.success(myfollows);
    }

    @GetMapping("/getmyfans")
    public Result getmyfans(@RequestParam String username) {
        log.info("查询我的关注");
        List<followUser> myfans = userService.getmyfans(username);
        return Result.success(myfans);
    }
}
