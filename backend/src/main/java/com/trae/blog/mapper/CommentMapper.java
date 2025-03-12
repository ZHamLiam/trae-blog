package com.trae.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trae.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
}