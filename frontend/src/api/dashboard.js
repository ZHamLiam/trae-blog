import request from './request';

// 仪表盘相关的API服务
export default {
  // 获取仪表盘统计数据
  getDashboardStats() {
    return request({
      url: '/dashboard/stats',
      method: 'get'
    });
  },
  
  // 获取统计图表数据
  getStatisticsData() {
    return request({
      url: '/dashboard/statistics',
      method: 'get'
    });
  }
};