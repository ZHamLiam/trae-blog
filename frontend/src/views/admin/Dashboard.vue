<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { Card, Row, Col, Statistic, List, Table, Tag, message, Modal } from 'ant-design-vue';
import {
  FileTextOutlined,
  EyeOutlined,
  CommentOutlined,
  UserOutlined,
  FireOutlined
} from '@ant-design/icons-vue';
import articleApi from '@/api/article';
import commentApi from '@/api/comment';
import userApi from '@/api/user';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';

// 获取当前实例
const { proxy } = getCurrentInstance();

// 统计数据
const statistics = ref({
  articleCount: 0,
  viewCount: 0,
  commentCount: 0,
  userCount: 0
});

// 最近文章列表
const recentArticles = ref([]);

// 热门文章列表
const hotArticles = ref([]);

// 最新评论列表
const recentComments = ref([]);

// 加载状态
const loading = ref(true);

// 文章预览相关
const previewVisible = ref(false);
const previewTitle = ref('');
const previewContent = ref('');
const previewLoading = ref(false);

// 热门文章表格列定义
const hotArticleColumns = [
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title',
    ellipsis: true
  },
  {
    title: '分类',
    dataIndex: 'categoryName',
    key: 'categoryName'
  },
  {
    title: '浏览量',
    dataIndex: 'viewCount',
    key: 'viewCount',
    sorter: true
  },
  {
    title: '发布时间',
    dataIndex: 'createTime',
    key: 'createTime',
    customRender: ({ text }) => {
      return text ? proxy.$formatDate(text) : ''; // 使用全局日期格式化方法
    }
  }
];

// 最新评论表格列定义
const commentColumns = [
  {
    title: '内容',
    dataIndex: 'content',
    key: 'content',
    ellipsis: true
  },
  {
    title: '文章',
    dataIndex: 'articleTitle',
    key: 'articleTitle',
    ellipsis: true
  },
  {
    title: '用户',
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: '时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: '180px',
    customRender: ({ text }) => {
      return text ? text : ''; // 简单处理，避免null值错误
    }
  }
];

onMounted(() => {
  // 模拟获取数据
  fetchDashboardData();
});

// 预览文章内容
// 预览文章内容
const previewArticle = async (articleId) => {
  previewLoading.value = true;
  previewVisible.value = true;
  
  try {
    console.log('正在获取文章ID:', articleId);
    const response = await articleApi.getArticleById(articleId);
    console.log('获取文章详情响应:', response);
    
    if (response && response.code === 200 && response.data) {
      previewTitle.value = response.data.title;
      previewContent.value = response.data.content;
    } else {
      console.error('文章数据格式不正确:', response);
      previewTitle.value = '内容加载失败';
      previewContent.value = '无法获取文章内容';
    }
  } catch (error) {
    console.error('获取文章详情失败:', error);
    previewTitle.value = '内容加载失败';
    previewContent.value = '无法获取文章内容';
  } finally {
    previewLoading.value = false;
  }
};

// 关闭预览
const closePreview = () => {
  previewVisible.value = false;
  previewTitle.value = '';
  previewContent.value = '';
};

// 获取仪表盘数据
const fetchDashboardData = async () => {
  loading.value = true;
  
  try {
    // 使用仪表盘API获取统计数据
    const dashboardApi = await import('@/api/dashboard');
    const statsData = await dashboardApi.default.getDashboardStats();
    
    // 更新统计数据
    if (statsData && statsData.data) {
      statistics.value.articleCount = statsData.data.articleCount || 0;
      statistics.value.viewCount = statsData.data.viewCount || 0;
      statistics.value.commentCount = statsData.data.commentCount || 0;
      statistics.value.userCount = statsData.data.userCount || 0;
      
      // 直接从DashboardDTO中获取热门文章、最新评论等数据
      if (Array.isArray(statsData.data.hotArticles)) {
        hotArticles.value = statsData.data.hotArticles;
      }
      
      if (Array.isArray(statsData.data.recentComments)) {
        recentComments.value = statsData.data.recentComments;
      }
      
      if (Array.isArray(statsData.data.recentArticles)) {
        recentArticles.value = statsData.data.recentArticles;
      } else {
        // 如果DashboardDTO中没有提供最近文章，单独获取
        const recentArticlesData = await articleApi.getRecentArticles();
        recentArticles.value = Array.isArray(recentArticlesData.data) ? recentArticlesData.data : [];
      }
    } else {
      // 如果新API不可用，则回退到单独获取数据的方式
      // 获取最近文章数据
      const recentArticlesData = await articleApi.getRecentArticles();
      recentArticles.value = Array.isArray(recentArticlesData.data) ? recentArticlesData.data : [];
      
      // 获取热门文章数据
      const hotArticlesData = await articleApi.getHotArticles();
      hotArticles.value = Array.isArray(hotArticlesData.data) ? hotArticlesData.data : [];
      
      // 获取最新评论数据
      const recentCommentsData = await commentApi.getRecentComments();
      recentComments.value = Array.isArray(recentCommentsData.data) ? recentCommentsData.data : [];
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
    message.error('获取仪表盘数据失败，请稍后重试');
    
    // 设置默认数据，避免页面显示异常
    statistics.value = {
      articleCount: 0,
      viewCount: 0,
      commentCount: 0,
      userCount: 0
    };
    recentArticles.value = [];
    hotArticles.value = [];
    recentComments.value = [];
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>
    
    <!-- 统计卡片 -->
    <a-row :gutter="16" class="stat-cards">
      <a-col :span="6">
        <a-card>
          <a-statistic 
            title="文章总数" 
            :value="statistics.articleCount"
            :loading="loading"
          >
            <template #prefix>
              <file-text-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic 
            title="总浏览量" 
            :value="statistics.viewCount"
            :loading="loading"
          >
            <template #prefix>
              <eye-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic 
            title="评论总数" 
            :value="statistics.commentCount"
            :loading="loading"
          >
            <template #prefix>
              <comment-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic 
            title="用户总数" 
            :value="statistics.userCount"
            :loading="loading"
          >
            <template #prefix>
              <user-outlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 最近文章和热门文章 -->
    <a-row :gutter="16" class="data-cards">
      <a-col :span="12">
        <a-card title="最近发布的文章" :loading="loading">
          <a-list
            :data-source="recentArticles"
            :pagination="false"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <a @click="previewArticle(item.id)" class="article-link">{{ item.title }}</a>
                  </template>
                  <template #description>
                    <span>{{ item.createTime ? $formatDate(item.createTime) : '' }} | {{ item.categoryName || '' }}</span>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="热门文章" :loading="loading">
          <a-table
            :columns="hotArticleColumns"
            :data-source="hotArticles"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, text, record }">
              <template v-if="column.key === 'viewCount'">
                <span>
                  <fire-outlined style="color: #ff4d4f; margin-right: 8px" />
                  {{ text }}
                </span>
              </template>
              <template v-else-if="column.key === 'createTime'">
                <span>{{ text ? proxy.$formatDate(text) : '' }}</span>
              </template>
              <template v-else-if="column.key === 'title'">
                <a @click="previewArticle(record.id)" class="article-link">{{ text }}</a>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 最新评论 -->
    <a-row class="data-cards">
      <a-col :span="24">
        <a-card title="最新评论" :loading="loading">
          <a-table
            :columns="commentColumns"
            :data-source="recentComments"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, text, record }">
              <template v-if="column.key === 'content'">
                <span>{{ text.length > 30 ? text.substring(0, 30) + '...' : text }}</span>
              </template>
              <template v-else-if="column.key === 'username'">
                <span>{{ record.nickname || record.username }}</span>
              </template>
              <template v-else-if="column.key === 'articleTitle'">
                <span>{{ text || '未知文章' }}</span>
              </template>
              <template v-else-if="column.key === 'createTime'">
                <span>{{ text ? proxy.$formatDate(text) : '' }}</span>
              </template>
              <template v-else-if="column.key === 'title'">
                <a @click="previewArticle(record.id)" class="article-link">{{ text }}</a>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
  
  <!-- 文章预览弹窗 -->
  <a-modal
    v-model:visible="previewVisible"
    :title="previewTitle"
    :width="800"
    @cancel="closePreview"
    :footer="null"
  >
    <a-spin :spinning="previewLoading">
      <md-preview :modelValue="previewContent" />
    </a-spin>
  </a-modal>
</template>

<style scoped>
.dashboard {
  padding: 0 12px;
}

.page-title {
  margin-bottom: 24px;
  font-size: 20px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
}

.stat-cards {
  margin-bottom: 24px;
}

.data-cards {
  margin-bottom: 24px;
}

.ant-statistic-title {
  font-size: 14px;
}

.ant-statistic-content {
  font-size: 24px;
}

.article-link {
  cursor: pointer;
  color: #1890ff;
}

.article-link:hover {
  text-decoration: underline;
}
</style>