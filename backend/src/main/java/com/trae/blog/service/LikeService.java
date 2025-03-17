package com.trae.blog.service;

/**
 * 点赞服务接口
 */
public interface LikeService {
    
    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean likeArticle(Long articleId, Long userId);
    
    /**
     * 取消文章点赞
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean unlikeArticle(Long articleId, Long userId);
    
    /**
     * 判断用户是否已点赞文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 是否已点赞
     */
    boolean hasUserLikedArticle(Long articleId, Long userId);
    
    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean likeComment(Long commentId, Long userId);
    
    /**
     * 取消评论点赞
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    boolean unlikeComment(Long commentId, Long userId);
    
    /**
     * 判断用户是否已点赞评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否已点赞
     */
    boolean hasUserLikedComment(Long commentId, Long userId);
}