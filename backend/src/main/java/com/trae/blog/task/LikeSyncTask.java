package com.trae.blog.task;

import com.trae.blog.entity.Article;
import com.trae.blog.entity.Comment;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 点赞和浏览量数据同步定时任务
 */
@Slf4j
@Component
@EnableScheduling
public class LikeSyncTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private CommentService commentService;
    
    // 缓存相关的常量
    private static final String ARTICLE_LIKE_COUNT_KEY = "like:article:count:";
    private static final String COMMENT_LIKE_COUNT_KEY = "like:comment:count:";
    private static final String ARTICLE_VIEW_COUNT_KEY = "view:article:count:";
    
    /**
     * 每天凌晨2点执行同步任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncLikeData() {
        log.info("开始同步点赞和浏览量数据到数据库...");
        try {
            // 同步文章点赞数
            syncArticleLikeCount();
            
            // 同步评论点赞数
            syncCommentLikeCount();
            
            // 同步文章浏览量
            syncArticleViewCount();
            
            log.info("点赞和浏览量数据同步完成");
        } catch (Exception e) {
            log.error("点赞和浏览量数据同步失败: {}", e.getMessage());
        }
    }
    
    /**
     * 同步文章点赞数
     */
    private void syncArticleLikeCount() {
        // 获取所有文章点赞数缓存的key
        Set<String> keys = redisTemplate.keys(ARTICLE_LIKE_COUNT_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        
        for (String key : keys) {
            try {
                // 从key中提取文章ID
                String articleIdStr = key.substring(ARTICLE_LIKE_COUNT_KEY.length());
                Long articleId = Long.parseLong(articleIdStr);
                
                // 获取缓存中的点赞数
                Object cachedCount = redisTemplate.opsForValue().get(key);
                if (cachedCount != null) {
                    Integer likeCount = (Integer) cachedCount;
                    
                    // 更新数据库中的点赞数
                    Article article = articleService.getById(articleId);
                    if (article != null) {
                        article.setLikeCount(likeCount);
                        articleService.updateById(article);
                        log.info("同步文章点赞数: articleId={}, likeCount={}", articleId, likeCount);
                    }
                }
            } catch (Exception e) {
                log.error("同步文章点赞数失败: key={}, error={}", key, e.getMessage());
            }
        }
    }
    
    /**
     * 同步评论点赞数
     */
    private void syncCommentLikeCount() {
        // 获取所有评论点赞数缓存的key
        Set<String> keys = redisTemplate.keys(COMMENT_LIKE_COUNT_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        
        for (String key : keys) {
            try {
                // 从key中提取评论ID
                String commentIdStr = key.substring(COMMENT_LIKE_COUNT_KEY.length());
                Long commentId = Long.parseLong(commentIdStr);
                
                // 获取缓存中的点赞数
                Object cachedCount = redisTemplate.opsForValue().get(key);
                if (cachedCount != null) {
                    Integer likeCount = (Integer) cachedCount;
                    
                    // 更新数据库中的点赞数
                    Comment comment = commentService.getById(commentId);
                    if (comment != null) {
                        comment.setLikeCount(likeCount);
                        commentService.updateById(comment);
                        log.info("同步评论点赞数: commentId={}, likeCount={}", commentId, likeCount);
                    }
                }
            } catch (Exception e) {
                log.error("同步评论点赞数失败: key={}, error={}", key, e.getMessage());
            }
        }
    }
    
    /**
     * 同步文章浏览量
     */
    private void syncArticleViewCount() {
        // 获取所有文章浏览量缓存的key
        Set<String> keys = redisTemplate.keys(ARTICLE_VIEW_COUNT_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        
        for (String key : keys) {
            try {
                // 从key中提取文章ID
                String articleIdStr = key.substring(ARTICLE_VIEW_COUNT_KEY.length());
                Long articleId = Long.parseLong(articleIdStr);
                
                // 获取缓存中的浏览量
                Object cachedCount = redisTemplate.opsForValue().get(key);
                if (cachedCount != null) {
                    Integer viewCount;
                    if (cachedCount instanceof Integer) {
                        viewCount = (Integer) cachedCount;
                    } else {
                        // 尝试转换其他类型
                        viewCount = Integer.parseInt(cachedCount.toString());
                    }
                    
                    // 更新数据库中的浏览量
                    Article article = articleService.getById(articleId);
                    if (article != null) {
                        article.setViewCount(viewCount);
                        articleService.updateById(article);
                        log.info("同步文章浏览量: articleId={}, viewCount={}", articleId, viewCount);
                    }
                }
            } catch (Exception e) {
                log.error("同步文章浏览量失败: key={}, error={}", key, e.getMessage());
            }
        }
        
        // 同步完成后，更新总浏览量缓存
        articleService.getTotalViewCount();
    }
}