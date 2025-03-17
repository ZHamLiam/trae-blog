<script setup>
import { ref, computed, h, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  Layout,
  Menu,
  Breadcrumb,
  Dropdown,
  Avatar,
  Button,
  Divider,
  message,
  Spin
} from 'ant-design-vue';
import {
  DashboardOutlined,
  FileTextOutlined,
  TagsOutlined,
  UserOutlined,
  CommentOutlined,
  AppstoreOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined
} from '@ant-design/icons-vue';
import { usePermissionStore } from '@/store/permission';
import { useUserStore } from '@/store/user';
import { useThemeStore } from '@/store/theme';
import ThemeSwitch from '@/components/ThemeSwitch.vue';

// 导入主题CSS变量
import '@/assets/theme.css';

const router = useRouter();
const route = useRoute();
const permissionStore = usePermissionStore();
const userStore = useUserStore();

// 侧边栏折叠状态
const collapsed = ref(false);
// 菜单加载状态
const menuLoading = ref(true);

// 切换侧边栏折叠状态
const toggleCollapsed = () => {
  collapsed.value = !collapsed.value;
};

// 动态菜单项
const menuItems = ref([]);

// 图标映射表
const iconMap = {
  'dashboard': DashboardOutlined,
  'file': FileTextOutlined,
  'appstore': AppstoreOutlined,
  'tags': TagsOutlined,
  'comment': CommentOutlined,
  'user': UserOutlined,
};

// 获取菜单图标组件
const getIconComponent = (iconName) => {
  return iconMap[iconName] || AppstoreOutlined;
};

// 初始化菜单数据
const initMenuData = async () => {
  menuLoading.value = true;
  try {
    // 获取用户菜单和权限
    const { menus } = await permissionStore.getUserMenusAndPermissions();
    menuItems.value = menus;
  } catch (error) {
    console.error('获取菜单数据失败:', error);
    message.error('获取菜单数据失败，请刷新页面重试');
  } finally {
    menuLoading.value = false;
  }
};

// 组件挂载时获取菜单数据
onMounted(() => {
  initMenuData();
});

// 当前选中的菜单项
const selectedKeys = computed(() => {
  const currentPath = route.path;
  // 如果是文章编辑页面，选中文章管理菜单
  if (currentPath.includes('/admin/article/edit')) {
    return ['/admin/article'];
  }
  // 精确匹配当前路径
  const matchedItem = menuItems.value.find(item => currentPath === item.key);
  if (matchedItem) {
    return [matchedItem.key];
  }
  // 前缀匹配
  const matchedPrefix = menuItems.value.find(item => 
    currentPath.startsWith(item.key) && item.key !== '/admin'
  );
  if (matchedPrefix) {
    return [matchedPrefix.key];
  }
  // 默认选中仪表盘
  return ['/admin'];
});

// 面包屑导航
const breadcrumbItems = computed(() => {
  // 基础面包屑，始终包含首页
  const items = [{ title: '首页', path: '/' }, { title: '后台管理', path: '/admin' }];
  
  // 获取当前路由路径
  const currentPath = route.path;
  
  // 查找当前路径匹配的菜单项
  // 首先尝试查找一级菜单
  let currentMenu = menuItems.value.find(item => currentPath === item.key || currentPath.startsWith(item.key + '/'));
  
  // 如果找到了一级菜单，添加到面包屑
  if (currentMenu) {
    items.push({ title: currentMenu.title, path: currentMenu.key });
    
    // 如果当前菜单有子菜单，尝试查找匹配的子菜单
    if (currentMenu.children && currentMenu.children.length > 0) {
      const childMenu = currentMenu.children.find(child => 
        currentPath === child.key || currentPath.startsWith(child.key + '/')
      );
      
      if (childMenu) {
        items.push({ title: childMenu.title, path: childMenu.key });
      }
    }
  } else {
    // 如果没有找到一级菜单匹配，尝试在所有菜单的子菜单中查找
    for (const menu of menuItems.value) {
      if (menu.children && menu.children.length > 0) {
        const childMenu = menu.children.find(child => 
          currentPath === child.key || currentPath.startsWith(child.key + '/')
        );
        
        if (childMenu) {
          items.push({ title: menu.title, path: menu.key });
          items.push({ title: childMenu.title, path: childMenu.key });
          break;
        }
      }
    }
  }
  
  // 处理特殊路由，如文章编辑页面
  if (route.name === 'ArticleAdd') {
    // 确保文章管理已经在面包屑中
    if (!items.some(item => item.path === '/admin/article')) {
      items.push({ title: '文章管理', path: '/admin/article' });
    }
    items.push({ title: '添加文章', path: route.fullPath });
  } else if (route.name === 'ArticleEdit') {
    // 确保文章管理已经在面包屑中
    if (!items.some(item => item.path === '/admin/article')) {
      items.push({ title: '文章管理', path: '/admin/article' });
    }
    items.push({ title: '编辑文章', path: route.fullPath });
  }
  
  return items;
});

// 处理菜单点击
const handleMenuClick = (e) => {
  // 检查是否是有效的路由路径
  if (e.key && e.key.startsWith('/')) {
    router.push(e.key);
  }
};

// 退出登录
const logout = () => {
  // 使用用户存储的登出方法
  userStore.logout();
  // 重置权限存储
  permissionStore.resetState();
  // 跳转到登录页
  router.push('/login');
};

// 用户下拉菜单项
const userMenuItems = [
  {
    key: '/admin/profile',
    label: '个人中心',
    icon: () => h(UserOutlined),
  },
  {
    key: 'logout',
    label: '退出登录',
    icon: () => h(LogoutOutlined),
  },
];

// 处理用户菜单点击
const handleUserMenuClick = (e) => {
  if (e.key === 'logout') {
    logout();
  } else if (e.key.startsWith('/')) {
    router.push(e.key);
  }
};
</script>

<template>
  <a-layout class="admin-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      class="admin-sider"
    >
      <div class="logo">
        <h2 v-if="!collapsed">Trae Blog</h2>
        <h2 v-else>TB</h2>
      </div>
      <div v-if="menuLoading" class="menu-loading">
        <a-spin tip="加载菜单中..." />
      </div>
      <a-menu
        v-else
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <template v-for="item in menuItems" :key="item.key">
          <!-- 没有子菜单的情况 -->
          <a-menu-item v-if="!item.children || item.children.length === 0" :key="item.key">
            <template #icon>
              <component :is="getIconComponent(item.icon)" />
            </template>
            <span>{{ item.title }}</span>
          </a-menu-item>
          
          <!-- 有子菜单的情况 -->
          <a-sub-menu v-else :key="`submenu-${item.key}`">
            <template #icon>
              <component :is="getIconComponent(item.icon)" />
            </template>
            <template #title>{{ item.title }}</template>
            
            <a-menu-item v-for="child in item.children" :key="child.key">
              <template #icon>
                <component :is="getIconComponent(child.icon)" />
              </template>
              <span>{{ child.title }}</span>
            </a-menu-item>
          </a-sub-menu>
        </template>
      </a-menu>
    </a-layout-sider>
    
    <a-layout>
      <!-- 头部 -->
      <a-layout-header class="admin-header">
        <div class="header-left">
          <button class="trigger" @click="toggleCollapsed">
            <menu-fold-outlined v-if="collapsed" />
            <menu-unfold-outlined v-else />
          </button>
          <a-breadcrumb>
            <a-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index">
              <router-link :to="item.path">{{ item.title }}</router-link>
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 主题切换开关 -->
          <ThemeSwitch />
          
          <a-dropdown>
            <div class="user-dropdown">
              <a-avatar><template #icon><user-outlined /></template></a-avatar>
              <span class="username">管理员</span>
            </div>
            <template #overlay>
              <a-menu @click="handleUserMenuClick">
                <a-menu-item v-for="item in userMenuItems" :key="item.key">
                  <component :is="item.icon" />
                  <span>{{ item.label }}</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      
      <!-- 内容区 -->
      <a-layout-content class="admin-content">
        <div class="content-wrapper">
          <router-view></router-view>
        </div>
      </a-layout-content>
      
      <!-- 页脚 -->
      <a-layout-footer class="admin-footer">
        Trae Blog Admin ©{{ new Date().getFullYear() }} Created by Trae
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background-color: var(--layout-body-bg);
}

.admin-sider {
  box-shadow: var(--shadow-1-right);
  z-index: 10;
  background-color: var(--layout-sider-bg) !important;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--layout-sider-bg);
  color: var(--menu-dark-highlight-color);
}

.logo h2 {
  margin: 0;
  color: var(--menu-dark-highlight-color);
  font-size: 20px;
  font-weight: 600;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.menu-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
  color: var(--menu-dark-highlight-color);
}

.admin-header {
  background-color: var(--component-bg);
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-1-down);
  z-index: 9;
  color: var(--text-color);
}

.header-left {
  display: flex;
  align-items: center;
  overflow-x: auto;
  white-space: nowrap;
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

.header-left::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.header-right {
  display: flex;
  align-items: center;
}

.trigger {
  font-size: 18px;
  padding: 0 24px;
  cursor: pointer;
  transition: color 0.3s;
  background: transparent;
  border: none;
  height: 64px;
  display: flex;
  align-items: center;
  color: var(--text-color);
}

.trigger:hover {
  color: var(--primary-color);
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 12px;
}

.username {
  margin-left: 8px;
  color: var(--text-color);
}

.admin-content {
  margin: 24px 16px;
  padding: 24px;
  background-color: var(--component-bg);
  min-height: 280px;
  border-radius: 2px;
  color: var(--text-color);
}

.content-wrapper {
  padding: 0;
  background-color: var(--component-bg);
  min-height: 100%;
}

.admin-footer {
  text-align: center;
  color: var(--text-color-secondary);
  background-color: var(--component-bg);
}

/* 响应式布局 */
@media (max-width: 992px) {
  .admin-content {
    margin: 16px 8px;
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .admin-header {
    padding: 0 8px;
  }
  
  .trigger {
    padding: 0 12px;
  }
  
  .username {
    display: none;
  }
  
  .admin-content {
    margin: 8px 4px;
    padding: 12px;
  }
}

@media (max-width: 576px) {
  .admin-content {
    margin: 4px 2px;
    padding: 8px;
  }
  
  .admin-footer {
    padding: 8px;
  }
}
</style>