package com.trae.blog.controller;

import com.trae.blog.common.Result;
import com.trae.blog.entity.User;
import com.trae.blog.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    @Autowired
    private com.trae.blog.service.UserService userService;
    
    private User getCurrentUser() {
        return userService.getUserInfo();
    }

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @PostMapping("/article/{articleId}")
    public Result<?> likeArticle(@PathVariable Long articleId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        boolean success = likeService.likeArticle(articleId, currentUser.getId());
        return success ? Result.success() : Result.error("点赞失败");
    }

    /**
     * 取消文章点赞
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @DeleteMapping("/article/{articleId}")
    public Result<?> unlikeArticle(@PathVariable Long articleId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        boolean success = likeService.unlikeArticle(articleId, currentUser.getId());
        return success ? Result.success() : Result.error("取消点赞失败");
    }

    /**
     * 获取用户对文章的点赞状态
     *
     * @param articleId 文章ID
     * @return 点赞状态
     */
    @GetMapping("/article/{articleId}/status")
    public Result<Boolean> getArticleLikeStatus(@PathVariable Long articleId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.success(false);
        }
        boolean liked = likeService.hasUserLikedArticle(articleId, currentUser.getId());
        return Result.success(liked);
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return 结果
     */
    @PostMapping("/comment/{commentId}")
    public Result<?> likeComment(@PathVariable Long commentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        boolean success = likeService.likeComment(commentId, currentUser.getId());
        return success ? Result.success() : Result.error("点赞失败");
    }

    /**
     * 取消评论点赞
     *
     * @param commentId 评论ID
     * @return 结果
     */
    @DeleteMapping("/comment/{commentId}")
    public Result<?> unlikeComment(@PathVariable Long commentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        boolean success = likeService.unlikeComment(commentId, currentUser.getId());
        return success ? Result.success() : Result.error("取消点赞失败");
    }

    /**
     * 获取用户对评论的点赞状态
     *
     * @param commentId 评论ID
     * @return 点赞状态
     */
    @GetMapping("/comment/{commentId}/status")
    public Result<Boolean> getCommentLikeStatus(@PathVariable Long commentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.success(false);
        }
        boolean liked = likeService.hasUserLikedComment(commentId, currentUser.getId());
        return Result.success(liked);
    }
}