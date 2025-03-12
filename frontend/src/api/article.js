import request from './request';

// 文章相关的API服务
export default {
  // 获取文章列表
  getArticleList(params) {
    return request({
      url: '/articles',
      method: 'get',
      params
    });
  },
  
  // 获取文章详情
  getArticleById(id) {
    return request({
      url: `/articles/${id}`,
      method: 'get'
    });
  },
  
  // 添加文章
  addArticle(data) {
    return request({
      url: '/articles',
      method: 'post',
      data
    });
  },
  
  // 更新文章
  updateArticle(id, data) {
    return request({
      url: `/articles/${id}`,
      method: 'put',
      data
    });
  },
  
  // 删除文章
  deleteArticle(id) {
    return request({
      url: `/articles/${id}`,
      method: 'delete'
    });
  },
  
  // 更新文章状态（发布/草稿）
  updateArticleStatus(id, status) {
    return request({
      url: `/articles/${id}/status/${status}`,
      method: 'put'
    });
  },
  
  // 更新文章置顶状态
  updateArticleTopStatus(id, isTop) {
    return request({
      url: `/articles/${id}/top/${isTop}`,
      method: 'put'
    });
  },
  
  // 获取热门文章
  getHotArticles() {
    return request({
      url: '/articles/hot',
      method: 'get'
    });
  },
  
  // 获取最新文章
  getRecentArticles() {
    return request({
      url: '/articles/recent',
      method: 'get'
    });
  },
  
  // 批量删除文章
  batchDeleteArticles(ids) {
    return request({
      url: '/articles/batch',
      method: 'delete',
      data: { ids }
    });
  }
};