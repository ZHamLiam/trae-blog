import { defineStore } from 'pinia';
import permissionApi from '@/api/permission';
import userApi from '@/api/user';
import { ref } from 'vue';

// 定义权限和菜单相关的存储
export const usePermissionStore = defineStore('permission', () => {
  // 用户菜单列表
  const menus = ref([]);
  // 用户权限列表
  const permissions = ref([]);
  // 菜单加载状态
  const loading = ref(false);

  // 获取用户菜单和权限
  const getUserMenusAndPermissions = async () => {
    loading.value = true;
    try {
      // 获取用户信息
      const userInfo = await userApi.getUserInfo();
      if (userInfo.code !== 200) {
        throw new Error(userInfo.message || '获取用户信息失败');
      }

      // 获取用户权限列表
      const permissionResult = await permissionApi.getPermissionTree();
      if (permissionResult.code !== 200) {
        throw new Error(permissionResult.message || '获取权限列表失败');
      }

      // 过滤出菜单类型的权限（type === 1）
      const menuPermissions = filterMenuPermissions(permissionResult.data);
      menus.value = menuPermissions;

      // 所有权限列表（包括按钮权限等）
      permissions.value = permissionResult.data;

      return { menus: menus.value, permissions: permissions.value };
    } catch (error) {
      console.error('获取用户菜单和权限失败:', error);
      return { menus: [], permissions: [] };
    } finally {
      loading.value = false;
    }
  };

  // 过滤菜单权限（type === 1 的为菜单）
  const filterMenuPermissions = (permissions) => {
    // 如果没有权限数据，返回空数组
    if (!permissions || !Array.isArray(permissions)) {
      return [];
    }

    // 过滤出菜单类型的权限并按sort字段排序
    return permissions
      .filter(item => item.type === 1 && item.status === 1)
      // 按sort字段降序排序（值越大越靠前）
      .sort((a, b) => (b.sort || 0) - (a.sort || 0))
      .map(item => ({
        key: item.path,
        title: item.name,
        icon: item.icon,
        sort: item.sort,
        children: item.children ? filterMenuPermissions(item.children) : []
      }));
  };

  // 重置状态
  const resetState = () => {
    menus.value = [];
    permissions.value = [];
  };

  return {
    menus,
    permissions,
    loading,
    getUserMenusAndPermissions,
    resetState
  };
});