<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import * as echarts from 'echarts';

// 接收props
const props = defineProps({
  statisticsData: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
});

// 图表DOM引用
const articleTrendChart = ref(null);
const userTrendChart = ref(null);
const commentTrendChart = ref(null);
const categoryChart = ref(null);
const viewTrendChart = ref(null);

// 图表实例
let articleChartInstance = null;
let userChartInstance = null;
let commentChartInstance = null;
let categoryChartInstance = null;
let viewChartInstance = null;

// 监听数据变化，更新图表
watch(() => props.statisticsData, (newVal) => {
  if (newVal) {
    renderCharts();
  }
}, { deep: true });

// 监听loading状态
watch(() => props.loading, (newVal) => {
  if (!newVal) {
    // 加载完成后重新渲染图表
    setTimeout(() => {
      renderCharts();
    }, 300);
  }
});

// 组件卸载前清理
const handleUnmount = () => {
  // 销毁图表实例
  if (articleChartInstance) articleChartInstance.dispose();
  if (userChartInstance) userChartInstance.dispose();
  if (commentChartInstance) commentChartInstance.dispose();
  if (categoryChartInstance) categoryChartInstance.dispose();
  if (viewChartInstance) viewChartInstance.dispose();
  
  // 移除事件监听
  window.removeEventListener('resize', handleResize);
};

// 初始化图表
onMounted(() => {
  // 初始化所有图表
  initCharts();
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', handleResize);
});

// 组件卸载时清理资源
onUnmounted(handleUnmount);

// 处理窗口大小变化
const handleResize = () => {
  if (articleChartInstance) articleChartInstance.resize();
  if (userChartInstance) userChartInstance.resize();
  if (commentChartInstance) commentChartInstance.resize();
  if (categoryChartInstance) categoryChartInstance.resize();
  if (viewChartInstance) viewChartInstance.resize();
};

// 初始化所有图表
const initCharts = () => {
  // 初始化文章趋势图表
  articleChartInstance = echarts.init(articleTrendChart.value);
  
  // 初始化用户趋势图表
  userChartInstance = echarts.init(userTrendChart.value);
  
  // 初始化评论趋势图表
  commentChartInstance = echarts.init(commentTrendChart.value);
  
  // 初始化分类统计图表
  categoryChartInstance = echarts.init(categoryChart.value);
  
  // 初始化浏览量趋势图表
  viewChartInstance = echarts.init(viewTrendChart.value);
  
  // 渲染图表
  renderCharts();
};

// 渲染所有图表
const renderCharts = () => {
  if (!props.statisticsData) return;
  
  // 渲染文章趋势图表
  renderArticleTrendChart();
  
  // 渲染用户趋势图表
  renderUserTrendChart();
  
  // 渲染评论趋势图表
  renderCommentTrendChart();
  
  // 渲染分类统计图表
  renderCategoryChart();
  
  // 渲染浏览量趋势图表
  renderViewTrendChart();
};

// 渲染文章趋势图表
const renderArticleTrendChart = () => {
  if (!articleChartInstance || !props.statisticsData.articleTrend) return;
  
  const { articleTrend } = props.statisticsData;
  const months = Object.keys(articleTrend);
  const data = Object.values(articleTrend);
  
  const option = {
    title: {
      text: '文章发布趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '文章数',
        type: 'bar',
        barWidth: '60%',
        data: data,
        itemStyle: {
          color: '#1890ff'
        }
      }
    ]
  };
  
  articleChartInstance.setOption(option);
};

// 渲染用户趋势图表
const renderUserTrendChart = () => {
  if (!userChartInstance || !props.statisticsData.userTrend) return;
  
  const { userTrend } = props.statisticsData;
  const months = Object.keys(userTrend);
  const data = Object.values(userTrend);
  
  const option = {
    title: {
      text: '用户注册趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '用户数',
        type: 'line',
        data: data,
        smooth: true,
        itemStyle: {
          color: '#52c41a'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(82, 196, 26, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(82, 196, 26, 0.1)'
              }
            ]
          }
        }
      }
    ]
  };
  
  userChartInstance.setOption(option);
};

// 渲染评论趋势图表
const renderCommentTrendChart = () => {
  if (!commentChartInstance || !props.statisticsData.commentTrend) return;
  
  const { commentTrend } = props.statisticsData;
  const months = Object.keys(commentTrend);
  const data = Object.values(commentTrend);
  
  const option = {
    title: {
      text: '评论趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '评论数',
        type: 'line',
        data: data,
        smooth: true,
        itemStyle: {
          color: '#fa8c16'
        }
      }
    ]
  };
  
  commentChartInstance.setOption(option);
};

// 渲染分类统计图表
const renderCategoryChart = () => {
  if (!categoryChartInstance || !props.statisticsData.categoryStats) return;
  
  const { categoryStats } = props.statisticsData;
  const categories = Object.keys(categoryStats);
  const data = categories.map(category => ({
    name: category,
    value: categoryStats[category]
  }));
  
  const option = {
    title: {
      text: '文章分类统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: categories
    },
    series: [
      {
        name: '分类统计',
        type: 'pie',
        radius: '55%',
        center: ['50%', '60%'],
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };
  
  categoryChartInstance.setOption(option);
};

// 渲染浏览量趋势图表
const renderViewTrendChart = () => {
  if (!viewChartInstance || !props.statisticsData.viewTrend) return;
  
  const { viewTrend } = props.statisticsData;
  const months = Object.keys(viewTrend);
  const data = Object.values(viewTrend);
  
  const option = {
    title: {
      text: '浏览量趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '浏览量',
        type: 'line',
        data: data,
        smooth: true,
        itemStyle: {
          color: '#722ed1'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(114, 46, 209, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(114, 46, 209, 0.1)'
              }
            ]
          }
        }
      }
    ]
  };
  
  viewChartInstance.setOption(option);
};
</script>

<template>
  <div class="dashboard-charts">
    <div class="chart-row">
      <div ref="articleTrendChart" class="chart-container"></div>
      <div ref="userTrendChart" class="chart-container"></div>
    </div>
    <div class="chart-row">
      <div ref="commentTrendChart" class="chart-container"></div>
      <div ref="viewTrendChart" class="chart-container"></div>
    </div>
    <div class="chart-row">
      <div ref="categoryChart" class="chart-container"></div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-charts {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-row {
  display: flex;
  gap: 20px;
  width: 100%;
}

.chart-container {
  height: 300px;
  flex: 1;
  min-width: 0;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}
</style>