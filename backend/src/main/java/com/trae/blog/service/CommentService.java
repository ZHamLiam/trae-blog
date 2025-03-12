package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 分页获取评论列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param articleId 文章ID
     * @param status 评论状态
     * @return 评论分页列表
     */
    IPage<Comment> getCommentList(Integer page, Integer size, Long articleId, Integer status);
    
    /**
     * 获取评论详情
     *
     * @param id 评论ID
     * @return 评论详情
     */
    Comment getCommentById(Long id);
    
    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @return 添加成功返回true，失败返回false
     */
    boolean addComment(Comment comment);
    
    /**
     * 更新评论状态
     *
     * @param id 评论ID
     * @param status 评论状态：0-待审核，1-已发布，2-已拒绝
     * @return 更新成功返回true，失败返回false
     */
    boolean updateCommentStatus(Long id, Integer status);
    
    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteComment(Long id);
    
    /**
     * 获取最新评论
     *
     * @return 最新评论列表
     */
    List<Comment> getRecentComments();
}