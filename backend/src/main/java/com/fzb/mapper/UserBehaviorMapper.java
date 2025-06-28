package com.fzb.mapper;

import com.fzb.pojo.AlgorithmChart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {

    /**
     * 社交网络用于补充的视频
     * @param userIds
     * @param limit
     * @return
     */
    @Select({
            "SELECT DISTINCT video_id, create_time FROM (" +
                    "   SELECT l.video_id, (SELECT v.create_time FROM video v WHERE v.id = l.video_id) AS create_time " +
                    "   FROM `like` l WHERE l.user_id IN (${userIds})" +
                    "   UNION ALL" +
                    "   SELECT c.video_id, (SELECT v.create_time FROM video v WHERE v.id = c.video_id) AS create_time " +
                    "   FROM collect c WHERE c.user_id IN (${userIds})" +
                    ") t ORDER BY create_time DESC LIMIT #{limit}"
    })
    public List<Long> selectLikedOrCollectedVideoIdsByUsers(@Param("userIds") String userIds, @Param("limit") int limit);

    /**
     * 社交网络用户收藏视频
     * @param userIds
     * @param limit
     * @return
     */
    @Select({
            "SELECT DISTINCT c.video_id " +
                    "FROM collect c " +
                    "WHERE c.user_id IN (${userIds}) " +
                    "ORDER BY (SELECT v.create_time FROM video v WHERE v.id = c.video_id) DESC " +
                    "LIMIT #{limit}"
    })
    public List<Long> selectCollectedVideoIdsByUsers(@Param("userIds") String userIds, @Param("limit") int limit);

    /**
     * 社交网络用户点赞视频
     * @param userIds
     * @param limit
     * @return
     */
    @Select({
            "SELECT DISTINCT l.video_id " +
                    "FROM `like` l " +
                    "WHERE l.user_id IN (${userIds}) " +
                    "ORDER BY (SELECT v.create_time FROM video v WHERE v.id = l.video_id) DESC " +
                    "LIMIT #{limit}"
    })
    public List<Long> selectLikedVideoIdsByUsers(@Param("userIds") String userIds, @Param("limit") int limit);

    /**
     * 获取用户偏好标签和权重（只要该标签用户点赞或收藏就加上对应权重，最终按照每个标签的权重排序）
     * @param userId
     * @param likeWeight
     * @param collectWeight
     * @return
     */
    @Select({
            "SELECT v.category, " +
                    "       SUM(CASE WHEN l.user_id IS NOT NULL THEN #{likeWeight} ELSE 0 END) + " +
                    "       SUM(CASE WHEN c.user_id IS NOT NULL THEN #{collectWeight} ELSE 0 END) AS weight " +
                    "FROM video v " +
                    "LEFT JOIN `like` l ON v.id = l.video_id AND l.user_id = #{userId} " +
                    "LEFT JOIN collect c ON v.id = c.video_id AND c.user_id = #{userId} " +
                    "WHERE v.category IS NOT NULL " +
                    "GROUP BY v.category " +
                    "HAVING weight > 0 " +
                    "ORDER BY weight DESC " +
                    "LIMIT 5"
    })
    public List<Map<String, Object>> selectUserPreferredCategoriesWithWeights(
            @Param("userId") Long userId,
            @Param("likeWeight") double likeWeight,
            @Param("collectWeight") double collectWeight);

    // 获取用户点赞的视频ID列表
    @Select({
            "SELECT video_id FROM `like` " +
                    "WHERE user_id = #{userId} " +
                    "ORDER BY id DESC"
    })
    public List<Long> selectLikedVideoIdsByUser(@Param("userId") Long userId);

    // 获取用户收藏的视频ID列表
    @Select({
            "SELECT video_id FROM collect " +
                    "WHERE user_id = #{userId} " +
                    "ORDER BY id DESC"
    })
    public List<Long> selectCollectedVideoIdsByUser(@Param("userId") Long userId);

    /**
     * 获取用户看过的所有视频ID列表
     * @param userId
     * @return
     */
    @Select({
            "SELECT video_id FROM view " +
                    "WHERE user_id = #{userId} " +
                    "ORDER BY id DESC"
    })
    public List<Long> selectViewedVideoIdsByUser(@Param("userId") Long userId);

    /**
     * 获取用户评论内容及情感分析结果
     * @param userId
     * @return
     */
    @Select({
            "SELECT c.contents, c.sentiment_score, c.sentiment_prob " +
                    "FROM comment c " +
                    "WHERE c.user_id = #{userId} " +
                    "ORDER BY c.create_time DESC " +
                    "LIMIT 50"
    })
    public List<Map<String, Object>> selectUserCommentsWithSentiment(@Param("userId") Long userId);

    /**
     * 获取视频评论情感分析统计
     * @param limit
     * @return
     */
    @Select({
            "SELECT v.id AS video_id, " +
                    "       AVG(c.sentiment_score) AS avg_sentiment, " +
                    "       AVG(c.sentiment_prob) AS avg_sentiment_prob, " +
                    "       COUNT(c.id) AS comment_count, " +
                    "       v.create_time " +
                    "FROM video v " +
                    "LEFT JOIN comment c ON v.id = c.video_id " +
                    "WHERE v.status = 2 " +
                    "GROUP BY v.id, v.create_time " +  // 将create_time加入GROUP BY
                    "ORDER BY v.create_time DESC " +
                    "LIMIT #{limit}"
    })
    public List<Map<String, Object>> selectVideosWithSentimentStats(@Param("limit") int limit);

    /**
     * 获取用户对各类别视频的情感倾向
     * @param userId
     * @return
     */
    @Select({
            "SELECT v.category, " +
                    "       AVG(c.sentiment_score) AS avg_sentiment, " +
                    "       COUNT(c.id) AS comment_count " +
                    "FROM comment c " +
                    "JOIN video v ON c.video_id = v.id " +
                    "WHERE c.user_id = #{userId} " +
                    "GROUP BY v.category " +
                    "HAVING comment_count > 0 " +
                    "ORDER BY comment_count DESC"
    })
    public List<Map<String, Object>> selectUserCategorySentiment(@Param("userId") Long userId);

    /**
     * 根据id查询用户点赞收藏的所有视频类型和该类型数量（去重）
     * @return
     */
    @Select({
            "SELECT v.category AS name, COUNT(DISTINCT v.id) AS value " +
                    "FROM video v " +
                    "LEFT JOIN `like` l ON v.id = l.video_id AND l.user_id = #{id} " +
                    "LEFT JOIN collect c ON v.id = c.video_id AND c.user_id = #{id} " +
                    "WHERE (l.id IS NOT NULL OR c.id IS NOT NULL) " +
                    "AND v.category IS NOT NULL " +
                    "GROUP BY v.category " +
                    "ORDER BY value DESC"
    })
    public List<AlgorithmChart> categoryList(Integer id);

    /**
     * 根据id查询用户对所有视频类型的偏好分数
     * @param id
     * @return
     */
    @Select({
            "SELECT v.category AS name, " +  // 列别名必须为 name
                    "       ROUND(AVG(c.sentiment_score)) AS value " +  // 列别名必须为 value
                    "FROM comment c " +
                    "JOIN video v ON c.video_id = v.id " +
                    "WHERE c.user_id = #{id} " +
                    "AND v.category IS NOT NULL " +
                    "GROUP BY v.category " +
                    "ORDER BY value DESC"
    })
    List<AlgorithmChart> sentimentList(@Param("id") Integer id);
}