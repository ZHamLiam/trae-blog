import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

// 主题类型常量
export const THEME_LIGHT = 'light';
export const THEME_DARK = 'dark';

// 主题存储键名
const THEME_STORAGE_KEY = 'trae-blog-theme';

// 获取系统主题偏好
const getSystemTheme = () => {
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
    ? THEME_DARK
    : THEME_LIGHT;
};

// 从本地存储获取主题设置，如果没有则跟随系统
const getStoredTheme = () => {
  const storedTheme = localStorage.getItem(THEME_STORAGE_KEY);
  if (storedTheme === THEME_LIGHT || storedTheme === THEME_DARK) {
    return storedTheme;
  }
  return 'system';
};

// 获取实际应用的主题（如果设置为跟随系统，则返回系统主题）
const getActualTheme = (themeMode) => {
  return themeMode === 'system' ? getSystemTheme() : themeMode;
};

// 定义主题存储
export const useThemeStore = defineStore('theme', () => {
  // 主题模式：light, dark, system
  const themeMode = ref(getStoredTheme());
  
  // 当前实际应用的主题
  const currentTheme = ref(getActualTheme(themeMode.value));
  
  // 监听系统主题变化
  if (window.matchMedia) {
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (themeMode.value === 'system') {
        currentTheme.value = e.matches ? THEME_DARK : THEME_LIGHT;
        applyTheme(currentTheme.value);
      }
    });
  }
  
  // 应用主题到DOM
  const applyTheme = (theme) => {
    document.documentElement.setAttribute('data-theme', theme);
    // 为了兼容Ant Design Vue的主题
    document.body.setAttribute('data-theme', theme);
  };
  
  // 设置主题模式
  const setThemeMode = (mode) => {
    themeMode.value = mode;
    localStorage.setItem(THEME_STORAGE_KEY, mode);
    
    // 更新当前实际主题
    currentTheme.value = getActualTheme(mode);
    applyTheme(currentTheme.value);
  };
  
  // 切换主题（在亮色和暗色之间切换）
  const toggleTheme = () => {
    const newMode = themeMode.value === 'system'
      ? (getSystemTheme() === THEME_LIGHT ? THEME_DARK : THEME_LIGHT)
      : (themeMode.value === THEME_LIGHT ? THEME_DARK : THEME_LIGHT);
    
    setThemeMode(newMode);
  };
  
  // 初始化主题
  const initTheme = () => {
    applyTheme(currentTheme.value);
  };
  
  return {
    themeMode,
    currentTheme,
    setThemeMode,
    toggleTheme,
    initTheme
  };
});