import request from './request';

// 分类相关的API服务
export default {
  // 获取分类列表
  getCategoryList(params) {
    return request({
      url: '/categories',
      method: 'get',
      params
    });
  },
  
  // 获取分类详情
  getCategoryById(id) {
    return request({
      url: `/categories/${id}`,
      method: 'get'
    });
  },
  
  // 添加分类
  addCategory(data) {
    return request({
      url: '/categories',
      method: 'post',
      data
    });
  },
  
  // 更新分类
  updateCategory(id, data) {
    return request({
      url: `/categories/${id}`,
      method: 'put',
      data
    });
  },
  
  // 删除分类
  deleteCategory(id) {
    return request({
      url: `/categories/${id}`,
      method: 'delete'
    });
  },
  
  // 获取所有分类（用于下拉选择）
  getAllCategories() {
    return request({
      url: '/categories/all',
      method: 'get'
    });
  },
  
  // 批量删除分类
  batchDeleteCategories(ids) {
    return request({
      url: '/categories/batch',
      method: 'delete',
      data: { ids }
    });
  }
};