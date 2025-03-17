import { defineStore } from 'pinia';
import userApi from '@/api/user';
import { ref } from 'vue';

// 定义用户相关的存储
export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({});
  // 用户token
  const token = ref(localStorage.getItem('token') || '');
  // 加载状态
  const loading = ref(false);

  // 登录
  const login = async (loginData) => {
    loading.value = true;
    try {
      const res = await userApi.login(loginData);
      if (res.code === 200) {
        token.value = res.data;
        localStorage.setItem('token', res.data);
        return true;
      }
      return false;
    } catch (error) {
      console.error('登录失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  // 获取用户信息
  const getUserInfo = async () => {
    loading.value = true;
    try {
      const res = await userApi.getUserInfo();
      if (res.code === 200) {
        userInfo.value = res.data;
        return res.data;
      }
      return null;
    } catch (error) {
      console.error('获取用户信息失败:', error);
      return null;
    } finally {
      loading.value = false;
    }
  };

  // 登出
  const logout = () => {
    // 清除token
    token.value = '';
    localStorage.removeItem('token');
    // 清除用户信息
    userInfo.value = {};
  };

  return {
    userInfo,
    token,
    loading,
    login,
    getUserInfo,
    logout
  };
});