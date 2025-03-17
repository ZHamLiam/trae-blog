package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.Role;
import com.trae.blog.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页获取角色列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 角色分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public Result<IPage<Role>> getRoleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Role> roleList = roleService.getRoleList(page, size, keyword);
        return Result.success(roleList);
    }

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public Result<Boolean> createRole(@RequestBody Role role) {
        boolean result = roleService.createRole(role);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("创建角色失败");
        }
    }

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Boolean> updateRole(
            @PathVariable Long id,
            @RequestBody Role role) {
        role.setId(id);
        boolean result = roleService.updateRole(role);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新角色失败");
        }
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Boolean> deleteRole(@PathVariable Long id) {
        boolean result = roleService.deleteRole(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除角色失败");
        }
    }

    /**
     * 获取角色的权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<List<Long>> getRolePermissionIds(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissionIds(id);
        return Result.success(permissionIds);
    }

    /**
     * 分配角色权限
     *
     * @param id     角色ID
     * @param params 包含权限ID列表的参数
     * @return 分配结果
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public Result<Boolean> assignPermissions(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> params) {
        List<Long> permissionIds = params.get("permissionIds");
        if (permissionIds == null) {
            return Result.error("权限ID列表不能为空");
        }
        
        boolean result = roleService.assignPermissions(id, permissionIds);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("分配角色权限失败");
        }
    }
    
    /**
     * 启用角色
     *
     * @param id 角色ID
     * @return 启用结果
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Boolean> enableRole(@PathVariable Long id) {
        boolean result = roleService.enableRole(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("启用角色失败");
        }
    }
    
    /**
     * 禁用角色
     *
     * @param id 角色ID
     * @return 禁用结果
     */
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Boolean> disableRole(@PathVariable Long id) {
        boolean result = roleService.disableRole(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("禁用角色失败");
        }
    }
}