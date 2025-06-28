package com.fzb.service;

import com.fzb.pojo.UserComment;
import com.fzb.pojo.UserMessage;
import com.fzb.pojo.UserNotice;

import java.util.List;

public interface MessageService {

    /**
     * 获取评论内容
     * @return
     */
    public List<UserComment> getcomment(Integer videoId);

    /**
     * 获取公告板内容
     * @return
     */
    public List<UserNotice> getnotice();

    /**
     * 获取留言板内容
     * @return
     */
    public List<UserMessage> getmessage();


    /**
     * 发布留言
     * @param userMessage
     */
    public void postmessage(UserMessage userMessage);

    /**
     * 发布评论
     * @param userComment
     */
    public void postcomment(UserComment userComment);

    /**
     * 发布公告
     * @param userNotice
     */
    public void postnotice(UserNotice userNotice);

    /**
     * 删除公告
     * @param id
     */
    public void deletenotice(Integer id);
}
