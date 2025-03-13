<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { UpOutlined } from '@ant-design/icons-vue';

// 控制按钮显示/隐藏的阈值
const visibilityThreshold = 300;
// 按钮可见性
const visible = ref(false);

// 监听滚动事件
const handleScroll = () => {
  visible.value = window.scrollY > visibilityThreshold;
};

// 返回顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<template>
  <div class="back-to-top" v-show="visible" @click="scrollToTop">
    <UpOutlined />
  </div>
</template>

<style scoped>
.back-to-top {
  position: fixed;
  right: 40px;
  bottom: 40px;
  width: 40px;
  height: 40px;
  background-color: #1890ff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
  z-index: 999;
}

.back-to-top:hover {
  background-color: #40a9ff;
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .back-to-top {
    right: 20px;
    bottom: 20px;
    width: 36px;
    height: 36px;
  }
}
</style>