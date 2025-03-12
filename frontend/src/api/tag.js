import request from './request';

// 标签相关的API服务
export default {
  // 获取标签列表
  getTagList(params) {
    return request({
      url: '/tags',
      method: 'get',
      params
    });
  },
  
  // 获取标签详情
  getTagById(id) {
    return request({
      url: `/tags/${id}`,
      method: 'get'
    });
  },
  
  // 添加标签
  addTag(data) {
    return request({
      url: '/tags',
      method: 'post',
      data
    });
  },
  
  // 更新标签
  updateTag(id, data) {
    return request({
      url: `/tags/${id}`,
      method: 'put',
      data
    });
  },
  
  // 删除标签
  deleteTag(id) {
    return request({
      url: `/tags/${id}`,
      method: 'delete'
    });
  },
  
  // 获取所有标签（用于下拉选择）
  getAllTags() {
    return request({
      url: '/tags/all',
      method: 'get'
    });
  },
  
  // 批量删除标签
  batchDeleteTags(ids) {
    return request({
      url: '/tags/batch',
      method: 'delete',
      data: { ids }
    });
  }
};