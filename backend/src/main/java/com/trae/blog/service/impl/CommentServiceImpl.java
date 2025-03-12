package com.trae.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.Comment;
import com.trae.blog.entity.User;
import com.trae.blog.mapper.CommentMapper;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CommentService;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserService userService;

    /**
     * 分页获取评论列表
     *
     * @param page      页码
     * @param size      每页大小
     * @param articleId 文章ID
     * @param status    评论状态
     * @return 评论分页列表
     */
    @Override
    public IPage<Comment> getCommentList(Integer page, Integer size, Long articleId, Integer status) {
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按文章ID筛选
        if (articleId != null) {
            queryWrapper.eq(Comment::getArticleId, articleId);
        }
        
        // 按状态筛选
        if (status != null) {
            queryWrapper.eq(Comment::getStatus, status);
        }
        
        // 只查询一级评论
        queryWrapper.eq(Comment::getLevel, 1);
        
        // 排序
        queryWrapper.orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> commentPage = page(pageParam, queryWrapper);
        
        // 设置文章标题和用户信息
        for (Comment comment : commentPage.getRecords()) {
            // 设置文章标题
            if (comment.getArticleId() != null) {
                Article article = articleService.getById(comment.getArticleId());
                if (article != null) {
                    comment.setArticleTitle(article.getTitle());
                }
            }
            
            // 设置用户名称
            if (comment.getUserId() != null) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
            }
            
            // 设置回复用户名称
            if (comment.getReplyUserId() != null && comment.getReplyUserId() > 0) {
                User replyUser = userService.getById(comment.getReplyUserId());
                if (replyUser != null) {
                    comment.setReplyUserName(replyUser.getNickname() != null ? replyUser.getNickname() : replyUser.getUsername());
                }
            }
        }
        
        return commentPage;
    }
    
    /**
     * 获取评论详情
     *
     * @param id 评论ID
     * @return 评论详情
     */
    @Override
    public Comment getCommentById(Long id) {
        Comment comment = getById(id);
        
        if (comment != null) {
            // 设置文章标题
            if (comment.getArticleId() != null) {
                Article article = articleService.getById(comment.getArticleId());
                if (article != null) {
                    comment.setArticleTitle(article.getTitle());
                }
            }
            
            // 设置用户名称
            if (comment.getUserId() != null) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
            }
            
            // 设置回复用户名称
            if (comment.getReplyUserId() != null && comment.getReplyUserId() > 0) {
                User replyUser = userService.getById(comment.getReplyUserId());
                if (replyUser != null) {
                    comment.setReplyUserName(replyUser.getNickname() != null ? replyUser.getNickname() : replyUser.getUsername());
                }
            }
        }
        
        return comment;
    }
    
    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @return 添加成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addComment(Comment comment) {
        // 设置评论状态为待审核
        if (comment.getStatus() == null) {
            comment.setStatus(0);
        }
        
        // 设置评论层级
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            comment.setLevel(2);
        } else {
            comment.setLevel(1);
            comment.setParentId(0L);
            comment.setReplyUserId(0L);
        }
        
        return save(comment);
    }
    
    /**
     * 更新评论状态
     *
     * @param id     评论ID
     * @param status 评论状态：0-待审核，1-已发布，2-已拒绝
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommentStatus(Long id, Integer status) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status);
        
        return updateById(comment);
    }
    
    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long id) {
        // 删除评论及其子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getId, id)
                .or()
                .eq(Comment::getParentId, id);
        
        return remove(queryWrapper);
    }
    
    /**
     * 获取最新评论
     *
     * @return 最新评论列表
     */
    @Override
    public List<Comment> getRecentComments() {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getStatus, 1) // 只查询已发布的评论
                .orderByDesc(Comment::getCreateTime)
                .last("LIMIT 10"); // 最近10条评论
        
        List<Comment> comments = list(queryWrapper);
        
        // 设置文章标题和用户信息
        for (Comment comment : comments) {
            // 设置文章标题
            if (comment.getArticleId() != null) {
                Article article = articleService.getById(comment.getArticleId());
                if (article != null) {
                    comment.setArticleTitle(article.getTitle());
                }
            }
            
            // 设置用户名称
            if (comment.getUserId() != null) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
            }
        }
        
        return comments;
    }
}