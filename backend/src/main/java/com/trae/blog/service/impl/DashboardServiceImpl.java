package com.trae.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trae.blog.dto.CommentDTO;
import com.trae.blog.dto.DashboardDTO;
import com.trae.blog.dto.StatisticsDTO;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.Category;
import com.trae.blog.entity.Comment;
import com.trae.blog.entity.User;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CategoryService;
import com.trae.blog.service.CommentService;
import com.trae.blog.service.DashboardService;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 * 提供仪表盘所需的统计数据和详细信息
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取仪表盘统计数据
     *
     * @return 仪表盘数据传输对象
     */
    @Override
    public DashboardDTO getDashboardStats() {
        DashboardDTO dashboardDTO = new DashboardDTO();
        
        // 获取文章总数
        long articleCount = articleService.count();
        dashboardDTO.setArticleCount(articleCount);
        
        // 获取总浏览量
        long viewCount = articleService.getTotalViewCount();
        dashboardDTO.setViewCount(viewCount);
        
        // 获取评论总数
        long commentCount = commentService.count();
        dashboardDTO.setCommentCount(commentCount);
        
        // 获取用户总数
        long userCount = userService.count();
        dashboardDTO.setUserCount(userCount);
        
        // 获取最近评论并关联用户和文章信息
        List<Comment> recentComments = commentService.getRecentComments();
        List<CommentDTO> commentDTOs = new ArrayList<>();
        
        if (!recentComments.isEmpty()) {
            // 获取评论中涉及的所有用户ID
            List<Long> userIds = recentComments.stream()
                    .map(Comment::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 获取评论中涉及的所有文章ID
            List<Long> articleIds = recentComments.stream()
                    .map(Comment::getArticleId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 批量查询用户信息
            Map<Long, User> userMap = userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
            
            // 批量查询文章信息
            Map<Long, Article> articleMap = articleService.listByIds(articleIds).stream()
                    .collect(Collectors.toMap(Article::getId, article -> article));
            
            // 组装评论DTO
            for (Comment comment : recentComments) {
                CommentDTO dto = CommentDTO.fromComment(comment);
                
                // 关联用户信息
                User user = userMap.get(comment.getUserId());
                if (user != null) {
                    dto.setUsername(user.getUsername());
                    dto.setNickname(user.getNickname());
                    dto.setAvatar(user.getAvatar());
                }
                
                // 关联文章信息
                Article article = articleMap.get(comment.getArticleId());
                if (article != null) {
                    dto.setArticleTitle(article.getTitle());
                }
                
                // 关联回复用户信息
                if (comment.getReplyUserId() != null && comment.getReplyUserId() > 0) {
                    User replyUser = userMap.get(comment.getReplyUserId());
                    if (replyUser != null) {
                        dto.setReplyUsername(replyUser.getUsername());
                        dto.setReplyNickname(replyUser.getNickname());
                    }
                }
                
                commentDTOs.add(dto);
            }
        }
        
        dashboardDTO.setRecentComments(commentDTOs);
        
        // 获取热门文章
        List<Article> hotArticles = articleService.getHotArticles();
        dashboardDTO.setHotArticles(hotArticles);
        
        // 获取最新用户
        List<User> recentUsers = userService.lambdaQuery()
                .orderByDesc(User::getCreateTime)
                .last("LIMIT 5")
                .list();
        dashboardDTO.setRecentUsers(recentUsers);
        
        return dashboardDTO;
    }
    
    /**
     * 获取统计图表数据
     *
     * @return 统计数据传输对象
     */
    @Override
    public StatisticsDTO getStatisticsData() {
        // 尝试从缓存获取统计数据
        Object cachedStats = redisTemplate.opsForValue().get("stats:charts");
        if (cachedStats instanceof StatisticsDTO) {
            return (StatisticsDTO) cachedStats;
        }
        
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        
        // 获取文章发布趋势数据（按月统计）
        statisticsDTO.setArticleTrend(getArticleTrendData());
        
        // 获取用户注册趋势数据（按月统计）
        statisticsDTO.setUserTrend(getUserTrendData());
        
        // 获取评论趋势数据（按月统计）
        statisticsDTO.setCommentTrend(getCommentTrendData());
        
        // 获取文章分类统计
        statisticsDTO.setCategoryStats(getCategoryStats());
        
        // 获取浏览量趋势数据（按月统计）
        statisticsDTO.setViewTrend(getViewTrendData());
        
        // 将统计数据存入缓存，设置过期时间为1小时
        redisTemplate.opsForValue().set("stats:charts", statisticsDTO, 1, TimeUnit.HOURS);
        
        return statisticsDTO;
    }
    
    /**
     * 获取文章发布趋势数据（按月统计）
     *
     * @return 文章发布趋势数据
     */
    private Map<String, Long> getArticleTrendData() {
        // 获取所有文章
        List<Article> articles = articleService.list();
        
        // 按月份分组统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> trendData = articles.stream()
                .filter(article -> article.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        article -> article.getCreateTime().format(formatter),
                        TreeMap::new,
                        Collectors.counting()
                ));
        
        // 确保最近6个月的数据都有，没有的补0
        return ensureRecentMonths(trendData);
    }
    
    /**
     * 获取用户注册趋势数据（按月统计）
     *
     * @return 用户注册趋势数据
     */
    private Map<String, Long> getUserTrendData() {
        // 获取所有用户
        List<User> users = userService.list();
        
        // 按月份分组统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> trendData = users.stream()
                .filter(user -> user.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getCreateTime().format(formatter),
                        TreeMap::new,
                        Collectors.counting()
                ));
        
        // 确保最近6个月的数据都有，没有的补0
        return ensureRecentMonths(trendData);
    }
    
    /**
     * 获取评论趋势数据（按月统计）
     *
     * @return 评论趋势数据
     */
    private Map<String, Long> getCommentTrendData() {
        // 获取所有评论
        List<Comment> comments = commentService.list();
        
        // 按月份分组统计
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> trendData = comments.stream()
                .filter(comment -> comment.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        comment -> comment.getCreateTime().format(formatter),
                        TreeMap::new,
                        Collectors.counting()
                ));
        
        // 确保最近6个月的数据都有，没有的补0
        return ensureRecentMonths(trendData);
    }
    
    /**
     * 获取文章分类统计
     *
     * @return 文章分类统计数据
     */
    private Map<String, Long> getCategoryStats() {
        // 获取所有分类
        List<Category> categories = categoryService.list();
        Map<Long, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        
        // 获取所有文章
        List<Article> articles = articleService.list();
        
        // 按分类ID分组统计
        Map<Long, Long> categoryCountMap = articles.stream()
                .filter(article -> article.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        Article::getCategoryId,
                        Collectors.counting()
                ));
        
        // 转换为分类名称和文章数量的映射
        Map<String, Long> result = new LinkedHashMap<>();
        categoryCountMap.forEach((categoryId, count) -> {
            String categoryName = categoryMap.getOrDefault(categoryId, "未分类");
            result.put(categoryName, count);
        });
        
        return result;
    }
    
    /**
     * 获取浏览量趋势数据
     * 注意：由于文章只记录总浏览量，无法按月统计，这里使用模拟数据
     *
     * @return 浏览量趋势数据
     */
    private Map<String, Long> getViewTrendData() {
        // 获取所有文章
        List<Article> articles = articleService.list();
        
        // 由于没有按月记录浏览量，这里使用文章创建时间和浏览量来模拟
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Long> trendData = new HashMap<>();
        
        for (Article article : articles) {
            if (article.getCreateTime() != null && article.getViewCount() != null) {
                String month = article.getCreateTime().format(formatter);
                trendData.put(month, trendData.getOrDefault(month, 0L) + article.getViewCount());
            }
        }
        
        // 确保最近6个月的数据都有，没有的补0
        return ensureRecentMonths(trendData);
    }
    
    /**
     * 确保最近6个月的数据都有，没有的补0
     *
     * @param data 原始数据
     * @return 处理后的数据
     */
    private Map<String, Long> ensureRecentMonths(Map<String, Long> data) {
        Map<String, Long> result = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // 生成最近6个月的月份列表
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthDate = now.minusMonths(i);
            String month = monthDate.format(formatter);
            result.put(month, data.getOrDefault(month, 0L));
        }
        
        return result;
    }
}