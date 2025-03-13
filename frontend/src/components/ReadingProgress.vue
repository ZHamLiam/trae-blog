<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

// 阅读进度
const progress = ref(0);

// 计算阅读进度
const calculateProgress = () => {
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight - windowHeight;
  const scrollTop = window.scrollY;
  
  // 计算百分比
  progress.value = Math.min(Math.round((scrollTop / documentHeight) * 100), 100);
};

onMounted(() => {
  window.addEventListener('scroll', calculateProgress);
  // 初始计算一次
  calculateProgress();
});

onUnmounted(() => {
  window.removeEventListener('scroll', calculateProgress);
});
</script>

<template>
  <div class="reading-progress-container">
    <div class="reading-progress-bar" :style="{ width: `${progress}%` }"></div>
    <div class="reading-progress-text" v-if="progress > 0">{{ progress }}%</div>
  </div>
</template>

<style scoped>
.reading-progress-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background-color: rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.reading-progress-bar {
  height: 100%;
  background-color: #1890ff;
  transition: width 0.2s ease;
}

.reading-progress-text {
  position: fixed;
  top: 10px;
  right: 20px;
  background-color: #1890ff;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

@media (max-width: 768px) {
  .reading-progress-text {
    top: 5px;
    right: 10px;
    font-size: 10px;
    padding: 1px 6px;
  }
}
</style>