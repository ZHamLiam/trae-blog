package com.trae.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trae.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    
}