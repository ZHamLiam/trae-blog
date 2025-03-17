package com.trae.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.common.exception.BusinessException;
import com.trae.blog.entity.Permission;
import com.trae.blog.entity.RolePermission;
import com.trae.blog.entity.UserRole;
import com.trae.blog.mapper.PermissionMapper;
import com.trae.blog.mapper.RolePermissionMapper;
import com.trae.blog.mapper.UserRoleMapper;
import com.trae.blog.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    /**
     * 分页获取权限列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @param type    权限类型
     * @return 权限分页列表
     */
    @Override
    public IPage<Permission> getPermissionList(Integer page, Integer size, String keyword, Integer type) {
        Page<Permission> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加类型查询条件
        if (type != null) {
            queryWrapper.eq(Permission::getType, type);
        }
        
        // 添加关键词查询条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Permission::getName, keyword)
                    .or()
                    .like(Permission::getCode, keyword);
        }
        
        // 按排序值降序、创建时间降序排列
        queryWrapper.orderByDesc(Permission::getSort)
                .orderByDesc(Permission::getCreateTime);
        
        return page(pageParam, queryWrapper);
    }
    
    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    @Override
    public List<Permission> getAllPermissions() {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getStatus, 1) // 只查询正常状态的权限
                .orderByDesc(Permission::getSort)
                .orderByDesc(Permission::getCreateTime);
        
        return list(queryWrapper);
    }
    
    /**
     * 根据ID获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    @Override
    public Permission getPermissionById(Long id) {
        return getById(id);
    }
    
    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPermission(Permission permission) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getCode, permission.getCode());
        long count = count(queryWrapper);
        if (count > 0) {
            throw new BusinessException("权限编码已存在，请使用其他编码");
        }
        return save(permission);
    }
    
    /**
     * 更新权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        // 检查权限编码是否已存在（排除自身）
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getCode, permission.getCode())
                .ne(Permission::getId, permission.getId());
        long count = count(queryWrapper);
        if (count > 0) {
            throw new BusinessException("权限编码已存在，请使用其他编码");
        }
        return updateById(permission);
    }
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long id) {
        // 删除权限前，先删除与该权限关联的角色权限关系
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(queryWrapper);
        
        // 删除权限
        return removeById(id);
    }
    
    /**
     * 获取权限树
     *
     * @return 权限树
     */
    @Override
    public List<Map<String, Object>> getPermissionTree() {
        // 获取所有权限
        List<Permission> allPermissions = getAllPermissions();
        
        // 构建树形结构
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取顶级权限
        List<Permission> topPermissions = allPermissions.stream()
                .filter(permission -> permission.getParentId() != null && permission.getParentId().equals(0L))
                .collect(Collectors.toList());
        
        // 递归构建树形结构
        for (Permission topPermission : topPermissions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", topPermission.getId());
            map.put("name", topPermission.getName());
            map.put("code", topPermission.getCode());
            map.put("type", topPermission.getType());
            map.put("path", topPermission.getPath());
            map.put("component", topPermission.getComponent());
            map.put("icon", topPermission.getIcon());
            map.put("sort", topPermission.getSort());
            map.put("status", topPermission.getStatus());
            
            // 递归获取子权限
            List<Map<String, Object>> children = getChildrenPermissions(topPermission.getId(), allPermissions);
            if (!children.isEmpty()) {
                map.put("children", children);
            }
            
            result.add(map);
        }
        
        return result;
    }
    
    /**
     * 递归获取子权限
     *
     * @param parentId       父权限ID
     * @param allPermissions 所有权限列表
     * @return 子权限列表
     */
    private List<Map<String, Object>> getChildrenPermissions(Long parentId, List<Permission> allPermissions) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取指定父权限ID的子权限
        List<Permission> childrenPermissions = allPermissions.stream()
                .filter(permission -> permission.getParentId().equals(parentId))
                .collect(Collectors.toList());
        
        // 递归构建树形结构
        for (Permission childPermission : childrenPermissions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", childPermission.getId());
            map.put("name", childPermission.getName());
            map.put("code", childPermission.getCode());
            map.put("type", childPermission.getType());
            map.put("path", childPermission.getPath());
            map.put("component", childPermission.getComponent());
            map.put("icon", childPermission.getIcon());
            map.put("sort", childPermission.getSort());
            map.put("status", childPermission.getStatus());
            
            // 递归获取子权限
            List<Map<String, Object>> children = getChildrenPermissions(childPermission.getId(), allPermissions);
            if (!children.isEmpty()) {
                map.put("children", children);
            }
            
            result.add(map);
        }
        
        return result;
    }
    
    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        // 查询用户角色
        LambdaQueryWrapper<UserRole> userRoleQueryWrapper = new LambdaQueryWrapper<>();
        userRoleQueryWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleQueryWrapper);
        
        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询角色权限
        LambdaQueryWrapper<RolePermission> rolePermissionQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionQueryWrapper.in(RolePermission::getRoleId, roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionQueryWrapper);
        
        // 获取权限ID列表
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());
        
        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询权限
        LambdaQueryWrapper<Permission> permissionQueryWrapper = new LambdaQueryWrapper<>();
        permissionQueryWrapper.in(Permission::getId, permissionIds)
                .eq(Permission::getStatus, 1);
        
        return list(permissionQueryWrapper);
    }
    
    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        // 查询角色权限
        LambdaQueryWrapper<RolePermission> rolePermissionQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionQueryWrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionQueryWrapper);
        
        // 获取权限ID列表
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        
        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询权限
        LambdaQueryWrapper<Permission> permissionQueryWrapper = new LambdaQueryWrapper<>();
        permissionQueryWrapper.in(Permission::getId, permissionIds)
                .eq(Permission::getStatus, 1);
        
        return list(permissionQueryWrapper);
    }
}