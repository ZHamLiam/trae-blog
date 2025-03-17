package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.Permission;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<Permission> {
    
    /**
     * 分页获取权限列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @param type    权限类型
     * @return 权限分页列表
     */
    IPage<Permission> getPermissionList(Integer page, Integer size, String keyword, Integer type);
    
    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    List<Permission> getAllPermissions();
    
    /**
     * 根据ID获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    Permission getPermissionById(Long id);
    
    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean createPermission(Permission permission);
    
    /**
     * 更新权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean updatePermission(Permission permission);
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    boolean deletePermission(Long id);
    
    /**
     * 获取权限树
     *
     * @return 权限树
     */
    List<Map<String, Object>> getPermissionTree();
    
    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);
    
    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);
}