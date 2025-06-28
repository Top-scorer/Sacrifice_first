package com.fzb.controller;

import com.fzb.pojo.PageBean;
import com.fzb.pojo.Result;
import com.fzb.service.RecommendService;
import com.fzb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
public class RecommendController {
    @Autowired
    RecommendService recommendService;

    @Autowired
    UserService userService;

    //推荐接口
    @GetMapping("/recommend")
    public Result page2(@RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam String username) {
        Long userId = userService.checkUser(username).getId().longValue();

        log.info("获取推荐视频，用户ID: {}, 页码: {}, 每页大小: {}", userId, page, pageSize);
        // 参数校验
        page = Math.max(1, page);
        pageSize = Math.min(Math.max(pageSize, 1), 100);

        PageBean pageBean = recommendService.recommendVideos(userId, page, pageSize);
        return Result.success(pageBean);
    }



}
