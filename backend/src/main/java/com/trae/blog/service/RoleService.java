package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 分页获取角色列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 角色分页列表
     */
    IPage<Role> getRoleList(Integer page, Integer size, String keyword);
    
    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<Role> getAllRoles();
    
    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    Role getRoleById(Long id);
    
    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean createRole(Role role);
    
    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(Role role);
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long id);
    
    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
    
    /**
     * 启用角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean enableRole(Long id);
    
    /**
     * 禁用角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean disableRole(Long id);
}