package com.fzb.controller;

import com.fzb.pojo.Result;
import com.fzb.pojo.UserComment;
import com.fzb.pojo.UserMessage;
import com.fzb.pojo.UserNotice;
import com.fzb.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class MessageController {

    @Autowired
    MessageService messageService;

    @GetMapping("/deletenotice")
    public Result deleteNotice(Integer id) {
        log.info("管理员admin删除公告，公告id为：{}",id);
        messageService.deletenotice(id);
        return Result.success();
    }

    @PostMapping("/postnotice")
    public Result postNotice(UserNotice userNotice) {
        log.info("管理员admin发布公告：标题：{}；内容：{}",userNotice.getTitle(),userNotice.getContent());
        messageService.postnotice(userNotice);
        return Result.success();
    }

    @PostMapping("/postmessage")
    public Result postMessage(UserMessage userMessage) {
        log.info("用户{}发布留言：{}",userMessage.getUsername(),userMessage.getContent());
        messageService.postmessage(userMessage);
        return Result.success();
    }

    @PostMapping("/postcomment")
    public Result postComment(UserComment userComment) {
        log.info("用户{}发布评论：{}",userComment.getUsername(),userComment.getContent());
        messageService.postcomment(userComment);
        return Result.success();
    }

    @GetMapping("/getmessage")
    public Result getMessage(@RequestParam String username){
        log.info("用户{}查询留言板",username);
        List<UserMessage> messages = messageService.getmessage();
        return Result.success(messages);
    }

    @GetMapping("/getnotice")
    public Result getNotice(@RequestParam String username){
        log.info("用户{}查询公告板",username);
        List<UserNotice> notices = messageService.getnotice();
        return Result.success(notices);
    }

    @GetMapping("/getcomment")
    public Result getComment(@RequestParam String username, @RequestParam Integer videoId){
        log.info("用户{}查询评论",username);
        List<UserComment> comments = messageService.getcomment(videoId);
        return Result.success(comments);
    }
}
