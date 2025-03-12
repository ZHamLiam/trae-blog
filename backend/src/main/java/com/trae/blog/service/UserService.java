package com.trae.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trae.blog.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回token，失败返回null
     */
    String login(String username, String password);
    
    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册成功返回true，失败返回false
     */
    boolean register(User user);
    
    /**
     * 根据token获取用户信息
     *
     * @return 用户信息
     */
    User getUserInfo();
    
    /**
     * 分页获取用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词
     * @return 用户分页列表
     */
    IPage<User> getUserList(Integer page, Integer size, String keyword);
    
    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新成功返回true，失败返回false
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteUser(Long id);
    
    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，失败返回false
     */
    boolean changePassword(String oldPassword, String newPassword);
}