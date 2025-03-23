package com.trae.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 */
@Data
@TableName("tb_article")
public class Article {
    
    /**
     * 文章ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章内容
     */
    private String content;
    
    /**
     * 文章摘要
     */
    private String summary;
    
    /**
     * 文章封面图URL
     */
    private String coverImage;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 点赞量
     */
    private Integer likeCount;
    
    /**
     * 评论量
     */
    private Integer commentCount;
    
    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;
    
    /**
     * 状态：0-草稿，1-已发布
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
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
    
    /**
     * 标签列表（非数据库字段）- 用于显示标签名称
     */
    @TableField(exist = false)
    private List<String> tags;
    
    /**
     * 标签ID列表（非数据库字段）- 用于接收前端传递的标签ID
     */
    @TableField(exist = false)
    private List<Long> tagIds;
    
    /**
     * 作者名称（非数据库字段）
     */
    @TableField(exist = false)
    private String author;
    
    /**
     * 作者头像（非数据库字段）
     */
    @TableField(exist = false)
    private String authorAvatar;
    
    /**
     * 作者简介（非数据库字段）
     */
    @TableField(exist = false)
    private String authorBio;
}