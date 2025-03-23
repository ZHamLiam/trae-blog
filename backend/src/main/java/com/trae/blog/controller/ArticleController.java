package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.Article;
import com.trae.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页获取文章列表
     *
     * @param page       页码
     * @param size       每页大小
     * @param categoryId 分类ID
     * @param tagId      标签ID
     * @param keyword    关键词
     * @param status     文章状态：0-草稿，1-已发布
     * @return 文章分页列表
     */
    @GetMapping
    public Result<IPage<Article>> getArticleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        IPage<Article> articleList = articleService.getArticleList(page, size, categoryId, tagId, keyword, status, sortField, sortOrder);
        return Result.success(articleList);
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id) {
        // 获取文章详情
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        
        // 检查文章状态，如果是草稿且不是管理接口访问，则拒绝访问
        // 通过Referer请求头判断是否为后台管理页面访问
        
        
        // 增加浏览量
        articleService.incrementViewCount(id);
        
        return Result.success(article);
    }

    /**
     * 添加文章
     *
     * @param article 文章信息
     * @return 添加结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('article:create')")
    public Result<Boolean> addArticle(@RequestBody Article article) {
        // 从article对象中获取标签ID列表
        List<Long> tagIds = article.getTagIds();
        boolean result = articleService.addArticle(article, tagIds);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("添加文章失败");
        }
    }

    /**
     * 更新文章
     *
     * @param id      文章ID
     * @param article 文章信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('article:update')")
    public Result<Boolean> updateArticle(
            @PathVariable Long id,
            @RequestBody Article article) {
        article.setId(id);
        // 从article对象中获取标签ID列表
        List<Long> tagIds = article.getTagIds();
        boolean result = articleService.updateArticle(article, tagIds);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新文章失败");
        }
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('article:delete')")
    public Result<Boolean> deleteArticle(@PathVariable Long id) {
        boolean result = articleService.deleteArticle(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除文章失败");
        }
    }

    /**
     * 更新文章状态
     *
     * @param id     文章ID
     * @param status 文章状态：0-草稿，1-已发布
     * @return 更新结果
     */
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('article:update')")
    public Result<Boolean> updateArticleStatus(
            @PathVariable Long id,
            @PathVariable Integer status) {
        boolean result = articleService.updateArticleStatus(id, status);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新文章状态失败");
        }
    }

    /**
     * 更新文章置顶状态
     *
     * @param id    文章ID
     * @param isTop 是否置顶：0-否，1-是
     * @return 更新结果
     */
    @PutMapping("/{id}/top/{isTop}")
    @PreAuthorize("hasAuthority('article:update')")
    public Result<Boolean> updateArticleTopStatus(
            @PathVariable Long id,
            @PathVariable Integer isTop) {
        boolean result = articleService.updateArticleTopStatus(id, isTop);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新文章置顶状态失败");
        }
    }

    /**
     * 获取热门文章
     *
     * @return 热门文章列表
     */
    @GetMapping("/hot")
    public Result<List<Article>> getHotArticles() {
        List<Article> hotArticles = articleService.getHotArticles();
        return Result.success(hotArticles);
    }

    /**
     * 获取最新文章
     *
     * @return 最新文章列表
     */
    @GetMapping("/recent")
    public Result<List<Article>> getRecentArticles() {
        List<Article> recentArticles = articleService.getRecentArticles();
        return Result.success(recentArticles);
    }
    
    /**
     * 批量删除文章
     *
     * @param params 包含文章ID列表的参数
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('article:delete')")
    public Result<Boolean> batchDeleteArticles(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("文章ID列表不能为空");
        }
        
        boolean result = articleService.removeBatchByIds(ids);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("批量删除文章失败");
        }
    }
}