package com.trae.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.Comment;
import com.trae.blog.entity.Like;
import com.trae.blog.mapper.LikeMapper;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CommentService;
import com.trae.blog.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 点赞服务实现类
 */
@Slf4j
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 缓存相关的常量
    private static final String ARTICLE_LIKE_COUNT_KEY = "like:article:count:";
    private static final String COMMENT_LIKE_COUNT_KEY = "like:comment:count:";
    private static final String USER_ARTICLE_LIKE_KEY = "like:user:article:";
    private static final String USER_COMMENT_LIKE_KEY = "like:user:comment:";
    private static final long CACHE_EXPIRE_TIME = 24; // 缓存过期时间（小时）
    
    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean likeArticle(Long articleId, Long userId) {
        // 检查是否已点赞
        if (hasUserLikedArticle(articleId, userId)) {
            return false;
        }
        
        // 检查文章是否存在
        Article article = articleService.getById(articleId);
        if (article == null) {
            return false;
        }
        
        try {
            // 创建点赞记录
            Like like = new Like();
            like.setType(1); // 1-文章
            like.setTargetId(articleId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            like.setUpdateTime(LocalDateTime.now());
            like.setDeleted(0);
            
            // 保存点赞记录
            boolean saved = save(like);
            
            if (saved) {
                // 更新文章点赞数
                article.setLikeCount(article.getLikeCount() + 1);
                articleService.updateById(article);
                
                // 更新缓存
                String userLikeKey = USER_ARTICLE_LIKE_KEY + userId + ":" + articleId;
                redisTemplate.opsForValue().set(userLikeKey, true, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                
                // 更新文章点赞数缓存
                String articleLikeCountKey = ARTICLE_LIKE_COUNT_KEY + articleId;
                redisTemplate.opsForValue().set(articleLikeCountKey, article.getLikeCount(), CACHE_EXPIRE_TIME, TimeUnit.HOURS);
            }
            
            return saved;
        } catch (Exception e) {
            // 记录异常日志
            log.error("点赞文章失败: articleId={}, userId={}, error={}", articleId, userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 取消文章点赞
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean unlikeArticle(Long articleId, Long userId) {
        try {
            // 查询点赞记录
            LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Like::getType, 1) // 1-文章
                    .eq(Like::getTargetId, articleId)
                    .eq(Like::getUserId, userId);
            
            Like like = getOne(queryWrapper);
            if (like == null) {
                return false;
            }
            
            // 删除点赞记录
            boolean removed = removeById(like.getId());
            
            if (removed) {
                // 更新文章点赞数
                Article article = articleService.getById(articleId);
                if (article != null && article.getLikeCount() > 0) {
                    article.setLikeCount(article.getLikeCount() - 1);
                    articleService.updateById(article);
                    
                    // 更新缓存
                    String userLikeKey = USER_ARTICLE_LIKE_KEY + userId + ":" + articleId;
                    redisTemplate.opsForValue().set(userLikeKey, false, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                    
                    // 更新文章点赞数缓存
                    String articleLikeCountKey = ARTICLE_LIKE_COUNT_KEY + articleId;
                    redisTemplate.opsForValue().set(articleLikeCountKey, article.getLikeCount(), CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                }
            }
            
            return removed;
        } catch (Exception e) {
            // 记录异常日志
            log.error("取消文章点赞失败: articleId={}, userId={}, error={}", articleId, userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 判断用户是否已点赞文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否已点赞
     */
    @Override
    public boolean hasUserLikedArticle(Long articleId, Long userId) {
        // 先从Redis缓存中查询
        String key = USER_ARTICLE_LIKE_KEY + userId + ":" + articleId;
        Object cachedResult = redisTemplate.opsForValue().get(key);
        
        if (cachedResult != null) {
            return (Boolean) cachedResult;
        }
        
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getType, 1) // 1-文章
                .eq(Like::getTargetId, articleId)
                .eq(Like::getUserId, userId);
        
        boolean result = count(queryWrapper) > 0;
        
        // 将结果存入缓存
        redisTemplate.opsForValue().set(key, result, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
        
        return result;
    }
    
    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean likeComment(Long commentId, Long userId) {
        // 检查是否已点赞
        if (hasUserLikedComment(commentId, userId)) {
            return false;
        }
        
        // 检查评论是否存在
        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            return false;
        }
        
        try {
            // 创建点赞记录
            Like like = new Like();
            like.setType(2); // 2-评论
            like.setTargetId(commentId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            like.setUpdateTime(LocalDateTime.now());
            like.setDeleted(0);
            
            // 保存点赞记录
            boolean saved = save(like);
            
            if (saved) {
                // 更新评论点赞数
                comment.setLikeCount(comment.getLikeCount() + 1);
                commentService.updateById(comment);
                
                // 更新缓存
                String userLikeKey = USER_COMMENT_LIKE_KEY + userId + ":" + commentId;
                redisTemplate.opsForValue().set(userLikeKey, true, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                
                // 更新评论点赞数缓存
                String commentLikeCountKey = COMMENT_LIKE_COUNT_KEY + commentId;
                redisTemplate.opsForValue().set(commentLikeCountKey, comment.getLikeCount(), CACHE_EXPIRE_TIME, TimeUnit.HOURS);
            }
            
            return saved;
        } catch (Exception e) {
            // 记录异常日志
            log.error("点赞评论失败: commentId={}, userId={}, error={}", commentId, userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 取消评论点赞
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean unlikeComment(Long commentId, Long userId) {
        try {
            // 查询点赞记录
            LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Like::getType, 2) // 2-评论
                    .eq(Like::getTargetId, commentId)
                    .eq(Like::getUserId, userId);
            
            Like like = getOne(queryWrapper);
            if (like == null) {
                return false;
            }
            
            // 删除点赞记录
            boolean removed = removeById(like.getId());
            
            if (removed) {
                // 更新评论点赞数
                Comment comment = commentService.getById(commentId);
                if (comment != null && comment.getLikeCount() > 0) {
                    comment.setLikeCount(comment.getLikeCount() - 1);
                    commentService.updateById(comment);
                    
                    // 更新缓存
                    String userLikeKey = USER_COMMENT_LIKE_KEY + userId + ":" + commentId;
                    redisTemplate.opsForValue().set(userLikeKey, false, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                    
                    // 更新评论点赞数缓存
                    String commentLikeCountKey = COMMENT_LIKE_COUNT_KEY + commentId;
                    redisTemplate.opsForValue().set(commentLikeCountKey, comment.getLikeCount(), CACHE_EXPIRE_TIME, TimeUnit.HOURS);
                }
            }
            
            return removed;
        } catch (Exception e) {
            // 记录异常日志
            log.error("取消评论点赞失败: commentId={}, userId={}, error={}", commentId, userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 判断用户是否已点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否已点赞
     */
    @Override
    public boolean hasUserLikedComment(Long commentId, Long userId) {
        // 先从Redis缓存中查询
        String key = USER_COMMENT_LIKE_KEY + userId + ":" + commentId;
        Object cachedResult = redisTemplate.opsForValue().get(key);
        
        if (cachedResult != null) {
            return (Boolean) cachedResult;
        }
        
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getType, 2) // 2-评论
                .eq(Like::getTargetId, commentId)
                .eq(Like::getUserId, userId);
        
        boolean result = count(queryWrapper) > 0;
        
        // 将结果存入缓存
        redisTemplate.opsForValue().set(key, result, CACHE_EXPIRE_TIME, TimeUnit.HOURS);
        
        return result;
    }
}