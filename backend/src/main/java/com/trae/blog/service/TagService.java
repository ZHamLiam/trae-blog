package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {
    
    /**
     * 分页获取标签列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词
     * @return 标签分页列表
     */
    IPage<Tag> getTagList(Integer page, Integer size, String keyword);
    
    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    Tag getTagById(Long id);
    
    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @return 添加成功返回true，失败返回false
     */
    boolean addTag(Tag tag);
    
    /**
     * 更新标签
     *
     * @param tag 标签信息
     * @return 更新成功返回true，失败返回false
     */
    boolean updateTag(Tag tag);
    
    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteTag(Long id);
    
    /**
     * 获取所有标签（用于下拉选择）
     *
     * @return 所有标签列表
     */
    List<Tag> getAllTags();
}