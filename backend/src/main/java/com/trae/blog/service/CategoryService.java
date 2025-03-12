package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    
    /**
     * 分页获取分类列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词
     * @return 分类分页列表
     */
    IPage<Category> getCategoryList(Integer page, Integer size, String keyword);
    
    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    Category getCategoryById(Long id);
    
    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加成功返回true，失败返回false
     */
    boolean addCategory(Category category);
    
    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 更新成功返回true，失败返回false
     */
    boolean updateCategory(Category category);
    
    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteCategory(Long id);
    
    /**
     * 获取所有分类（用于下拉选择）
     *
     * @return 所有分类列表
     */
    List<Category> getAllCategories();
}