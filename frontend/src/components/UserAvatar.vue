<script setup>
import { computed } from 'vue';
import { Avatar } from 'ant-design-vue';

// 组件属性定义
const props = defineProps({
  // 用户头像URL
  src: {
    type: String,
    default: ''
  },
  // 用户名，用于生成默认头像
  username: {
    type: String,
    default: ''
  },
  // 头像大小
  size: {
    type: [Number, String],
    default: 'default'
  },
  // 形状：circle 或 square
  shape: {
    type: String,
    default: 'circle'
  }
});

// 根据用户名生成背景颜色
const getColorFromUsername = (username) => {
  if (!username) return '#1890ff'; // 默认蓝色
  
  // 简单的哈希算法生成颜色
  let hash = 0;
  for (let i = 0; i < username.length; i++) {
    hash = username.charCodeAt(i) + ((hash << 5) - hash);
  }
  
  // 转换为HSL颜色，保持较高饱和度和适中亮度
  const h = Math.abs(hash % 360);
  return `hsl(${h}, 70%, 60%)`;
};

// 获取用户名的首字母或第一个字
const getInitials = (username) => {
  if (!username) return '?';
  
  // 对于中文名，取第一个字
  if (/[\u4e00-\u9fa5]/.test(username)) {
    return username.charAt(0);
  }
  
  // 对于英文名，取首字母大写
  return username.charAt(0).toUpperCase();
};

// 计算头像样式
const avatarStyle = computed(() => {
  if (props.src) return {}; // 有图片时不需要背景色
  
  return {
    backgroundColor: getColorFromUsername(props.username),
    color: '#fff',
    fontSize: typeof props.size === 'number' ? `${props.size / 2}px` : '14px',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
  };
});

// 是否显示默认头像
const showDefault = computed(() => !props.src);
</script>

<template>
  <Avatar
    :src="src"
    :size="size"
    :shape="shape"
    :style="avatarStyle"
  >
    <template #icon v-if="showDefault">
      {{ getInitials(username) }}
    </template>
  </Avatar>
</template>