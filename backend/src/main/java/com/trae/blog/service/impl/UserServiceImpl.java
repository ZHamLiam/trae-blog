package com.trae.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.User;
import com.trae.blog.entity.UserRole;
import com.trae.blog.mapper.UserMapper;
import com.trae.blog.mapper.UserRoleMapper;
import com.trae.blog.service.UserService;
import com.trae.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;
    
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回token，失败返回null
     */
    @Override
    public String login(String username, String password) {
        // 根据用户名查询用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getStatus, 1));
        
        // 用户不存在或密码不匹配
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        
        // 使用JWT工具类生成token
        String token = jwtUtil.generateToken(user);
        
        return token;
    }
    
    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        // 检查用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            return false;
        }
        
        // 设置默认值
        user.setRole("user");
        user.setStatus(1);
        
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 保存用户
        return save(user);
    }
    
    /**
     * 根据token获取用户信息
     *
     * @return 用户信息
     */
    @Override
    public User getUserInfo() {
        // 从请求头获取token
        String authHeader = request.getHeader(tokenHeader);
        if (StrUtil.isBlank(authHeader) || !authHeader.startsWith(tokenPrefix)) {
            return null;
        }
        
        // 提取token
        String token = authHeader.substring(tokenPrefix.length()).trim();
        
        try {
            // 从token中获取用户名
            String username = jwtUtil.getUsernameFromToken(token);
            
            // 根据用户名查询用户
            return getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
                    .eq(User::getStatus, 1));
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 分页获取用户列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @param status  用户状态，null表示全部状态，1表示正常，0表示禁用
     * @param sortField 排序字段
     * @param sortOrder 排序方式：ascend-升序，descend-降序
     * @return 用户分页列表
     */
    @Override
    public IPage<User> getUserList(Integer page, Integer size, String keyword, Integer status, String sortField, String sortOrder) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getEmail, keyword));
        }
        
        // 状态筛选
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        
        // 处理排序
        if (StrUtil.isNotBlank(sortField)) {
            // 根据前端传入的排序字段和排序方式进行排序
            switch (sortField) {
                case "createTime":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(User::getCreateTime);
                    } else {
                        queryWrapper.orderByDesc(User::getCreateTime);
                    }
                    break;
                case "username":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(User::getUsername);
                    } else {
                        queryWrapper.orderByDesc(User::getUsername);
                    }
                    break;
                case "nickname":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(User::getNickname);
                    } else {
                        queryWrapper.orderByDesc(User::getNickname);
                    }
                    break;
                case "email":
                    if ("ascend".equals(sortOrder)) {
                        queryWrapper.orderByAsc(User::getEmail);
                    } else {
                        queryWrapper.orderByDesc(User::getEmail);
                    }
                    break;
                default:
                    // 默认按创建时间降序排序
                    queryWrapper.orderByDesc(User::getCreateTime);
                    break;
            }
        } else {
            // 没有指定排序字段，默认按创建时间降序排序
            queryWrapper.orderByDesc(User::getCreateTime);
        }
        
        return page(pageParam, queryWrapper);
    }
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        // 如果密码不为空，则加密
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 不更新密码
            user.setPassword(null);
        }
        
        return updateById(user);
    }
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        return removeById(id);
    }
    
    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，失败返回false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(String oldPassword, String newPassword) {
        // 获取当前用户
        User user = getUserInfo();
        if (user == null) {
            return false;
        }
        
        // 验证旧密码
        User dbUser = getById(user.getId());
        if (!passwordEncoder.matches(oldPassword, dbUser.getPassword())) {
            return false;
        }
        
        // 更新密码
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        
        return updateById(updateUser);
    }
    
    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }
    
    /**
     * 分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除原有的用户角色关系
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(queryWrapper);
        
        // 如果角色ID列表为空，则直接返回成功
        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }
        
        // 批量添加新的用户角色关系
        List<UserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        
        // 批量插入
        for (UserRole userRole : userRoles) {
            userRoleMapper.insert(userRole);
        }
        
        return true;
    }
    
    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableUser(Long id) {
        User user = getById(id);
        if (user == null) {
            return false;
        }
        
        // 设置用户状态为启用(1)
        user.setStatus(1);
        return updateById(user);
    }
    
    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableUser(Long id) {
        User user = getById(id);
        if (user == null) {
            return false;
        }
        
        // 设置用户状态为禁用(0)
        user.setStatus(0);
        return updateById(user);
    }
}