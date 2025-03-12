package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.Article;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {
    
    /**
     * 分页获取文章列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @param keyword 关键词
     * @return 文章分页列表
     */
    IPage<Article> getArticleList(Integer page, Integer size, Long categoryId, Long tagId, String keyword);
    
    /**
     * 分页获取文章列表（带状态筛选）
     *
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @param keyword 关键词
     * @param status 文章状态：0-草稿，1-已发布
     * @return 文章分页列表
     */
    IPage<Article> getArticleList(Integer page, Integer size, Long categoryId, Long tagId, String keyword, Integer status);
    
    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    Article getArticleById(Long id);
    
    /**
     * 添加文章
     *
     * @param article 文章信息
     * @param tagIds 标签ID列表
     * @return 添加成功返回true，失败返回false
     */
    boolean addArticle(Article article, List<Long> tagIds);
    
    /**
     * 更新文章
     *
     * @param article 文章信息
     * @param tagIds 标签ID列表
     * @return 更新成功返回true，失败返回false
     */
    boolean updateArticle(Article article, List<Long> tagIds);
    
    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteArticle(Long id);
    
    /**
     * 获取热门文章
     *
     * @return 热门文章列表
     */
    List<Article> getHotArticles();
    
    /**
     * 获取最新文章
     *
     * @return 最新文章列表
     */
    List<Article> getRecentArticles();
    
    /**
     * 增加文章浏览量
     *
     * @param id 文章ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 获取文章总浏览量
     *
     * @return 总浏览量
     */
    long getTotalViewCount();
    
    /**
     * 更新文章状态
     *
     * @param id 文章ID
     * @param status 文章状态：0-草稿，1-已发布
     * @return 更新成功返回true，失败返回false
     */
    boolean updateArticleStatus(Long id, Integer status);
    
    /**
     * 更新文章置顶状态
     *
     * @param id 文章ID
     * @param isTop 是否置顶：0-否，1-是
     * @return 更新成功返回true，失败返回false
     */
    boolean updateArticleTopStatus(Long id, Integer isTop);
}