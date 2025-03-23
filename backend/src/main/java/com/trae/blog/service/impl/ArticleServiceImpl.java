package com.trae.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.ArticleTag;
import com.trae.blog.entity.Category;
import com.trae.blog.entity.Tag;
import com.trae.blog.entity.User;
import com.trae.blog.mapper.ArticleMapper;
import com.trae.blog.mapper.ArticleTagMapper;
import com.trae.blog.service.ArticleService;
import com.trae.blog.service.CategoryService;
import com.trae.blog.service.TagService;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleTagMapper articleTagMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页获取文章列表
     *
     * @param page       页码
     * @param size       每页大小
     * @param categoryId 分类ID
     * @param tagId      标签ID
     * @param keyword    关键词
     * @return 文章分页列表
     */
    
    @Override
    public IPage<Article> getArticleList(Integer page, Integer size, Long categoryId, Long tagId, String keyword, Integer status, String sortField, String sortOrder) {
        // MyBatis-Plus的Page是从1开始的，与前端传入的page一致，不需要减1
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按分类ID筛选
        if (categoryId != null) {
            queryWrapper.eq(Article::getCategoryId, categoryId);
        }
        
        // 按状态筛选
        if (status != null) {
            queryWrapper.eq(Article::getStatus, status);
        }
        
        // 按标签ID筛选（需要关联查询）
        if (tagId != null) {
            // 先查询包含该标签的文章ID列表
            LambdaQueryWrapper<ArticleTag> tagQueryWrapper = new LambdaQueryWrapper<>();
            tagQueryWrapper.eq(ArticleTag::getTagId, tagId);
            List<ArticleTag> articleTags = articleTagMapper.selectList(tagQueryWrapper);
            
            List<Long> articleIds = new ArrayList<>();
            for (ArticleTag articleTag : articleTags) {
                articleIds.add(articleTag.getArticleId());
            }
            
            if (articleIds.isEmpty()) {
                // 如果没有找到包含该标签的文章，返回空结果
                return pageParam;
            }
            
            queryWrapper.in(Article::getId, articleIds);
        }
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getContent, keyword)
                    .or()
                    .like(Article::getSummary, keyword);
        }
        
        // 处理排序
        // 默认排序：先按置顶排序
        queryWrapper.orderByDesc(Article::getIsTop);
        
        // 如果有指定排序字段和排序方式，则按指定字段排序
        if (StrUtil.isNotBlank(sortField)) {
            // 根据前端传入的排序字段和排序方式进行排序
            switch (sortField) {
                case "viewCount":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(Article::getViewCount);
                    } else {
                        queryWrapper.orderByDesc(Article::getViewCount);
                    }
                    break;
                case "commentCount":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(Article::getCommentCount);
                    } else {
                        queryWrapper.orderByDesc(Article::getCommentCount);
                    }
                    break;
                case "createTime":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(Article::getCreateTime);
                    } else {
                        queryWrapper.orderByDesc(Article::getCreateTime);
                    }
                    break;
                default:
                    // 默认按创建时间降序排序
                    queryWrapper.orderByDesc(Article::getCreateTime);
                    break;
            }
        } else {
            // 没有指定排序字段，默认按创建时间降序排序
            queryWrapper.orderByDesc(Article::getCreateTime);
        }
        
        // 获取分页数据
        IPage<Article> articlePage = page(pageParam, queryWrapper);
        
        // 处理分类名称和标签数据
        List<Article> records = articlePage.getRecords();
        for (Article article : records) {
            // 设置分类名称
            if (article.getCategoryId() != null) {
                Category category = categoryService.getById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            }
            
            // 设置标签列表
            LambdaQueryWrapper<ArticleTag> articleTagQueryWrapper = new LambdaQueryWrapper<>();
            articleTagQueryWrapper.eq(ArticleTag::getArticleId, article.getId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagQueryWrapper);
            
            if (!articleTags.isEmpty()) {
                List<String> tagNames = new ArrayList<>();
                for (ArticleTag articleTag : articleTags) {
                    Tag tag = tagService.getById(articleTag.getTagId());
                    if (tag != null) {
                        tagNames.add(tag.getName());
                    }
                }
                article.setTags(tagNames);
            }
        }
        
        return articlePage;
    }
    
    /**
     * 分页获取文章列表（兼容旧接口）
     */
    @Override
    public IPage<Article> getArticleList(Integer page, Integer size, Long categoryId, Long tagId, String keyword) {
        return getArticleList(page, size, categoryId, tagId, keyword, null, null, null);
    }
    
    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Override
    public Article getArticleById(Long id) {
        // 从缓存获取文章
        Object cachedArticle = redisTemplate.opsForValue().get("article:" + id);
        Article article = null;
        
        // 处理从Redis获取的数据，避免类型转换错误
        if (cachedArticle instanceof Article) {
            article = (Article) cachedArticle;
        } else if (cachedArticle != null) {
            // 如果缓存中的对象不是Article类型（例如是LinkedHashMap），则从数据库重新获取
            article = getById(id);
            if (article != null) {
                // 更新缓存
                redisTemplate.opsForValue().set("article:" + id, article, 1, TimeUnit.HOURS);
            }
        } else {
            // 缓存未命中，从数据库获取
            article = getById(id);
            
            if (article != null) {
                // 存入缓存，设置过期时间为1小时
                redisTemplate.opsForValue().set("article:" + id, article, 1, TimeUnit.HOURS);
            }
        }
        
        // 如果文章存在，获取作者信息
        if (article != null && article.getAuthorId() != null) {
            // 获取作者信息
            User author = userService.getById(article.getAuthorId());
            if (author != null) {
                // 设置作者名称（优先使用昵称，如果没有则使用用户名）
                article.setAuthor(author.getNickname() != null ? author.getNickname() : author.getUsername());
                // 设置作者头像
                article.setAuthorAvatar(author.getAvatar());
                // 可以添加更多作者相关信息
            }
        }
        
        return article;
    }
    
    /**
     * 添加文章
     *
     * @param article 文章信息
     * @param tagIds  标签ID列表
     * @return 添加成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addArticle(Article article, List<Long> tagIds) {
        // 获取当前登录用户
        User user = userService.getUserInfo();
        if (user != null) {
            // 设置作者ID
            article.setAuthorId(user.getId());
        } else {
            // 如果无法获取当前用户，则设置一个默认作者ID
            // 这里设置为1，假设ID为1的用户是系统管理员
            article.setAuthorId(1L);
        }
        
        // 保存文章
        boolean result = save(article);
        
        if (result && tagIds != null && !tagIds.isEmpty()) {
            // 保存文章标签关联
            for (Long tagId : tagIds) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tagId);
                articleTagMapper.insert(articleTag);
            }
        }
        
        return result;
    }
    
    /**
     * 更新文章
     *
     * @param article 文章信息
     * @param tagIds  标签ID列表
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticle(Article article, List<Long> tagIds) {
        // 获取原文章信息
        Article originalArticle = getById(article.getId());
        if (originalArticle != null) {
            // 保留原作者ID
            article.setAuthorId(originalArticle.getAuthorId());
        } else {
            // 如果找不到原文章，则设置当前用户为作者
            User user = userService.getUserInfo();
            if (user != null) {
                article.setAuthorId(user.getId());
            }
        }
        
        // 更新文章
        boolean result = updateById(article);
        
        if (result) {
            // 删除原有的文章标签关联
            LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ArticleTag::getArticleId, article.getId());
            articleTagMapper.delete(queryWrapper);
            
            // 添加新的文章标签关联
            if (tagIds != null && !tagIds.isEmpty()) {
                for (Long tagId : tagIds) {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(article.getId());
                    articleTag.setTagId(tagId);
                    articleTagMapper.insert(articleTag);
                }
            }
            
            // 删除缓存
            redisTemplate.delete("article:" + article.getId());
        }
        
        return result;
    }
    
    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long id) {
        // 删除文章标签关联
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        articleTagMapper.delete(queryWrapper);
        
        // 删除文章
        boolean result = removeById(id);
        
        if (result) {
            // 删除缓存
            redisTemplate.delete("article:" + id);
        }
        
        return result;
    }
    
    /**
     * 获取热门文章
     *
     * @return 热门文章列表
     */
    @Override
    public List<Article> getHotArticles() {
        // 从缓存获取热门文章
        List<Article> hotArticles = (List<Article>) redisTemplate.opsForValue().get("articles:hot");
        
        if (hotArticles == null) {
            // 缓存未命中，从数据库获取
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getStatus, 1) // 只查询已发布的文章
                    .orderByDesc(Article::getViewCount)
                    .last("LIMIT 10"); // 最多10篇热门文章
            
            hotArticles = list(queryWrapper);
            
            // 存入缓存，设置过期时间为1小时
            redisTemplate.opsForValue().set("articles:hot", hotArticles, 1, TimeUnit.HOURS);
        }
        
        return hotArticles;
    }
    
    /**
     * 获取最新文章
     *
     * @return 最新文章列表
     */
    @Override
    public List<Article> getRecentArticles() {
        // 从缓存获取最新文章
        List<Article> recentArticles = (List<Article>) redisTemplate.opsForValue().get("articles:recent");
        
        if (recentArticles == null) {
            // 缓存未命中，从数据库获取
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getStatus, 1) // 只查询已发布的文章
                    .orderByDesc(Article::getCreateTime)
                    .last("LIMIT 10"); // 最近10篇文章
            
            recentArticles = list(queryWrapper);
            
            // 存入缓存，设置过期时间为30分钟
            redisTemplate.opsForValue().set("articles:recent", recentArticles, 30, TimeUnit.MINUTES);
        }
        
        return recentArticles;
    }
    
    /**
     * 增加文章浏览量
     *
     * @param id 文章ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        // 缓存中的浏览量key
        String viewCountKey = "view:article:count:" + id;
        
        // 从缓存中获取浏览量
        Object cachedCount = redisTemplate.opsForValue().get(viewCountKey);
        Integer viewCount = 1;
        
        if (cachedCount != null) {
            // 如果缓存中存在，则增加浏览量
            if (cachedCount instanceof Integer) {
                viewCount = (Integer) cachedCount + 1;
            } else {
                // 尝试转换其他类型
                viewCount = Integer.parseInt(cachedCount.toString()) + 1;
            }
        } else {
            // 缓存未命中，从数据库获取
            Article article = getById(id);
            if (article != null && article.getViewCount() != null) {
                viewCount = article.getViewCount() + 1;
            }
        }
        
        // 更新缓存中的浏览量，设置24小时过期
        redisTemplate.opsForValue().set(viewCountKey, viewCount, 24, TimeUnit.HOURS);
        
        // 获取文章缓存
        Object cachedArticle = redisTemplate.opsForValue().get("article:" + id);
        if (cachedArticle instanceof Article) {
            // 更新缓存中文章的浏览量
            Article article = (Article) cachedArticle;
            article.setViewCount(viewCount);
            redisTemplate.opsForValue().set("article:" + id, article, 1, TimeUnit.HOURS);
        }
    }
    
    /**
     * 更新文章状态
     *
     * @param id 文章ID
     * @param status 文章状态：0-草稿，1-已发布
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticleStatus(Long id, Integer status) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(status);
        
        boolean result = updateById(article);
        
        if (result) {
            // 删除缓存
            redisTemplate.delete("article:" + id);
            
            // 如果状态变更为草稿，也需要从热门文章和最新文章缓存中移除
            if (status == 0) {
                redisTemplate.delete("articles:hot");
                redisTemplate.delete("articles:recent");
            }
        }
        
        return result;
    }
    
    /**
     * 更新文章置顶状态
     *
     * @param id 文章ID
     * @param isTop 是否置顶：0-否，1-是
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArticleTopStatus(Long id, Integer isTop) {
        Article article = new Article();
        article.setId(id);
        article.setIsTop(isTop);
        
        boolean result = updateById(article);
        
        if (result) {
            // 删除缓存
            redisTemplate.delete("article:" + id);
        }
        
        return result;
    }
    
    /**
     * 获取文章总浏览量
     *
     * @return 总浏览量
     */
    @Override
    public long getTotalViewCount() {
        // 从缓存获取总浏览量
        Object cachedCount = redisTemplate.opsForValue().get("stats:viewCount");
        
        if (cachedCount != null) {
            // 修复类型转换错误，确保正确处理Integer到Long的转换
            if (cachedCount instanceof Integer) {
                return ((Integer) cachedCount).longValue();
            } else if (cachedCount instanceof Long) {
                return (Long) cachedCount;
            } else {
                // 如果是其他类型，尝试转换为字符串后解析为Long
                return Long.parseLong(cachedCount.toString());
            }
        }
        
        // 缓存未命中，从数据库计算总浏览量
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Article::getViewCount);
        List<Article> articles = list(queryWrapper);
        
        long totalViewCount = 0;
        for (Article article : articles) {
            totalViewCount += article.getViewCount();
        }
        
        // 存入缓存，设置过期时间为1小时
        redisTemplate.opsForValue().set("stats:viewCount", totalViewCount, 1, TimeUnit.HOURS);
        
        return totalViewCount;
    }
}