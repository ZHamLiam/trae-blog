package com.trae.blog.dto;

import com.trae.blog.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论数据传输对象
 * 包含评论信息以及关联的用户和文章信息
 */
@Data
public class CommentDTO {
    
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 文章标题
     */
    private String articleTitle;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 父评论ID，如果是一级评论则为0
     */
    private Long parentId;
    
    /**
     * 回复用户ID，如果是一级评论则为0
     */
    private Long replyUserId;
    
    /**
     * 回复用户名
     */
    private String replyUsername;
    
    /**
     * 回复用户昵称
     */
    private String replyNickname;
    
    /**
     * 评论层级：1-一级评论，2-二级评论
     */
    private Integer level;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态：0-待审核，1-已发布，2-已拒绝
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 从Comment实体转换为CommentDTO
     * 
     * @param comment 评论实体
     * @return CommentDTO
     */
    public static CommentDTO fromComment(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setArticleId(comment.getArticleId());
        dto.setUserId(comment.getUserId());
        dto.setParentId(comment.getParentId());
        dto.setReplyUserId(comment.getReplyUserId());
        dto.setLevel(comment.getLevel());
        dto.setLikeCount(comment.getLikeCount());
        dto.setStatus(comment.getStatus());
        dto.setCreateTime(comment.getCreateTime());
        dto.setUpdateTime(comment.getUpdateTime());
        return dto;
    }
}