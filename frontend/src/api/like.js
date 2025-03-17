import request from './request';

// 点赞相关的API服务
export default {
  // 点赞文章
  likeArticle(articleId) {
    return request({
      url: `/likes/article/${articleId}`,
      method: 'post'
    });
  },
  
  // 取消文章点赞
  unlikeArticle(articleId) {
    return request({
      url: `/likes/article/${articleId}`,
      method: 'delete'
    });
  },
  
  // 获取用户对文章的点赞状态
  getArticleLikeStatus(articleId) {
    return request({
      url: `/likes/article/${articleId}/status`,
      method: 'get'
    });
  },
  
  // 点赞评论
  likeComment(commentId) {
    return request({
      url: `/likes/comment/${commentId}`,
      method: 'post'
    });
  },
  
  // 取消评论点赞
  unlikeComment(commentId) {
    return request({
      url: `/likes/comment/${commentId}`,
      method: 'delete'
    });
  },
  
  // 获取用户对评论的点赞状态
  getCommentLikeStatus(commentId) {
    return request({
      url: `/likes/comment/${commentId}/status`,
      method: 'get'
    });
  }
};