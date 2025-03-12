<script setup>
import { ref, computed, h } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  Layout,
  Menu,
  Breadcrumb,
  Dropdown,
  Avatar,
  Button,
  Divider
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

const router = useRouter();
const route = useRoute();

// 侧边栏折叠状态
const collapsed = ref(false);

// 切换侧边栏折叠状态
const toggleCollapsed = () => {
  collapsed.value = !collapsed.value;
};

// 菜单项配置
const menuItems = [
  {
    key: '/admin',
    title: '仪表盘',
    icon: DashboardOutlined,
  },
  {
    key: '/admin/article',
    title: '文章管理',
    icon: FileTextOutlined,
  },
  {
    key: '/admin/category',
    title: '分类管理',
    icon: AppstoreOutlined,
  },
  {
    key: '/admin/tag',
    title: '标签管理',
    icon: TagsOutlined,
  },
  {
    key: '/admin/comment',
    title: '评论管理',
    icon: CommentOutlined,
  },
  {
    key: '/admin/user',
    title: '用户管理',
    icon: UserOutlined,
  },
];

// 当前选中的菜单项
const selectedKeys = computed(() => {
  const currentPath = route.path;
  // 如果是文章编辑页面，选中文章管理菜单
  if (currentPath.includes('/admin/article/edit')) {
    return ['/admin/article'];
  }
  // 精确匹配当前路径
  const matchedItem = menuItems.find(item => currentPath === item.key);
  if (matchedItem) {
    return [matchedItem.key];
  }
  // 前缀匹配
  const matchedPrefix = menuItems.find(item => 
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
  const items = [{ title: '首页', path: '/' }];
  
  // 添加当前页面的面包屑
  const currentMenu = menuItems.find(item => selectedKeys.value.includes(item.key));
  if (currentMenu) {
    items.push({ title: currentMenu.title, path: currentMenu.key });
  }
  
  // 如果是文章编辑页面，添加额外的面包屑
  if (route.name === 'ArticleAdd') {
    items.push({ title: '添加文章', path: route.fullPath });
  } else if (route.name === 'ArticleEdit') {
    items.push({ title: '编辑文章', path: route.fullPath });
  }
  
  return items;
});

// 处理菜单点击
const handleMenuClick = (e) => {
  router.push(e.key);
};

// 退出登录
const logout = () => {
  // 清除token
  localStorage.removeItem('token');
  // 跳转到登录页
  router.push('/login');
};

// 用户下拉菜单项
const userMenuItems = [
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
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <a-menu-item v-for="item in menuItems" :key="item.key">
          <template #icon>
            <component :is="item.icon" />
          </template>
          <span>{{ item.title }}</span>
        </a-menu-item>
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
}

.admin-sider {
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
}

.logo {
  height: 64px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #001529;
}

.logo h2 {
  color: white;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.admin-header {
  background: #fff;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
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
}

.trigger:hover {
  color: #1890ff;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 12px;
}

.username {
  margin-left: 8px;
}

.admin-content {
  margin: 24px 16px;
  padding: 24px;
  background: #fff;
  min-height: 280px;
  border-radius: 2px;
}

.content-wrapper {
  padding: 0;
  background: #fff;
  min-height: 100%;
}

.admin-footer {
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
}
</style>