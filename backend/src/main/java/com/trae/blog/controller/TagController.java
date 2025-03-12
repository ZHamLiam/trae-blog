package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.Tag;
import com.trae.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 分页获取标签列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 标签分页列表
     */
    @GetMapping
    public Result<IPage<Tag>> getTagList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Tag> tagList = tagService.getTagList(page, size, keyword);
        return Result.success(tagList);
    }

    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.getTagById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }
        return Result.success(tag);
    }

    /**
     * 添加标签
     *
     * @param tag 标签信息
     * @return 添加结果
     */
    @PostMapping
    public Result<Boolean> addTag(@RequestBody Tag tag) {
        boolean result = tagService.addTag(tag);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("添加标签失败，可能标签名已存在");
        }
    }

    /**
     * 更新标签
     *
     * @param id  标签ID
     * @param tag 标签信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateTag(
            @PathVariable Long id,
            @RequestBody Tag tag) {
        tag.setId(id);
        boolean result = tagService.updateTag(tag);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新标签失败，可能标签名已存在");
        }
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTag(@PathVariable Long id) {
        boolean result = tagService.deleteTag(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除标签失败，可能该标签下有文章");
        }
    }

    /**
     * 获取所有标签（用于下拉选择）
     *
     * @return 所有标签列表
     */
    @GetMapping("/all")
    public Result<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return Result.success(tags);
    }
    
    /**
     * 批量删除标签
     *
     * @param params 包含标签ID列表的参数
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteTags(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("标签ID列表不能为空");
        }
        
        // 检查是否有标签被文章引用
        for (Long id : ids) {
            if (!tagService.deleteTag(id)) {
                return Result.error("删除失败，ID为" + id + "的标签下有关联的文章");
            }
        }
        
        return Result.success(true);
    }
}