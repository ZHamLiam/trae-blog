import axios from 'axios';
import { message } from 'ant-design-vue';

// 创建axios实例
const request = axios.create({
  // 基础URL，实际开发中应该根据环境配置
  baseURL: 'http://localhost:8080/api',
  // 请求超时时间
  timeout: 10000
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    // 添加调试日志，查看token的值
    console.log('当前token值:', token);
    
    // 如果有token且token不是undefined或null，则添加到请求头
    if (token && token !== 'undefined' && token !== 'null') {
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log('添加到请求头的Authorization:', `Bearer ${token}`);
    } else {
      console.warn('Token无效或不存在，不添加到请求头');
    }
    return config;
  },
  error => {
    // 请求错误处理
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    // 否则的话抛出错误
    if (response.status === 200) {
      return response.data;
    }
    
    message.error('请求失败');
    return Promise.reject(new Error('请求失败'));
  },
  error => {
    // 处理HTTP状态码为401的情况（未授权）
    if (error.response && error.response.status === 401) {
      message.error('登录已过期，请重新登录');
      // 清除token
      localStorage.removeItem('token');
      // 跳转到登录页
      window.location.href = '/login';
      return Promise.reject(error);
    }
    
    // 处理HTTP状态码为403的情况（禁止访问）
    if (error.response && error.response.status === 403) {
      message.error('没有权限访问');
      return Promise.reject(error);
    }
    
    // 处理HTTP状态码为404的情况（资源不存在）
    if (error.response && error.response.status === 404) {
      message.error('请求的资源不存在');
      return Promise.reject(error);
    }
    
    // 处理HTTP状态码为500的情况（服务器错误）
    if (error.response && error.response.status === 500) {
      message.error('服务器错误，请稍后再试');
      return Promise.reject(error);
    }
    
    // 其他错误处理
    message.error(error.message || '请求失败，请稍后再试');
    return Promise.reject(error);
  }
);

export default request;