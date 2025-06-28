package com.fzb.service.impl;

import com.fzb.mapper.MessageMapper;
import com.fzb.mapper.UserMapper;
import com.fzb.pojo.SentimentResult;
import com.fzb.pojo.UserComment;
import com.fzb.pojo.UserMessage;
import com.fzb.pojo.UserNotice;
import com.fzb.service.MessageService;
import com.fzb.utils.NlpPipeline;
import com.fzb.utils.YoudaoTranslate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageMapper messageMapper;

    static YoudaoTranslate youdaoTranslate = null;

    // stanford coreNLP英文情感分析
    static NlpPipeline nlpPipeline = null;
    static SentimentResult sentimentResult = null;
    public static void processText(String text) {
        nlpPipeline.estimatingSentiment(text);
    }


    @Override
    public List<UserComment> getcomment(Integer videoId){
        List<UserComment> comments = messageMapper.getComments(videoId);
        return comments;
    }

    @Override
    public List<UserNotice> getnotice(){
        List<UserNotice> notices = messageMapper.getNotices();
        return notices;
    }

    @Override
    public List<UserMessage> getmessage(){
        List<UserMessage> messages = messageMapper.getMessages();
        return messages;
    }

    @Override
    public void postmessage(UserMessage userMessage) {
        userMessage.setCreateTime(LocalDateTime.now());
        messageMapper.insertMessage(userMessage);
    }

    @Override
    public void postcomment(UserComment userComment){
        //1.创建时间
        userComment.setCreateTime(LocalDateTime.now());
        //2.翻译
        String content = userComment.getContent();
        youdaoTranslate = new YoudaoTranslate();
        String translation = youdaoTranslate.GetTranslation(content);
        userComment.setTranslation(translation);
        //3.情感分析
        nlpPipeline  = new NlpPipeline();
        nlpPipeline.init();
        sentimentResult = nlpPipeline.estimatingSentiment(translation);
        userComment.setSentimentScore(sentimentResult.getSentimentScore());
        userComment.setSentimentProb(sentimentResult.getSentimentProb());
        //4.插入评论
        messageMapper.insertComment(userComment);
    }

    @Override
    public void postnotice(UserNotice userNotice){
        userNotice.setCreateTime(LocalDateTime.now());
        messageMapper.insertNotice(userNotice);
    }

    @Override
    public void deletenotice(Integer id){
        messageMapper.deleteNotice(id);
    }


}
