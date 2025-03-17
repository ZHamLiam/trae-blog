package com.trae.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trae.blog.common.Result;
import com.trae.blog.entity.User;
import com.trae.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginMap 登录信息，包含username和password
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> loginMap) {
        String username = loginMap.get("username");
        String password = loginMap.get("password");
        
        String token = userService.login(username, password);
        if (token != null) {
            return Result.success("登录成功", token);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody User user) {
        boolean result = userService.register(user);
        if (result) {
            return Result.success("注册成功", true);
        } else {
            return Result.error("注册失败，可能用户名已存在");
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        User user = userService.getUserInfo();
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("获取用户信息失败，请重新登录");
        }
    }

    /**
     * 分页获取用户列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 用户分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:view')")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        IPage<User> userList = userService.getUserList(page, size, keyword, status);
        return Result.success(userList);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 出于安全考虑，不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     *
     * @param id   用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public Result<Boolean> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        user.setId(id);
        boolean result = userService.updateUser(user);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新用户信息失败");
        }
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除用户失败");
        }
    }

    /**
     * 修改密码
     *
     * @param passwordMap 密码信息，包含oldPassword和newPassword
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        
        boolean result = userService.changePassword(oldPassword, newPassword);
        if (result) {
            return Result.success("密码修改成功", true);
        } else {
            return Result.error("密码修改失败，可能旧密码不正确");
        }
    }
    
    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        
        try {
            // 获取当前用户
            User user = userService.getUserInfo();
            if (user == null) {
                return Result.error("用户未登录");
            }
            
            // 调用文件上传服务
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            
            // 按日期构建目录
            String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String uploadPath = System.getProperty("user.dir") + "/uploads/avatar/";
            File targetDir = new File(uploadPath + dateDir);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            
            // 构建文件路径
            File targetFile = new File(targetDir.getAbsolutePath() + File.separator + fileName);
            
            // 保存文件
            file.transferTo(targetFile);
            
            // 构建访问URL
            String fileUrl = "/api/uploads/avatar/" + dateDir + "/" + fileName;
            
            // 更新用户头像
            user.setAvatar(fileUrl);
            userService.updateById(user);
            
            return Result.success("头像上传成功", fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的角色ID列表
     *
     * @param id 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long id) {
        List<Long> roleIds = userService.getUserRoleIds(id);
        return Result.success(roleIds);
    }
    
    /**
     * 分配用户角色
     *
     * @param id     用户ID
     * @param params 包含角色ID列表的参数
     * @return 分配结果
     */
    @PostMapping("/{id}/roles")
    public Result<Boolean> assignRoles(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> params) {
        List<Long> roleIds = params.get("roleIds");
        if (roleIds == null) {
            return Result.error("角色ID列表不能为空");
        }
        
        boolean result = userService.assignRoles(id, roleIds);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("分配用户角色失败");
        }
    }
    
    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 启用结果
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('user:update')")
    public Result<Boolean> enableUser(@PathVariable Long id) {
        boolean result = userService.enableUser(id);
        if (result) {
            return Result.success("用户启用成功", true);
        } else {
            return Result.error("用户启用失败");
        }
    }
    
    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 禁用结果
     */
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('user:update')")
    public Result<Boolean> disableUser(@PathVariable Long id) {
        boolean result = userService.disableUser(id);
        if (result) {
            return Result.success("用户禁用成功", true);
        } else {
            return Result.error("用户禁用失败");
        }
    }
}