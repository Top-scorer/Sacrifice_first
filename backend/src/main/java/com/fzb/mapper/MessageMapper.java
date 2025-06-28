package com.fzb.mapper;

import com.fzb.pojo.UserComment;
import com.fzb.pojo.UserMessage;
import com.fzb.pojo.UserNotice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 根据用户ID获取评论总数
     * @param userId 用户ID
     * @return 评论总数
     */
    @Select("SELECT COUNT(*) FROM comment WHERE user_id = #{userId}")
    public int countCommentsByUserId(Long userId);

    /**
     * 获取所有评论内容
     * @return
     */
    @Select("SELECT c.id, u.username, u.image, c.contents AS content, c.create_time " +
            "FROM comment c " +
            "JOIN user u ON c.user_id = u.id " +
            "WHERE c.video_id = #{videoId} " +
            "ORDER BY c.create_time DESC")
    public List<UserComment> getComments(Integer videoId);


    /**
     * 获取公告板内容5条
     * @return
     */
    @Select("SELECT id, title, contents AS content, create_time AS createTime " +
            "FROM notice " +
            "ORDER BY create_time DESC " +
            "LIMIT 5")
    public List<UserNotice> getNotices();


    /**
     * 获取留言板内容100条
     * @return
     */
    @Select("SELECT m.id, u.username, u.image, m.contents as content, m.create_time as createTime " +
            "FROM message m " +
            "JOIN user u ON m.user_id = u.id " +
            "ORDER BY m.create_time DESC " +
            "LIMIT 100")
    public List<UserMessage> getMessages();

    /**
     * 根据UserMessage插入留言
     * @param userMessage
     */
    @Insert("INSERT INTO message(user_id, contents, create_time) " +
            "VALUES((SELECT id FROM user WHERE username = #{username}), #{content}, #{createTime})")
    public void insertMessage(UserMessage userMessage);

    /**
     * 根据UserComment插入评论
     * @param userComment
     */
    @Insert("INSERT INTO comment(video_id, user_id, contents, translation, sentiment_score, sentiment_prob, create_time) " +
            "VALUES(#{videoId}, " +
            "(SELECT id FROM user WHERE username = #{username}), " +
            "#{content}, #{translation}, #{sentimentScore}, #{sentimentProb}, #{createTime})")
    public void insertComment(UserComment userComment);

    /**
     * 根据UserNotice插入公告
     * @param userNotice
     */
    @Insert("INSERT INTO notice(title, contents, create_time) " +
            "VALUES(#{title}, #{content}, #{createTime})")
    public void insertNotice(UserNotice userNotice);

    /**
     * 根据id删除公告
     * @param id
     */
    @Delete("DELETE FROM notice WHERE id = #{id}")
    public void deleteNotice(Integer id);
}
