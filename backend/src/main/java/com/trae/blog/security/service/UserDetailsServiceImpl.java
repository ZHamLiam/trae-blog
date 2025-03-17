package com.trae.blog.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trae.blog.entity.Permission;
import com.trae.blog.entity.Role;
import com.trae.blog.entity.User;
import com.trae.blog.entity.UserRole;
import com.trae.blog.mapper.UserMapper;
import com.trae.blog.mapper.UserRoleMapper;
import com.trae.blog.service.PermissionService;
import com.trae.blog.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义UserDetailsService实现类，用于加载用户信息
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getStatus, 1));

        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 创建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 查询用户角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, user.getId());
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        
        // 添加角色权限
        for (UserRole userRole : userRoles) {
            Role role = roleService.getById(userRole.getRoleId());
            if (role != null && role.getStatus() == 1) {
                // 添加角色，需要添加ROLE_前缀
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode().toUpperCase()));
                
                // 查询角色对应的权限
                List<Permission> permissions = permissionService.getPermissionsByRoleId(role.getId());
                for (Permission permission : permissions) {
                    if (permission.getStatus() == 1) {
                        // 添加权限
                        authorities.add(new SimpleGrantedAuthority(permission.getCode()));
                    }
                }
            }
        }
        
        // 如果没有分配角色，使用默认角色（兼容旧数据）
        if (authorities.isEmpty() && user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
        }

        // 返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}