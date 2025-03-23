package com.trae.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.Article;
import com.trae.blog.entity.ArticleTag;
import com.trae.blog.entity.Tag;
import com.trae.blog.mapper.ArticleMapper;
import com.trae.blog.mapper.ArticleTagMapper;
import com.trae.blog.mapper.TagMapper;
import com.trae.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;
    
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 分页获取标签列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 标签分页列表
     */
    @Override
    public IPage<Tag> getTagList(Integer page, Integer size, String keyword) {
        Page<Tag> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(Tag::getName, keyword)
                    .or()
                    .like(Tag::getDescription, keyword);
        }
        
        // 排序
        queryWrapper.orderByDesc(Tag::getCreateTime);
        
        IPage<Tag> tagPage = page(pageParam, queryWrapper);
        
        // 获取每个标签的文章数量
        for (Tag tag : tagPage.getRecords()) {
            LambdaQueryWrapper<ArticleTag> articleTagWrapper = new LambdaQueryWrapper<>();
            articleTagWrapper.eq(ArticleTag::getTagId, tag.getId());
            long count = articleTagMapper.selectCount(articleTagWrapper);
            tag.setArticleCount((int)count);
        }
        
        return tagPage;
    }
    
    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    @Override
    public Tag getTagById(Long id) {
        Tag tag = getById(id);
        if (tag != null) {
            // 获取标签的文章数量
            LambdaQueryWrapper<ArticleTag> articleTagWrapper = new LambdaQueryWrapper<>();
            articleTagWrapper.eq(ArticleTag::getTagId, id);
            long count = articleTagMapper.selectCount(articleTagWrapper);
            tag.setArticleCount((int)count);
        }
        return tag;
    }
    
    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @return 添加成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTag(Tag tag) {
        // 检查标签名是否已存在
        long count = count(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, tag.getName()));
        if (count > 0) {
            return false;
        }
        
        return save(tag);
    }
    
    /**
     * 更新标签
     *
     * @param tag 标签信息
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTag(Tag tag) {
        // 检查标签名是否已存在（排除自身）
        long count = count(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, tag.getName())
                .ne(Tag::getId, tag.getId()));
        if (count > 0) {
            return false;
        }
        
        return updateById(tag);
    }
    
    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(Long id) {
        // 检查标签是否被文章引用
        LambdaQueryWrapper<ArticleTag> articleTagWrapper = new LambdaQueryWrapper<>();
        articleTagWrapper.eq(ArticleTag::getTagId, id);
        long count = articleTagMapper.selectCount(articleTagWrapper);
        if (count > 0) {
            return false; // 如果标签被文章引用，则不允许删除
        }
        
        return removeById(id);
    }
    
    /**
     * 获取所有标签（用于下拉选择）
     *
     * @return 所有标签列表
     */
    @Override
    public List<Tag> getAllTags() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Tag::getName);
        
        List<Tag> tags = list(queryWrapper);
        
        // 获取每个标签的文章数量
        for (Tag tag : tags) {
            LambdaQueryWrapper<ArticleTag> articleTagWrapper = new LambdaQueryWrapper<>();
            articleTagWrapper.eq(ArticleTag::getTagId, tag.getId());
            long count = articleTagMapper.selectCount(articleTagWrapper);
            tag.setArticleCount((int)count);
        }
        
        return tags;
    }

    @Override
    public IPage<Tag> getTagList(Integer page, Integer size, String keyword, String sortField, String sortOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTagList'");
    }
}