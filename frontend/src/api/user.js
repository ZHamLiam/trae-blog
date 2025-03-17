import request from './request';

// 用户相关的API服务
export default {
  // 用户登录
  login(data) {
    return request({
      url: '/users/login',
      method: 'post',
      data
    });
  },
  
  // 用户注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    });
  },
  
  // 获取当前登录用户信息
  getUserInfo() {
    return request({
      url: '/users/info',
      method: 'get'
    });
  },
  
  // 根据ID获取用户信息
  getUserById(id) {
    return request({
      url: `/users/${id}`,
      method: 'get'
    });
  },
  
  // 获取用户列表（管理员权限）
  getUserList(params) {
    return request({
      url: '/users',
      method: 'get',
      params
    });
  },
  
  // 更新用户信息
  updateUser(id, data) {
    return request({
      url: `/users/${id}`,
      method: 'put',
      data
    });
  },
  
  // 启用用户
  enableUser(id) {
    return request({
      url: `/users/${id}/enable`,
      method: 'put'
    });
  },
  
  // 禁用用户
  disableUser(id) {
    return request({
      url: `/users/${id}/disable`,
      method: 'put'
    });
  },
  
  // 删除用户（管理员权限）
  deleteUser(id) {
    return request({
      url: `/users/${id}`,
      method: 'delete'
    });
  },
  
  // 修改密码
  changePassword(data) {
    return request({
      url: '/users/password',
      method: 'put',
      data
    });
  },
  
  // 上传头像
  uploadAvatar(data) {
    return request({
      url: '/users/avatar',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  
  // 获取用户的角色ID列表
  getUserRoleIds(id) {
    return request({
      url: `/users/${id}/roles`,
      method: 'get'
    });
  },
  
  // 分配用户角色
  assignRoles(id, data) {
    return request({
      url: `/users/${id}/roles`,
      method: 'post',
      data
    });
  }
};