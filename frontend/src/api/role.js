import request from './request';

// 角色相关的API服务
export default {
  // 获取角色列表
  getRoleList(params) {
    return request({
      url: '/roles',
      method: 'get',
      params
    });
  },
  
  // 获取所有角色
  getAllRoles() {
    return request({
      url: '/roles/all',
      method: 'get'
    });
  },
  
  // 获取角色详情
  getRoleById(id) {
    return request({
      url: `/roles/${id}`,
      method: 'get'
    });
  },
  
  // 创建角色
  createRole(data) {
    return request({
      url: '/roles',
      method: 'post',
      data
    });
  },
  
  // 更新角色
  updateRole(id, data) {
    return request({
      url: `/roles/${id}`,
      method: 'put',
      data
    });
  },
  
  // 删除角色
  deleteRole(id) {
    return request({
      url: `/roles/${id}`,
      method: 'delete'
    });
  },
  
  // 获取角色的权限ID列表
  getRolePermissionIds(id) {
    return request({
      url: `/roles/${id}/permissions`,
      method: 'get'
    });
  },
  
  // 分配角色权限
  assignPermissions(id, data) {
    return request({
      url: `/roles/${id}/permissions`,
      method: 'post',
      data
    });
  },
  
  // 启用角色
  enableRole(id) {
    return request({
      url: `/roles/${id}/enable`,
      method: 'put'
    });
  },
  
  // 禁用角色
  disableRole(id) {
    return request({
      url: `/roles/${id}/disable`,
      method: 'put'
    });
  }
};