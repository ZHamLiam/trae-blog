package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.Permission;
import com.trae.blog.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页获取权限列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @param type    权限类型
     * @return 权限分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<IPage<Permission>> getPermissionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {
        IPage<Permission> permissionList = permissionService.getPermissionList(page, size, keyword, type);
        return Result.success(permissionList);
    }

    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Map<String, Object>>> getPermissionTree() {
        List<Map<String, Object>> permissionTree = permissionService.getPermissionTree();
        return Result.success(permissionTree);
    }

    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public Result<Boolean> createPermission(@RequestBody Permission permission) {
        boolean result = permissionService.createPermission(permission);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("创建权限失败");
        }
    }

    /**
     * 更新权限
     *
     * @param id         权限ID
     * @param permission 权限信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public Result<Boolean> updatePermission(
            @PathVariable Long id,
            @RequestBody Permission permission) {
        permission.setId(id);
        boolean result = permissionService.updatePermission(permission);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新权限失败");
        }
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Result<Boolean> deletePermission(@PathVariable Long id) {
        boolean result = permissionService.deletePermission(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除权限失败");
        }
    }

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }
}