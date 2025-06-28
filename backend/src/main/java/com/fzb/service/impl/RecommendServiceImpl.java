package com.fzb.service.impl;


import com.fzb.mapper.FollowMapper;
import com.fzb.mapper.MessageMapper;
import com.fzb.mapper.UserBehaviorMapper;
import com.fzb.mapper.VideoMapper;
import com.fzb.pojo.PageBean;
import com.fzb.pojo.RandomVideo;
import com.fzb.pojo.UserSentimentProfile;
import com.fzb.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    FollowMapper followMapper;

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserBehaviorMapper userBehaviorMapper;

    @Autowired
    VideoMapper videoMapper;

    @Value("${RecommendWeight.basicTotal}")//期望获取推荐视频总数 250
    private int basicTotal;

    @Value("${RecommendWeight.socialRelationship.totalweight}")//社交网络总权重 0.3
    private double socialRelationship_totalweight;

    @Value("${RecommendWeight.contents.totalweight}")//内容总权重 0.5
    private double contents_totalweight;

    @Value("${RecommendWeight.hotGuarantee.totalweight}")//热门总权重 0.2
    private double hotGuarantee_totalweight;


    @Value("${RecommendWeight.socialRelationship.like}")//社交网络喜欢权重 0.3
    private double socialRelationship_like;

    @Value("${RecommendWeight.socialRelationship.collect}")//社交网络收藏权重 0.7
    private double socialRelationship_collect;

    @Value("${RecommendWeight.contents.category.totalweight}")//内容——标签总权重 0.6
    private double contents_category_totalweight;

    @Value("${RecommendWeight.contents.semantics.totalweight}")//内容——语义总权重 0.6
    private double contents_semantics_totalweight;

    @Value("${RecommendWeight.contents.category.like}")//内容——标签——喜欢权重 0.4
    private double contents_category_like;

    @Value("${RecommendWeight.contents.category.collect}")//内容——标签——收藏权重 0.6
    private double contents_category_collect;


    @Override
    public PageBean recommendVideos(Long userId, Integer page, Integer pageSize) {
        // 1. 获取今日最热门3个视频（排除用户已交互的）
        Set<Long> interactedVideoIds = getUserInteractedVideoIds(userId);
        List<Long> todayHotVideoIds = getTodayHotVideoIds(3).stream()
                .filter(videoId -> !interactedVideoIds.contains(videoId))
                .collect(Collectors.toList());

        List<RandomVideo> todayHotVideos = todayHotVideoIds.isEmpty() ?
                Collections.emptyList() :
                videoMapper.selectRandomVideosByIds(userId, StringUtils.join(todayHotVideoIds, ","));

        // 2. 获取常规推荐视频（原有逻辑）
        List<RandomVideo> regularRecommendedVideos = getRegularRecommendedVideos(userId, interactedVideoIds);

        // 3. 合并结果：今日热门在前，常规推荐在后（使用LinkedHashSet去重）
        Set<RandomVideo> finalVideosSet = new LinkedHashSet<>();
        finalVideosSet.addAll(todayHotVideos);  // 先添加今日热门
        finalVideosSet.addAll(regularRecommendedVideos);  // 再添加常规推荐

        List<RandomVideo> finalRecommendedVideos = new ArrayList<>(finalVideosSet);

        // 4. 分页处理
        int total = finalRecommendedVideos.size();
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= total) {
            return new PageBean((long) total, Collections.emptyList());
        }
        int toIndex = Math.min(fromIndex + pageSize, total);

        log.info("推荐系统返还给用户的视频总量：{}",total);
        return new PageBean((long) total, finalRecommendedVideos.subList(fromIndex, toIndex));
    }

    /**
     * 获取常规推荐视频（原有推荐逻辑）
     */
    private List<RandomVideo> getRegularRecommendedVideos(Long userId, Set<Long> excludedVideoIds) {
        // 1. 获取所有推荐视频ID
        List<Long> allVideoIds = getAllRecommendedVideoIds(userId).stream()
                .filter(videoId -> !excludedVideoIds.contains(videoId))
                .collect(Collectors.toList());

        // 2. 查询视频详情
        List<RandomVideo> videos1 = allVideoIds.isEmpty() ?
                Collections.emptyList() :
                videoMapper.selectRandomVideosByIds(userId, StringUtils.join(allVideoIds, ","));

        // 3. 排序
        List<RandomVideo> sortedVideos = videos1.isEmpty() ?
                videos1 :
                sortVideosByScore(videos1);

        // 4. 获取保底推荐
        int hotvideostotal = (int)(basicTotal*hotGuarantee_totalweight);
        List<Long> hotVideoIds = getHotVideoIds(hotvideostotal).stream()
                .filter(videoId -> !excludedVideoIds.contains(videoId))
                .collect(Collectors.toList());

        List<RandomVideo> videos2 = videoMapper.selectRandomVideosByIds(
                userId,
                StringUtils.join(hotVideoIds, ",")
        );

        // 5. 合并结果
        Set<RandomVideo> videoSet = new LinkedHashSet<>(sortedVideos);
        videoSet.addAll(videos2);
        return new ArrayList<>(videoSet);
    }

    /**
     * 获取所有推荐视频ID
     * @param userId
     * @return
     */
    private List<Long> getAllRecommendedVideoIds(Long userId) {
        //信号1： 获取社交网络推荐视频ID（关注用户喜欢收藏的视频）
        int socialvideostotal = (int)(basicTotal*socialRelationship_totalweight);//获取社交关系推荐视频总量
        List<Long> socialVideoIds = getSocialVideoIds(userId, socialvideostotal);

        //信号2： 获取内容推荐视频ID（标签+语义）
        int contentvideostotal = (int)(basicTotal*contents_totalweight);//获取内容推荐视频总量
        List<Long> contentVideoIds = getContentVideoIds(userId, contentvideostotal);

        // 合并并去重（保持顺序）
        Set<Long> allIds = new LinkedHashSet<>(socialVideoIds);
        allIds.addAll(contentVideoIds);

        return new ArrayList<>(allIds);
    }

    /**
     * 获取社交网络 视频ID
     */
    private List<Long> getSocialVideoIds(Long userId, int limit) {
        log.info("启动社交网络推荐");
        //1.获取所有关注的用户
        List<Long> followingIds = followMapper.selectFollowingIds(userId);
        if (followingIds.isEmpty()) {
            log.info("当前用户没有关注行为");
            return Collections.emptyList();
        }

        // 2. 计算喜欢和收藏视频的获取数量（按权重分配）
        int likeLimit = (int)(limit * socialRelationship_like);
        int collectLimit = (int)(limit * socialRelationship_collect);

        // 3. 获取关注用户喜欢的视频
        List<Long> likedVideoIds = userBehaviorMapper.selectLikedVideoIdsByUsers(
                StringUtils.join(followingIds, ","),
                likeLimit
        );
        log.info("社交网络点赞推荐，期望获取视频总数：{}",likeLimit);
        log.info("社交网络点赞推荐获取视频总数：{}",likedVideoIds.size());

        // 4. 获取关注用户收藏的视频
        List<Long> collectedVideoIds = userBehaviorMapper.selectCollectedVideoIdsByUsers(
                StringUtils.join(followingIds, ","),
                collectLimit
        );
        log.info("社交网络收藏推荐，期望获取视频总数：{}",collectLimit);
        log.info("社交网络收藏推荐获取视频总数：{}",collectedVideoIds.size());

        // 5. 合并并去重（保持顺序）
        Set<Long> allVideoIds = new LinkedHashSet<>(likedVideoIds);
        allVideoIds.addAll(collectedVideoIds);
        log.info("社交网络推荐，期望获取视频总数：{}",limit);
        log.info("社交网络获取视频总数：{}",allVideoIds.size());

        // 6. 转换为List并截取到limit数量
        return new ArrayList<>(allVideoIds).subList(0, Math.min(allVideoIds.size(), limit));
    }

    /**
     * 获取内容 视频ID
     */
    private List<Long> getContentVideoIds(Long userId, int limit) {
        log.info("内容推荐启动");
        // 1. 获取用户偏好标签及权重
        List<Map<String, Object>> weightedCategories = userBehaviorMapper
                .selectUserPreferredCategoriesWithWeights(
                        userId,
                        contents_category_like,
                        contents_category_collect
                );

        int commentCount = messageMapper.countCommentsByUserId(userId);
        if (weightedCategories.isEmpty() && commentCount == 0) {
            log.info("用户没有点赞、收藏和评论行为");
            return Collections.emptyList();
        }

        // 2. 计算标签推荐和语义推荐的视频数量（按权重分配）
        int categoryLimit = (int)(limit * contents_category_totalweight);
        int semanticsLimit = (int)(limit * contents_semantics_totalweight);

        // 3. 获取基于标签权重的推荐视频
        List<Long> categoryVideoIds = getCategoryBasedVideoIds(userId, weightedCategories, categoryLimit);
        log.info("内容标签推荐，期望获取视频总数：{}",categoryLimit);
        log.info("内容标签推荐获取视频总数：{}", categoryVideoIds.size());

        // 4. 获取基于评论情感的推荐视频（信号2.2：情感分析）
        List<Long> semanticsVideoIds = getSemanticsBasedVideoIds(userId, semanticsLimit);
        log.info("内容语义推荐，期望获取视频总数：{}",semanticsLimit);
        log.info("内容语义推荐获取视频总数：{}", semanticsVideoIds.size());

        // 5. 合并并去重（保持顺序）
        Set<Long> allVideoIds = new LinkedHashSet<>(categoryVideoIds);
        allVideoIds.addAll(semanticsVideoIds);

        // 6. 转换为List并截取到limit数量
        log.info("内容推荐，期望获取视频总数：{}",limit);
        log.info("内容推荐获取视频总数：{}", allVideoIds.size());
        return new ArrayList<>(allVideoIds).subList(0, Math.min(allVideoIds.size(), limit));
    }

    /**
     * 获取基于标签的推荐视频ID
     */
    private List<Long> getCategoryBasedVideoIds(Long userId, List<Map<String, Object>> weightedCategories, int limit) {
        log.info("基于内容的标签权重推荐启动，用户标签权重：{}", weightedCategories);

        // 1. 如果没有偏好标签，返回空列表
        if (weightedCategories.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取用户已交互的视频ID（用于排除）
        Set<Long> interactedVideoIds = getUserInteractedVideoIds(userId);
        String excludedIdsStr = interactedVideoIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));// 转换Set<Long>为逗号分隔的String

        // 3. 按标签权重分配获取数量
        List<Long> recommendedVideoIds = new ArrayList<>();
        double totalWeight = weightedCategories.stream()
                .mapToDouble(m -> ((Number)m.get("weight")).doubleValue())
                .sum();

        for (Map<String, Object> categoryData : weightedCategories) {
            String category = (String) categoryData.get("category");
            double weight = ((Number) categoryData.get("weight")).doubleValue();
            int categoryLimit = (int) Math.ceil(limit * (weight / totalWeight));

            // 4. 获取同标签新视频（排除已交互的）
            List<Long> categoryVideos = videoMapper.selectNewVideosByCategoryExcludingInteracted(
                    category,
                    userId,
                    excludedIdsStr.isEmpty() ? "0" : excludedIdsStr, // 确保不为空，空时用0代替
                    categoryLimit
            );
            recommendedVideoIds.addAll(categoryVideos);
        }

        // 5. 随机打乱避免单一标签集中
        Collections.shuffle(recommendedVideoIds);

        return recommendedVideoIds.subList(0, Math.min(recommendedVideoIds.size(), limit));
    }

    /**
     * 获取用户已经交互过的视频
     * @param userId
     * @return
     */
    private Set<Long> getUserInteractedVideoIds(Long userId) {
        Set<Long> videoIds = new HashSet<>();
        videoIds.addAll(userBehaviorMapper.selectViewedVideoIdsByUser(userId));
        return videoIds;
    }

    /**
     * 获取基于评论情感分析的推荐视频ID
     * @param userId
     * @param limit
     * @return
     */
    private List<Long> getSemanticsBasedVideoIds(Long userId, int limit) {
        log.info("内容（评论情感分析）推荐启动");
        // 1. 获取用户评论及情感分析结果
        List<Map<String, Object>> userComments = userBehaviorMapper.selectUserCommentsWithSentiment(userId);
        if (userComments.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 分析用户情感特征（考虑情感概率）
        UserSentimentProfile userProfile = analyzeUserSentiment(userComments);
        log.info("当前用户平均情感分数：{}",userProfile.getAverageSentiment());
        log.info("当前用户主导情感类别：{}",userProfile.getDominantEmotion());

        // 3. 获取用户对各类别的情感倾向
        List<Map<String, Object>> categorySentiments = userBehaviorMapper.selectUserCategorySentiment(userId);
        Map<String, Double> categorySentimentMap = categorySentiments.stream()
                .collect(Collectors.toMap(
                        m -> (String)m.get("category"),
                        m -> ((Number)m.get("avg_sentiment")).doubleValue()
                ));

        // 4. 获取候选视频及其情感分析统计
        List<Map<String, Object>> candidateVideos = userBehaviorMapper.selectVideosWithSentimentStats(limit * 5);
        log.info("候选视频总数：{}",candidateVideos.size());

        // 5. 计算视频与用户情感偏好的匹配度
        List<Long> recommendedVideoIds = candidateVideos.stream()
                .map(video -> {
                    long videoId = ((Number)video.get("video_id")).longValue();
                    int commentCount = ((Number)video.get("comment_count")).intValue();
                    double videoSentiment;
                    double sentimentProb;
                    if(commentCount == 0){//视频没有评论，使用默认参数
                        videoSentiment = 2.0;//中性
                        sentimentProb = 1.0;//概率为1，绝对
                    }else {
                        videoSentiment = ((Number) video.get("avg_sentiment")).doubleValue();
                        sentimentProb = ((Number) video.get("avg_sentiment_prob")).doubleValue();
                    }

                    String category = videoMapper.selectVideoCategory(videoId);

                    // 计算情感匹配度（考虑类别偏好，首先获取用户对于类别的情感偏好权重，）
                    double categoryWeight = categorySentimentMap.getOrDefault(category, 0.5);
                    log.info("当前视频的标签为：{}，和用户情感偏好的权重为：{}",category, categoryWeight);
                    double sentimentMatch = calculateSentimentMatch(
                            userProfile,//用户整体偏好
                            videoSentiment,//视频整体偏好
                            sentimentProb,//视频整体偏好情感概率
                            categoryWeight//用户对该类型视频的整体偏好
                    );

                    double popularityWeight;
                    // 根据评论总数计算热度权重（对数变换，平滑转换权重值）
                    if(commentCount == 0){
                        popularityWeight = 0.5; // 或使用固定值如1.0
                    }else {
                        /**
                         * 对数变换将视频的评论数（commentCount）转换为一个平滑的权重值（popularityWeight）
                         * popularityWeight = log₂(1 + commentCount)
                         * 抑制热门内容的绝对优势
                         * 提升中长尾内容的曝光机会
                         * 保持“评论越多权重越高”的基本逻辑
                         */
                        popularityWeight = Math.log1p(commentCount) / Math.log(2);
                    }

                    // 综合得分 = 情感匹配度 * 热度权重
                    double score = sentimentMatch * popularityWeight;
                    log.info("视频{}综合得分：{}", videoId, score);
                    return new AbstractMap.SimpleEntry<>(videoId, score);
                })
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return recommendedVideoIds;
    }


    /**
     * 分析用户情感特征
     * @param comments
     * @return
     */
    private UserSentimentProfile analyzeUserSentiment(List<Map<String, Object>> comments) {
        // 1. 计算加权平均情感分（考虑情感概率）
        double weightedSum = 0;
        double weightSum = 0;

        for (Map<String, Object> comment : comments) {
            int score = ((Number)comment.get("sentiment_score")).intValue();
            double prob = ((Number)comment.get("sentiment_prob")).doubleValue();
            double weight = prob * getScoreWeight(score); // 概率×情感强度权重

            weightedSum += score * weight;
            weightSum += weight;
        }

        double avgScore = weightSum > 0 ? weightedSum / weightSum : 2; // 默认中性(2)

        // 2. 分析主导情感（所有情感类型评论按照Map添加后取最大概率情感极性）
        Map<String, Double> emotionWeights = new HashMap<>();
        for (Map<String, Object> comment : comments) {
            int score = ((Number)comment.get("sentiment_score")).intValue();
            double prob = ((Number)comment.get("sentiment_prob")).doubleValue();
            String emotion = getEmotionCategory(score);

            emotionWeights.merge(emotion, prob, Double::sum);
        }

        String dominantEmotion = emotionWeights.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("neutral");

        return new UserSentimentProfile(avgScore, dominantEmotion);
    }

    /**
     * 计算情感匹配度
     * @param userProfile
     * @param videoSentiment
     * @param sentimentProb
     * @param categoryWeight
     * @return
     */
    private double calculateSentimentMatch(UserSentimentProfile userProfile,
                                           double videoSentiment,
                                           double sentimentProb,
                                           double categoryWeight) {
        //userProfile 用户整体偏好
        //videoSentiment 视频整体偏好
        //sentimentProb 视频整体偏好情感概率
        //categoryWeight 用户对该类型视频的整体偏好

        // 1. 归一化处理（全部映射到0-1范围）

        double userSentimentNormalized = userProfile.getAverageSentiment() / 4.0;
        double videoSentimentNormalized = videoSentiment / 4.0;
        double categoryNormalized = categoryWeight / 4.0;

        /**
         * 2. 用户和视频整体情感匹配程度计算（使用高斯函数平滑差异，量化用户与视频的情感匹配程度，差异越小分数越高)
         *         使用高斯核函数（e^(-k*x²)）将情感差异转换为相似度。
         *         缩放因子 5 控制曲线陡峭度（值越大，对差异越敏感）。
         *         高斯函数缩放因子（5）	增大 → 更严苛的情感差异容忍度	差异小的视频得分更高，差异大的得分更低。
         */
        double sentimentDiff = Math.abs(userSentimentNormalized - videoSentimentNormalized);
        double sentimentSimilarity = Math.exp(-5 * sentimentDiff * sentimentDiff); // 缩放因子5

        /**
         * 3. 用户对该类别整体偏好匹配度（使用Sigmoid函数确保合理范围，将分类权重转换为非线性匹配分数，突出重要分类）
         *         Sigmoid函数（1/(1+e^(-k(x-b)))）将输入压缩到(0,1)。
         *         参数说明：
         *         10：控制斜率（值越大，过渡越陡峭）。
         *         0.5：中心点（分类权重=0.5时输出0.5）。
         *         Sigmoid斜率（10）	减小 → 分类权重过渡更平缓	弱分类的得分提升，强分类的优势减弱。
         */
        double categoryMatch = 1.0 / (1 + Math.exp(-10 * (categoryNormalized - 0.5))); // 缩放因子10

        /**
         * 4. 情感概率权重（保持原有逻辑）调整情感分析置信度（sentimentProb）的影响范围。
         *         原始置信度范围：[0, 1] → 映射到 [0.5, 1.0],转换为高置换度。
         *         避免低置信度（如0.1）对结果产生过大负面影响。
         *         示例：
         *         sentimentProb=0.8 → 0.5 + 0.8/2 = 0.9
         *         sentimentProb=0.3 → 0.5 + 0.3/2 = 0.65
         *         概率权重基线（0.5）	降低 → 低置信度影响更大	模型对不确定结果的惩罚更强。
         */
        double probWeight = 0.5 + sentimentProb / 2.0;

        /**
         * 5. 综合计算整合三个维度的分数，生成最终匹配度。
         *         乘积策略：任一维度接近0会显著拉低总分（强过滤作用）。
         *         权重优先级：实际业务中可通过调整各部分的系数控制影响程度。
         */
        double score = sentimentSimilarity * probWeight * categoryMatch;
        return score;
    }

    /**
     * 获取情感类别
     * @param score
     * @return
     */
    private String getEmotionCategory(int score) {
        switch (score) {
            case 0: return "very_negative";
            case 1: return "negative";
            case 2: return "neutral";
            case 3: return "positive";
            case 4: return "very_positive";
            default: return "neutral";
        }
    }

    /**
     * 获取情感分数权重（非常正面/负面给予更高权重）
     * @param score
     * @return
     */
    private double getScoreWeight(int score) {
        switch (score) {
            case 0: return 1.5; // 非常负面
            case 1: return 1.0; // 负面
            case 2: return 0.8; // 中性
            case 3: return 1.0; // 正面
            case 4: return 1.5; // 非常正面
            default: return 1.0;
        }
    }

    /**
     * 获取热度视频ID
     * @param limit
     * @return
     */
    private List<Long> getHotVideoIds(int limit) {
        return videoMapper.selectHotVideoIds(limit);
    }

    /**
     * 获取今日最热门视频ID（按当天点赞数排序）
     * @param limit
     * @return
     */
    private List<Long> getTodayHotVideoIds(int limit) {
        return videoMapper.selectTodayHotVideoIds(limit);
    }

    // 视频排序方法
    private List<RandomVideo> sortVideosByScore(List<RandomVideo> videos) {
        // 计算每个视频的得分并存入Map
        Map<RandomVideo, Double> videoScores = new HashMap<>();
        for (RandomVideo video : videos) {
            videoScores.put(video, calculateVideoScore(video));
        }

        // 按得分降序排序，使用Double.compare保证精度
        return videos.stream()
                .sorted((v1, v2) -> Double.compare(videoScores.get(v2), videoScores.get(v1)))
                .collect(Collectors.toList());
    }

    // 计算视频热度得分
    private double calculateVideoScore(RandomVideo video) {
        // 1. 获取视频元数据
        Map<String, Object> metadata = videoMapper.selectVideoMetadata(video.getVideoId());
        if (metadata == null || metadata.isEmpty()) {
            return 0;
        }

        // 2. 计算基础热度
        double basePopularity = calculateBasePopularity(metadata);

        // 3. 计算时间衰减因子
        LocalDateTime createTime = (LocalDateTime) metadata.get("createTime");
        long hoursSinceUpload = ChronoUnit.HOURS.between(createTime, LocalDateTime.now());
        double timeWeight = calculateTimeWeight(hoursSinceUpload);

        // 4. 最终得分计算
        double finalScore = basePopularity * timeWeight;

        log.info("当前视频{}得分：基础热度={}，时间权重={}，最终得分={}",video.getVideoId(),
                basePopularity, timeWeight, finalScore);

        return finalScore;
    }

    private double calculateBasePopularity(Map<String, Object> metadata) {
        // 1. 获取各项指标（使用对数处理避免极端值影响）
        double likes = Math.log1p(((Number)metadata.getOrDefault("likeCount", 0)).doubleValue());
        double collects = Math.log1p(((Number)metadata.getOrDefault("collectCount", 0)).doubleValue());
        double comments = Math.log1p(((Number)metadata.getOrDefault("commentCount", 0)).doubleValue());
        double fansCount = Math.log1p(((Number)metadata.getOrDefault("fansCount", 0)).doubleValue());

        // 2. 计算各项权重得分
        double likeScore = likes * 0.4;       // 点赞权重40%
        double collectScore = collects * 0.3; // 收藏权重30%
        double commentScore = comments * 0.2; // 评论权重20%
        double authorScore = fansCount * 0.1; // 作者影响力10%

        // 3. 综合基础热度
        return likeScore + collectScore + commentScore + authorScore;
    }

    private double calculateTimeWeight(long hoursSinceUpload) {
        // 非线性时间衰减函数
        if (hoursSinceUpload <= 24) { // 1天内
            return 1.0;
        } else if (hoursSinceUpload <= 72) { // 3天内
            return 0.8 - (hoursSinceUpload - 24) * 0.005; // 每小时下降0.5%
        } else { // 超过3天
            return 0.6 * Math.pow(0.95, (hoursSinceUpload - 72)); // 每小时下降5%
        }
    }
}
