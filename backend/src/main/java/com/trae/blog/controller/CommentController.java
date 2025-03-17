package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.Comment;
import com.trae.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页获取评论列表
     *
     * @param page      页码
     * @param size      每页大小
     * @param articleId 文章ID
     * @param status    评论状态
     * @return 评论分页列表
     */
    @GetMapping
    public Result<IPage<Comment>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Integer status) {
        IPage<Comment> commentList = commentService.getCommentList(page, size, articleId, status);
        return Result.success(commentList);
    }

    /**
     * 获取评论详情
     *
     * @param id 评论ID
     * @return 评论详情
     */
    @GetMapping("/{id}")
    public Result<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        return Result.success(comment);
    }

    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Boolean> addComment(@RequestBody Comment comment) {
        boolean result = commentService.addComment(comment);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("添加评论失败");
        }
    }

    /**
     * 更新评论状态
     *
     * @param id     评论ID
     * @param status 评论状态：0-待审核，1-已发布，2-已拒绝
     * @return 更新结果
     */
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('comment:update')")
    public Result<Boolean> updateCommentStatus(
            @PathVariable Long id,
            @PathVariable Integer status) {
        boolean result = commentService.updateCommentStatus(id, status);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新评论状态失败");
        }
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('comment:delete')")
    public Result<Boolean> deleteComment(@PathVariable Long id) {
        boolean result = commentService.deleteComment(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除评论失败");
        }
    }

    /**
     * 获取最新评论
     *
     * @return 最新评论列表
     */
    @GetMapping("/recent")
    public Result<List<Comment>> getRecentComments() {
        List<Comment> recentComments = commentService.getRecentComments();
        return Result.success(recentComments);
    }
    
    /**
     * 批量删除评论
     *
     * @param params 包含评论ID列表的参数
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('comment:delete')")
    public Result<Boolean> batchDeleteComments(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("评论ID列表不能为空");
        }
        
        boolean result = true;
        for (Long id : ids) {
            if (!commentService.deleteComment(id)) {
                result = false;
                break;
            }
        }
        
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("批量删除评论失败");
        }
    }
}