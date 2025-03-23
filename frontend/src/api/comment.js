import request from './request';

// 评论相关的API服务
export default {
  // 获取评论列表
  getCommentList(params) {
    return request({
      url: '/comments',
      method: 'get',
      params
    });
  },
  
  // 获取评论详情
  getCommentById(id) {
    return request({
      url: `/comments/${id}`,
      method: 'get'
    });
  },
  
  // 添加评论
  addComment(data) {
    return request({
      url: '/comments',
      method: 'post',
      data
    });
  },
  
  // 更新评论状态（审核通过/拒绝）
  updateCommentStatus(id, status) {
    return request({
      url: `/comments/${id}/status/${status}`,
      method: 'put'
    });
  },
  
  // 删除评论
  deleteComment(id) {
    return request({
      url: `/comments/${id}`,
      method: 'delete'
    });
  },
  
  // 获取最新评论
  getRecentComments() {
    return request({
      url: '/comments/recent',
      method: 'get'
    });
  },
  
  // 批量删除评论
  batchDeleteComments(ids) {
    return request({
      url: '/comments/batch',
      method: 'delete',
      data: { ids }
    });
  }
};