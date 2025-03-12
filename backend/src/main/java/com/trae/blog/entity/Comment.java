package com.trae.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("tb_comment")
public class Comment {
    
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
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
     * 用户ID
     */
    private Long userId;
    
    /**
     * 父评论ID，如果是一级评论则为0
     */
    private Long parentId;
    
    /**
     * 回复用户ID，如果是一级评论则为0
     */
    private Long replyUserId;
    
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
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 文章标题（非数据库字段）
     */
    @TableField(exist = false)
    private String articleTitle;
    
    /**
     * 用户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;
    
    /**
     * 回复用户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String replyUserName;
}