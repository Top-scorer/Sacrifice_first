package com.fzb.mapper;

import com.fzb.pojo.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {

    @Insert("INSERT INTO video (" +
            "    author_id, " +
            "    video_url, " +
            "    cover_url, " +
            "    video_title, " +
            "    status, " +
            "    like_base, " +
            "    create_time, " +
            "    update_time" +
            ") " +
            "SELECT " +
            "    u.id, " +
            "    #{video.videoUrl}, " +
            "    #{video.coverUrl}, " +
            "    #{video.videoTitle}, " +
            "    #{video.status}, " +
            "    #{video.likeBase}, " +
            "    #{video.createTime}, " +
            "    #{video.updateTime} " +
            "FROM user u " +
            "WHERE u.username = #{username}")
    public void insertByUsername(@Param("video") Video video, @Param("username") String username);

    /**
     * 分页查询
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    public List<Video> list(Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end);

    /**
     * 审核中视频分页查询
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    public List<Video> list2(Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end);

    /**
     * 私密视频分页查询
     * @param authorId
     * @param videoTitle
     * @param begin
     * @param end
     * @return
     */
    public List<Video> list3(Integer authorId, String videoTitle, LocalDateTime begin, LocalDateTime end);

    /**
     * 更新视频状态为2
     * @param id
     */
    @Update("update video set status = 2 where id = #{id}")
    public void updatepublic(Integer id);

    /**
     * 更新视频状态为3
     * @param id
     */
    @Update("update video set status = 3 where id = #{id}")
    public void updateprivate(Integer id);

    /**
     * 根据用户名查询作品
     * @param username
     * @return
     */
    @Select("select * from video where author_id = (select id from user where username = #{username})")
    public List<Video> list4(String username);

    /**
     * 根据用户名查询喜欢
     * @param username
     * @return
     */
    @Select("SELECT \n" +
            "    v.id AS videoId,\n" +
            "    v.video_url AS videoUrl,\n" +
            "    v.cover_url AS coverUrl,\n" +
            "    v.video_title AS videoTitle,\n" +
            "    author.username AS authorUsername,\n" +
            "    true AS liked, -- 因为是喜欢列表，所以默认已点赞\n" +
            "    CASE WHEN c.id IS NOT NULL THEN true ELSE false END AS collected, -- 检查是否收藏\n" +
            "    v.like_base + (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) AS likeCount, -- 基础点赞数+实际点赞数\n" +
            "    (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collectCount -- 收藏总数\n" +
            "FROM \n" +
            "    `user` u\n" +
            "JOIN \n" +
            "    `like` l ON u.id = l.user_id\n" +
            "JOIN \n" +
            "    `video` v ON l.video_id = v.id\n" +
            "JOIN \n" +
            "    `user` author ON v.author_id = author.id\n" +
            "LEFT JOIN \n" +
            "    `collect` c ON c.video_id = v.id AND c.user_id = u.id -- 检查当前用户是否收藏\n" +
            "WHERE \n" +
            "    u.username = #{username}\n" +
            "    AND v.status = 2\n" +
            "ORDER BY \n" +
            "    v.like_base + (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) DESC")
    public List<LikeVideo> list5(String username);

    /**
     * 根据用户名查询收藏
     * @param username
     * @return
     */
    @Select("SELECT \n" +
            "    v.id AS videoId,\n" +
            "    v.video_url AS videoUrl,\n" +
            "    v.cover_url AS coverUrl,\n" +
            "    v.video_title AS videoTitle,\n" +
            "    author.username AS authorUsername,\n" +
            "    CASE WHEN l.id IS NOT NULL THEN true ELSE false END AS liked, -- 检查是否点赞\n" +
            "    true AS collected, -- 因为是收藏列表，所以默认已收藏\n" +
            "    v.like_base + (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) AS likeCount, -- 基础点赞数+实际点赞数\n" +
            "    (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collectCount -- 收藏总数\n" +
            "FROM \n" +
            "    `user` u\n" +
            "JOIN \n" +
            "    `collect` c ON u.id = c.user_id\n" +
            "JOIN \n" +
            "    `video` v ON c.video_id = v.id\n" +
            "JOIN \n" +
            "    `user` author ON v.author_id = author.id\n" +
            "LEFT JOIN \n" +
            "    `like` l ON l.video_id = v.id AND l.user_id = u.id -- 检查当前用户是否点赞\n" +
            "WHERE \n" +
            "    u.username = #{username}\n" +
            "    AND v.status = 2\n" +
            "ORDER BY \n" +
            "    v.like_base + (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) DESC")
    List<LikeVideo> list7(String username);

    /**
     * 查询当前用户未观看的视频（按照点赞量降序排序）
     * @param username 当前用户名，用于排除该用户已观看的视频
     * @return 未观看的视频列表（按点赞量倒序）
     */
    @Select("SELECT \n" +
            "    v.id AS video_id,\n" +
            "    v.video_url,\n" +
            "    v.cover_url,\n" +
            "    v.video_title,\n" +
            "    v.category,\n" +
            "    u.username AS author_username,\n" +
            "    u.image AS author_image,\n" +
            "    (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) + v.like_base AS like_count,\n" +
            "    (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collect_count,\n" +
            "    (SELECT COUNT(*) FROM comment WHERE video_id = v.id) AS comment_count,\n" +
            "    v.update_time\n" +
            "FROM \n" +
            "    video v\n" +
            "JOIN \n" +
            "    user u ON v.author_id = u.id\n" +
            "WHERE \n" +
            "    v.status = 2\n" +
            "    AND NOT EXISTS (\n" +
            "        SELECT 1 FROM view w \n" +
            "        WHERE w.video_id = v.id \n" +
            "        AND w.user_id = (SELECT id FROM user WHERE username = #{username})\n" +
            "    )\n" +
            "ORDER BY \n" +
            "    like_count DESC")
    public List<RandomVideo> list8(String username);


    /**
     * 查询所有未观看视频（按照创建时间进行排序）
     * @param username 用于查询视频是否被该用户关注、喜欢、收藏，并排除已观看视频
     * @return 随机视频列表
     */
    @Select("SELECT \n" +
            "    v.id AS video_id,\n" +
            "    v.video_url,\n" +
            "    v.cover_url,\n" +
            "    v.video_title,\n" +
            "    v.category,\n" +
            "    u.username AS author_username,\n" +
            "    u.image AS author_image,\n" +
            "    (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) + v.like_base AS like_count,\n" +
            "    (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collect_count,\n" +
            "    (SELECT COUNT(*) FROM comment WHERE video_id = v.id) AS comment_count,\n" +
            "    v.update_time\n" +
            "FROM \n" +
            "    video v\n" +
            "JOIN \n" +
            "    user u ON v.author_id = u.id\n" +
            "WHERE \n" +
            "    v.status = 2\n" +
            "    AND NOT EXISTS (\n" +
            "        SELECT 1 FROM view w \n" +
            "        WHERE w.video_id = v.id \n" +
            "        AND w.user_id = (SELECT id FROM user WHERE username = #{username})\n" +
            "    )\n" +
            "ORDER BY \n" +
            "    v.update_time DESC")
    public List<RandomVideo> list6(String username);


    /**
     * 根据用户名检测是否喜欢该视频
     * @param videoId
     * @param username
     * @return
     */
    @Select("SELECT EXISTS(\n" +
            "    SELECT 1 \n" +
            "    FROM `like` l\n" +
            "    JOIN user u ON l.user_id = u.id\n" +
            "    WHERE l.video_id = #{videoId}\n" +
            "    AND u.username = #{username}\n" +
            ")")
    public boolean checkLike(Integer videoId, String username);

    /**
     * 根据用户名检测是否收藏该视频
     * @param videoId
     * @param username
     * @return
     */
    @Select("SELECT EXISTS(\n" +
            "    SELECT 1 \n" +
            "    FROM `collect` c\n" +
            "    JOIN user u ON c.user_id = u.id\n" +
            "    WHERE c.video_id = #{videoId}\n" +
            "    AND u.username = #{username}\n" +
            ")")
    public boolean checkCollect(Integer videoId, String username);

    /**
     * 根据用户名检测是否观看过改视频
     * @param videoId
     * @param username
     * @return
     */
    @Select("SELECT EXISTS(\n" +
            "    SELECT 1 \n" +
            "    FROM `view` v\n" +
            "    JOIN user u ON v.user_id = u.id\n" +
            "    WHERE v.video_id = #{videoId}\n" +
            "    AND u.username = #{username}\n" +
            ")")
    public boolean checkView(Integer videoId, String username);


    /**
     * 根据用户名和视频id删除like记录
     * @param videoId
     * @param username
     */
    @Delete("DELETE FROM `like`\n" +
            "WHERE video_id = #{videoId}\n" +
            "AND user_id = (SELECT id FROM user WHERE username = #{username});")
    public void deletelike(Integer videoId, String username);

    /**
     * 根据用户名和视频id删除collect记录
     * @param videoId
     * @param username
     */
    @Delete("DELETE FROM `collect`\n" +
            "WHERE video_id = #{videoId}\n" +
            "AND user_id = (SELECT id FROM user WHERE username = #{username});")
    public void deletecollect(Integer videoId, String username);

    /**
     * 根据用户名和视频id添加like记录
     * @param videoId
     * @param username
     */
    @Insert("INSERT INTO `like` (video_id, user_id)\n" +
            "SELECT #{videoId}, id \n" +
            "FROM user \n" +
            "WHERE username = #{username};")
    public void insertlike(Integer videoId, String username);

    /**
     * 根据用户名和视频id添加collect记录
     * @param videoId
     * @param username
     */
    @Insert("INSERT INTO `collect` (video_id, user_id)\n" +
            "SELECT #{videoId}, id \n" +
            "FROM user \n" +
            "WHERE username = #{username};")
    public void insertcollect(Integer videoId, String username);

    /**
     * 插入或更新观看记录（如果已存在则增加观看次数）
     * @param videoId 视频ID
     * @param username 用户名
     */
    @Insert("INSERT INTO view(video_id, user_id, count, first_time) " +
            "SELECT #{videoId}, u.id, 1, NOW() FROM user u " +
            "WHERE u.username = #{username} " +
            "ON DUPLICATE KEY UPDATE count = count + 1")
    public void insertView(Integer videoId, String username);


    /**
     * 根据视频id和category更新视频分类，
     * @param id
     * @param category
     */
    @Update("UPDATE video SET category = #{category} WHERE id = #{id}")
    public void updatecategory(Integer id, String category);




    //下面是推荐接口的相关实现


    /**
     * 根据视频Id获取视频（RandomVideo格式）
     * @param userId
     * @param videoIds
     * @return
     */
    @Select({
            "SELECT v.id as videoId, v.video_url as videoUrl, v.video_title as videoTitle, v.category, " +
                    "u.username as authorUsername, " +
                    "EXISTS(SELECT 1 FROM follow f WHERE f.fans_id = #{userId} AND f.follow_id = v.author_id) as followed, " +
                    "EXISTS(SELECT 1 FROM `like` l WHERE l.user_id = #{userId} AND l.video_id = v.id) as liked, " +
                    "EXISTS(SELECT 1 FROM collect c WHERE c.user_id = #{userId} AND c.video_id = v.id) as collected, " +
                    "(v.like_base + (SELECT COUNT(*) FROM `like` WHERE video_id = v.id))as likeCount, " +
                    "(SELECT COUNT(*) FROM collect WHERE video_id = v.id) as collectCount, " +
                    "(SELECT COUNT(*) FROM comment WHERE video_id = v.id) as commentCount, " +
                    "v.update_time as updateTime " +
                    "FROM video v JOIN user u ON v.author_id = u.id " +
                    "WHERE v.id IN (${videoIds}) AND v.status = 2 " +
                    "ORDER BY FIELD(v.id, ${videoIds})"
    })
    public List<RandomVideo> selectRandomVideosByIds(
            @Param("userId") Long userId,
            @Param("videoIds") String videoIds
    );

    /**
     * 获取热度视频的ID
     * @param limit
     * @return
     */
    @Select({
            "SELECT id FROM video " +
                    "WHERE status = 2 " +
                    "ORDER BY (like_base + (SELECT COUNT(*) FROM `like` WHERE video_id = video.id)) DESC, " +
                    "create_time DESC LIMIT #{limit}"
    })
    public List<Long> selectHotVideoIds(@Param("limit") int limit);

    /**
     * 获取单个视频的创建时间和作者粉丝数
     */
    @Select({
            "SELECT v.id AS videoId, v.create_time AS createTime, " +
                    "       (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) + v.like_base AS likeCount, " +  // 加上like_base
                    "       (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collectCount, " +
                    "       (SELECT COUNT(*) FROM comment WHERE video_id = v.id) AS commentCount, " +
                    "       (SELECT COUNT(*) FROM follow WHERE follow_id = v.author_id) AS fansCount " +
                    "FROM video v " +
                    "WHERE v.id = #{videoId}"
    })
    Map<String, Object> selectVideoMetadata(@Param("videoId") Integer videoId);

    /**
     * 根据用户名和关键词搜索视频
     * @param username
     * @param keyword
     * @return
     */
    @Select({
            "SELECT " +
                    "    v.id AS videoId, " +
                    "    v.video_url AS videoUrl, " +
                    "    v.cover_url AS coverUrl, " +
                    "    v.video_title AS videoTitle, " +
                    "    v.category, " +
                    "    u.username AS authorUsername, " +
                    "    u.image, " +
                    "    EXISTS(SELECT 1 FROM follow f WHERE f.follow_id = v.author_id AND f.fans_id = (SELECT id FROM user WHERE username = #{username})) AS followed, " +
                    "    EXISTS(SELECT 1 FROM `like` l WHERE l.video_id = v.id AND l.user_id = (SELECT id FROM user WHERE username = #{username})) AS liked, " +
                    "    EXISTS(SELECT 1 FROM collect c WHERE c.video_id = v.id AND c.user_id = (SELECT id FROM user WHERE username = #{username})) AS collected, " +
                    "    (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) + v.like_base AS likeCount, " +
                    "    (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collectCount " +
                    "FROM " +
                    "    video v " +
                    "JOIN " +
                    "    user u ON v.author_id = u.id " +
                    "WHERE " +
                    "    v.status = 2 " +
                    "    AND (v.video_title LIKE CONCAT('%', #{keyword}, '%') " +
                    "        OR v.category LIKE CONCAT('%', #{keyword}, '%') " +
                    "        OR u.username LIKE CONCAT('%', #{keyword}, '%')) " +  // 新增作者用户名搜索条件
                    "ORDER BY " +
                    "    v.create_time DESC"
    })
    public List<SearchVideoResult> list9(@Param("username") String username, @Param("keyword") String keyword);

    /**
     * 根据视频ID和当前用户获取视频数据封装
     * @param username 当前登录用户名
     * @param videoId 视频ID
     * @return 封装后的视频数据
     */
    @Select({
            "SELECT "+
            "    v.id AS videoId, " +
            "    v.video_url AS videoUrl, " +
            "    v.cover_url AS coverUrl, " +
            "    v.video_title AS videoTitle, " +
            "    v.category, " +
            "    u.username AS authorUsername, " +
            "    u.image, " +
            "    EXISTS(SELECT 1 FROM follow WHERE follow_id = v.author_id AND fans_id = (SELECT id FROM user WHERE username = #{username})) AS followed, " +
            "    EXISTS(SELECT 1 FROM `like` WHERE video_id = v.id AND user_id = (SELECT id FROM user WHERE username = #{username})) AS liked, " +
            "    EXISTS(SELECT 1 FROM collect WHERE video_id = v.id AND user_id = (SELECT id FROM user WHERE username = #{username})) AS collected, " +
            "    (SELECT COUNT(*) FROM `like` WHERE video_id = v.id) + v.like_base AS likeCount, " +
            "    (SELECT COUNT(*) FROM collect WHERE video_id = v.id) AS collectCount, " +
            "    (SELECT COUNT(*) FROM comment WHERE video_id = v.id) AS commentCount " +
            "FROM video v " +
            "JOIN user u ON v.author_id = u.id " +
            "WHERE v.id = #{videoId}"
    })
    public SearchVideoResult getPlayingvideoById(@Param("username") String username, @Param("videoId") Integer videoId);

    /**
     * 查询视频类别
     */
    @Select("SELECT category FROM video WHERE id = #{videoId}")
    public String selectVideoCategory(@Param("videoId") Long videoId);

    /**
     * 获取同标签新视频（排除已交互的）
     * @param category
     * @param userId
     * @param excludedVideoIds
     * @param limit
     * @return
     */
    @Select({
            "SELECT v.id FROM video v " +
                    "WHERE v.category = #{category} " +
                    "AND v.status = 2 " +
                    "AND v.id NOT IN (SELECT video_id FROM `like` WHERE user_id = #{userId}) " +
                    "AND v.id NOT IN (SELECT video_id FROM collect WHERE user_id = #{userId}) " +
                    "AND v.id NOT IN (${excludedVideoIds}) " +  // 保持String类型，需要调用方转换
                    "ORDER BY v.create_time DESC " +
                    "LIMIT #{limit}"
    })
    public List<Long> selectNewVideosByCategoryExcludingInteracted(
            @Param("category") String category,
            @Param("userId") Long userId,
            @Param("excludedVideoIds") String excludedVideoIds,
            @Param("limit") int limit);

    /**
     * 获取每日最热门TopN视频
     * @param limit
     * @return
     */
    @Select({
            "SELECT v.id FROM video v " +
                    "LEFT JOIN `like` l ON v.id = l.video_id " +
                    "WHERE v.status = 2 " +
                    "AND DATE(v.create_time) = CURRENT_DATE() " +
                    "GROUP BY v.id, v.create_time, v.like_base " +  // 添加like_base到GROUP BY
                    "ORDER BY COUNT(l.id) + v.like_base DESC, v.create_time DESC " +  // 加上like_base
                    "LIMIT #{limit}"
    })
    public List<Long> selectTodayHotVideoIds(@Param("limit") int limit);

    /**
     * 查询所有视频
     * @return
     */
    @Select("SELECT id, video_title AS title, category AS type FROM video WHERE status = 2 ORDER BY id")
    public List<VideoIdTitleType> videoList();

    /**
     * 查询视频的各类情感偏向和数量
     * @param id
     * @return
     */
    @Select("SELECT \n" +
            "            CASE \n" +
            "                WHEN sentiment_score = 4 THEN '非常积极'\n" +
            "                WHEN sentiment_score = 3 THEN '积极'\n" +
            "                WHEN sentiment_score = 2 THEN '中性'\n" +
            "                WHEN sentiment_score = 1 THEN '消极'\n" +
            "                ELSE '非常消极'\n" +
            "            END AS name,\n" +
            "            COUNT(*) AS value,\n" +
            "            AVG(sentiment_prob) AS probability\n" +
            "        FROM comment\n" +
            "        WHERE video_id = #{videoId}\n" +
            "        GROUP BY sentiment_score\n" +
            "        ORDER BY sentiment_score DESC")
    public List<AlgorithmChart> videoSentimentList(Integer id);

    /**
     * 查询类型的各类情感偏向和数量
     * @param id
     * @return
     */
    @Select(" SELECT \n" +
            "        CASE \n" +
            "            WHEN sentiment_score = 4 THEN '非常积极'\n" +
            "            WHEN sentiment_score = 3 THEN '积极'\n" +
            "            WHEN sentiment_score = 2 THEN '中性'\n" +
            "            WHEN sentiment_score = 1 THEN '消极'\n" +
            "            ELSE '非常消极'\n" +
            "        END AS name,\n" +
            "        COUNT(*) AS value,\n" +
            "        AVG(sentiment_prob) AS probability\n" +
            "    FROM comment\n" +
            "    WHERE video_id IN (\n" +
            "        SELECT id FROM video \n" +
            "        WHERE category = (SELECT category FROM video WHERE id = #{videoId} LIMIT 1)\n" +
            "    )\n" +
            "    GROUP BY sentiment_score \n" +
            "    ORDER BY sentiment_score DESC")
    public List<AlgorithmChart> typeSentimentList(Integer id);
}