package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.Category;
import com.trae.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页获取分类列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 分类分页列表
     */
    @GetMapping
    public Result<IPage<Category>> getCategoryList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Category> categoryList = categoryService.getCategoryList(page, size, keyword);
        return Result.success(categoryList);
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Boolean> addCategory(@RequestBody Category category) {
        boolean result = categoryService.addCategory(category);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("添加分类失败，可能分类名已存在");
        }
    }

    /**
     * 更新分类
     *
     * @param id       分类ID
     * @param category 分类信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {
        category.setId(id);
        boolean result = categoryService.updateCategory(category);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新分类失败，可能分类名已存在");
        }
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        boolean result = categoryService.deleteCategory(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除分类失败，可能该分类下有文章");
        }
    }

    /**
     * 获取所有分类（用于下拉选择）
     *
     * @return 所有分类列表
     */
    @GetMapping("/all")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 批量删除分类
     *
     * @param params 包含分类ID列表的参数
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteCategories(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("分类ID列表不能为空");
        }
        
        // 检查是否有分类被文章引用
        for (Long id : ids) {
            if (!categoryService.deleteCategory(id)) {
                return Result.error("删除失败，ID为" + id + "的分类下有关联的文章");
            }
        }
        
        return Result.success(true);
    }
}