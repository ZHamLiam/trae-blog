package com.trae.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.Category;
import com.trae.blog.mapper.ArticleMapper;
import com.trae.blog.mapper.CategoryMapper;
import com.trae.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 分页获取分类列表
     *
     * @param page      页码
     * @param size      每页大小
     * @param keyword   关键词
     * @param sortField 排序字段
     * @param sortOrder 排序方式：ascend-升序，descend-降序
     * @return 分类分页列表
     */
    @Override
    public IPage<Category> getCategoryList(Integer page, Integer size, String keyword, String sortField, String sortOrder) {
        Page<Category> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Category::getName, keyword)
                    .or()
                    .like(Category::getDescription, keyword);
        }
        
        // 处理排序
        if (StrUtil.isNotBlank(sortField)) {
            // 根据前端传入的排序字段和排序方式进行排序
            switch (sortField) {
                case "name":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(Category::getName);
                    } else {
                        queryWrapper.orderByDesc(Category::getName);
                    }
                    break;
                case "sort":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(Category::getSort);
                    } else {
                        queryWrapper.orderByDesc(Category::getSort);
                    }
                    break;
                case "createTime":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(Category::getCreateTime);
                    } else {
                        queryWrapper.orderByDesc(Category::getCreateTime);
                    }
                    break;
                default:
                    // 默认按排序值降序排列
                    queryWrapper.orderByDesc(Category::getSort);
                    break;
            }
        } else {
            // 没有指定排序字段，默认按排序值降序排列
            queryWrapper.orderByDesc(Category::getSort);
        }
        
        IPage<Category> categoryPage = page(pageParam, queryWrapper);
        
        // 获取每个分类的文章数量
        for (Category category : categoryPage.getRecords()) {
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
            articleWrapper.eq(Article::getCategoryId, category.getId());
            long count = articleMapper.selectCount(articleWrapper);
            category.setArticleCount((int) count);
        }
        
        return categoryPage;
    }
    
    /**
     * 分页获取分类列表（兼容旧接口）
     */
    @Override
    public IPage<Category> getCategoryList(Integer page, Integer size, String keyword) {
        return getCategoryList(page, size, keyword, null, null);
    }
    
    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @Override
    public Category getCategoryById(Long id) {
        Category category = getById(id);
        if (category != null) {
            // 获取分类的文章数量
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
            articleWrapper.eq(Article::getCategoryId, id);
            long count = articleMapper.selectCount(articleWrapper);
            category.setArticleCount((int) count);
        }
        return category;
    }
    
    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCategory(Category category) {
        // 检查分类名是否已存在
        long count = count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, category.getName()));
        if (count > 0) {
            return false;
        }
        
        return save(category);
    }
    
    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Category category) {
        // 检查分类名是否已存在（排除自身）
        long count = count(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, category.getName())
                .ne(Category::getId, category.getId()));
        if (count > 0) {
            return false;
        }
        
        return updateById(category);
    }
    
    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        // 检查分类是否被文章引用
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getCategoryId, id);
        long count = articleMapper.selectCount(articleWrapper);
        if (count > 0) {
            return false; // 如果分类被文章引用，则不允许删除
        }
        
        return removeById(id);
    }
    
    /**
     * 获取所有分类（用于下拉选择）
     *
     * @return 所有分类列表
     */
    @Override
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getSort);
        
        List<Category> categories = list(queryWrapper);
        
        // 获取每个分类的文章数量
        for (Category category : categories) {
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
            articleWrapper.eq(Article::getCategoryId, category.getId());
            long count = articleMapper.selectCount(articleWrapper);
            category.setArticleCount((int) count);
        }
        
        return categories;
    }
}