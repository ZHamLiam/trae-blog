<script setup>
import { ref, computed } from 'vue';
import { BulbOutlined, BulbFilled } from '@ant-design/icons-vue';
import { Switch, Dropdown, Menu } from 'ant-design-vue';
import { useThemeStore, THEME_LIGHT, THEME_DARK } from '@/store/theme';

const themeStore = useThemeStore();

// 当前主题模式
const themeMode = computed(() => themeStore.themeMode);

// 当前实际主题
const currentTheme = computed(() => themeStore.currentTheme);

// 是否是暗色模式
const isDarkMode = computed(() => currentTheme.value === THEME_DARK);

// 主题选项
const themeOptions = [
  { key: THEME_LIGHT, label: '浅色模式' },
  { key: THEME_DARK, label: '深色模式' },
  { key: 'system', label: '跟随系统' },
];

// 切换主题
const toggleTheme = () => {
  themeStore.toggleTheme();
};

// 选择特定主题
const selectTheme = ({ key }) => {
  themeStore.setThemeMode(key);
};
</script>

<template>
  <div class="theme-switch">
    <!-- 简单开关模式 -->
    <div class="switch-mode">
      <BulbOutlined v-if="isDarkMode" />
      <BulbFilled v-else />
      <a-switch
        :checked="isDarkMode"
        @change="toggleTheme"
        class="theme-switch-toggle"
      />
    </div>
    
    <!-- 下拉选择模式 -->
    <a-dropdown placement="bottomRight">
      <div class="dropdown-trigger">
        <BulbOutlined v-if="isDarkMode" />
        <BulbFilled v-else />
        <span class="theme-text">{{ 
          themeMode === 'system' ? '跟随系统' : 
          (isDarkMode ? '深色模式' : '浅色模式') 
        }}</span>
      </div>
      <template #overlay>
        <a-menu @click="selectTheme" :selectedKeys="[themeMode]">
          <a-menu-item v-for="option in themeOptions" :key="option.key">
            {{ option.label }}
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<style scoped>
.theme-switch {
  display: flex;
  align-items: center;
  margin-right: 16px;
}

.switch-mode {
  display: flex;
  align-items: center;
  margin-right: 16px;
}

.theme-switch-toggle {
  margin-left: 8px;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.dropdown-trigger:hover {
  background-color: var(--hover-bg-color);
}

.theme-text {
  margin-left: 8px;
}

@media (max-width: 768px) {
  .theme-text {
    display: none;
  }
  
  .switch-mode {
    display: none;
  }
}
</style>