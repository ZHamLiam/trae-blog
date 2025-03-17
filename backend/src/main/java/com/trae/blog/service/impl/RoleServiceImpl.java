package com.trae.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.Role;
import com.trae.blog.entity.RolePermission;
import com.trae.blog.entity.UserRole;
import com.trae.blog.mapper.RoleMapper;
import com.trae.blog.mapper.RolePermissionMapper;
import com.trae.blog.mapper.UserRoleMapper;
import com.trae.blog.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    /**
     * 分页获取角色列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 角色分页列表
     */
    @Override
    public IPage<Role> getRoleList(Integer page, Integer size, String keyword) {
        Page<Role> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加关键词查询条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Role::getName, keyword)
                    .or()
                    .like(Role::getCode, keyword)
                    .or()
                    .like(Role::getDescription, keyword);
        }
        
        // 按排序值降序、创建时间降序排列
        queryWrapper.orderByDesc(Role::getSort)
                .orderByDesc(Role::getCreateTime);
        
        return page(pageParam, queryWrapper);
    }
    
    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, 1) // 只查询正常状态的角色
                .orderByDesc(Role::getSort)
                .orderByDesc(Role::getCreateTime);
        
        return list(queryWrapper);
    }
    
    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @Override
    public Role getRoleById(Long id) {
        return getById(id);
    }
    
    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        return save(role);
    }
    
    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        return updateById(role);
    }
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        // 删除角色前，先删除与该角色关联的角色权限关系
        LambdaQueryWrapper<RolePermission> rolePermissionQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionQueryWrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(rolePermissionQueryWrapper);
        
        // 删除角色前，先删除与该角色关联的用户角色关系
        LambdaQueryWrapper<UserRole> userRoleQueryWrapper = new LambdaQueryWrapper<>();
        userRoleQueryWrapper.eq(UserRole::getRoleId, id);
        userRoleMapper.delete(userRoleQueryWrapper);
        
        // 删除角色
        return removeById(id);
    }
    
    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有的角色权限关系
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(queryWrapper);
        
        // 如果权限ID列表为空，则直接返回成功
        if (permissionIds == null || permissionIds.isEmpty()) {
            return true;
        }
        
        // 批量添加新的角色权限关系
        List<RolePermission> rolePermissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissions.add(rolePermission);
        }
        
        // 批量插入
        for (RolePermission rolePermission : rolePermissions) {
            rolePermissionMapper.insert(rolePermission);
        }
        
        return true;
    }
    
    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(queryWrapper);
        
        return rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }
    
    /**
     * 启用角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableRole(Long id) {
        Role role = getById(id);
        if (role == null) {
            return false;
        }
        
        role.setStatus(1); // 设置为正常状态
        return updateById(role);
    }
    
    /**
     * 禁用角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableRole(Long id) {
        Role role = getById(id);
        if (role == null) {
            return false;
        }
        
        role.setStatus(0); // 设置为禁用状态
        return updateById(role);
    }
}