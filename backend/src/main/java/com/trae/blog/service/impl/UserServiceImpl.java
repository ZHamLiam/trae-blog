package com.trae.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trae.blog.entity.User;
import com.trae.blog.mapper.UserMapper;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        
        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        
        // 将用户信息存入Redis，设置过期时间
        redisTemplate.opsForValue().set("token:" + token, user, expiration, TimeUnit.SECONDS);
        
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
        
        // 从Redis获取用户信息
        return (User) redisTemplate.opsForValue().get("token:" + token);
    }
    
    /**
     * 分页获取用户列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 用户分页列表
     */
    @Override
    public IPage<User> getUserList(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getEmail, keyword);
        }
        
        // 排序
        queryWrapper.orderByDesc(User::getCreateTime);
        
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
}