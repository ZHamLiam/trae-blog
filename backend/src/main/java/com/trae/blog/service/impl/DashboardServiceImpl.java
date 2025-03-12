package com.trae.blog.service.impl;

import com.trae.blog.dto.CommentDTO;
import com.trae.blog.dto.DashboardDTO;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.Comment;
import com.trae.blog.entity.User;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CommentService;
import com.trae.blog.service.DashboardService;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
}