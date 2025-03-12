package com.trae.blog.dto;

import com.trae.blog.entity.Article;
import com.trae.blog.entity.Comment;
import com.trae.blog.entity.User;
import lombok.Data;

import java.util.List;

/**
 * 仪表盘数据传输对象
 * 包含仪表盘所需的统计数据和详细信息
 */
@Data
public class DashboardDTO {
    
    /**
     * 文章总数
     */
    private Long articleCount;
    
    /**
     * 总浏览量
     */
    private Long viewCount;
    
    /**
     * 评论总数
     */
    private Long commentCount;
    
    /**
     * 用户总数
     */
    private Long userCount;
    
    /**
     * 最近评论列表
     */
    private List<CommentDTO> recentComments;
    
    /**
     * 热门文章列表
     */
    private List<Article> hotArticles;
    
    /**
     * 最新用户列表
     */
    private List<User> recentUsers;
}