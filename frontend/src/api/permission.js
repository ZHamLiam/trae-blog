import request from './request';

// 权限相关的API服务
export default {
  // 获取权限列表
  getPermissionList(params) {
    return request({
      url: '/permissions',
      method: 'get',
      params
    });
  },
  
  // 获取所有权限
  getAllPermissions() {
    return request({
      url: '/permissions/all',
      method: 'get'
    });
  },
  
  // 获取权限树
  getPermissionTree() {
    return request({
      url: '/permissions/tree',
      method: 'get'
    });
  },
  
  // 获取权限详情
  getPermissionById(id) {
    return request({
      url: `/permissions/${id}`,
      method: 'get'
    });
  },
  
  // 创建权限
  createPermission(data) {
    return request({
      url: '/permissions',
      method: 'post',
      data
    });
  },
  
  // 更新权限
  updatePermission(id, data) {
    return request({
      url: `/permissions/${id}`,
      method: 'put',
      data
    });
  },
  
  // 删除权限
  deletePermission(id) {
    return request({
      url: `/permissions/${id}`,
      method: 'delete'
    });
  }
};