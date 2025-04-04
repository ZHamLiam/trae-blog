<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Card, Row, Col, Pagination, Tag, Divider, Input, message } from 'ant-design-vue';
import BackToTop from '@/components/BackToTop.vue';
import articleApi from '@/api/article';
import categoryApi from '@/api/category';
import tagApi from '@/api/tag';

const router = useRouter();

// 搜索关键词
const searchKeyword = ref('');

// 文章列表数据
const articles = ref([]);
// 热门文章
const hotArticles = ref([]);
// 分类列表
const categories = ref([]);
// 标签列表
const tags = ref([]);

// 分页参数
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
});

// 加载状态
const loading = ref(false);

// 从API获取数据
const fetchData = async () => {
  loading.value = true;
  try {
    // 获取文章列表
    const articlesRes = await articleApi.getArticleList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      status: 1 // 只获取已发布的文章
    });
    
    if (articlesRes.code === 200) {
      articles.value = articlesRes.data.records || [];
      pagination.value.total = articlesRes.data.total || 0;
    } else {
      message.error(articlesRes.message || '获取文章列表失败');
      useMockData();
    }
    
    // 获取热门文章
    const hotArticlesRes = await articleApi.getHotArticles();
    if (hotArticlesRes.code === 200) {
      hotArticles.value = hotArticlesRes.data || [];
    } else {
      message.error(hotArticlesRes.message || '获取热门文章失败');
    }
    
    // 获取分类列表
    const categoriesRes = await categoryApi.getCategoryList();
    if (categoriesRes.code === 200) {
      categories.value = categoriesRes.data.records || [];
    } else {
      message.error(categoriesRes.message || '获取分类列表失败');
    }
    
    // 获取标签列表
    const tagsRes = await tagApi.getTagList();
    if (tagsRes.code === 200) {
      tags.value = tagsRes.data.records || [];
    } else {
      message.error(tagsRes.message || '获取标签列表失败');
    }
  } catch (error) {
    console.error('获取数据失败:', error);
    message.error('获取数据失败，请稍后再试');
    // 如果API请求失败，使用模拟数据
    useMockData();
  } finally {
    loading.value = false;
  }
};

// 使用模拟数据（当API请求失败时使用）
const useMockData = () => {
  // 模拟文章列表数据
  articles.value = [
    {
      id: 1,
      title: '如何使用Vue3和Vite构建现代化前端应用',
      summary: 'Vue3和Vite是现代前端开发的强大组合，本文将介绍如何使用它们构建高性能的Web应用...',
      coverImage: 'https://picsum.photos/800/400?random=1',
      author: '张三',
      categoryName: '前端开发',
      viewCount: 1024,
      commentCount: 32,
      createTime: '2023-05-15 10:30:00',
      tags: ['Vue3', 'Vite', '前端']
    },
    {
      id: 2,
      title: 'Spring Boot实战：构建RESTful API',
      summary: 'Spring Boot是Java生态中最流行的框架之一，本文将详细介绍如何使用Spring Boot构建RESTful API...',
      coverImage: 'https://picsum.photos/800/400?random=2',
      author: '李四',
      categoryName: '后端开发',
      viewCount: 856,
      commentCount: 18,
      createTime: '2023-05-12 14:20:00',
      tags: ['Spring Boot', 'Java', 'RESTful']
    },
    {
      id: 3,
      title: '深入理解MySQL索引优化',
      summary: '索引是提高数据库查询性能的关键，本文将深入分析MySQL索引的工作原理和优化策略...',
      coverImage: 'https://picsum.photos/800/400?random=3',
      author: '王五',
      categoryName: '数据库',
      viewCount: 723,
      commentCount: 15,
      createTime: '2023-05-10 09:15:00',
      tags: ['MySQL', '数据库', '性能优化']
    }
  ];
  
  // 模拟热门文章数据
  hotArticles.value = [
    { id: 4, title: 'Docker容器化部署实践指南', viewCount: 1560 },
    { id: 5, title: 'Redis缓存设计与优化', viewCount: 1320 },
    { id: 6, title: '前端性能优化最佳实践', viewCount: 1280 }
  ];
  
  // 模拟分类数据
  categories.value = [
    { id: 1, name: '前端开发', count: 28 },
    { id: 2, name: '后端开发', count: 35 },
    { id: 3, name: '数据库', count: 16 },
    { id: 4, name: '运维部署', count: 12 },
    { id: 5, name: '编程语言', count: 23 }
  ];
  
  // 模拟标签数据
  tags.value = [
    { id: 1, name: 'Java' },
    { id: 2, name: 'Spring Boot' },
    { id: 3, name: 'Vue3' },
    { id: 4, name: 'MySQL' },
    { id: 5, name: 'Redis' },
    { id: 6, name: 'Docker' },
    { id: 7, name: 'JavaScript' },
    { id: 8, name: 'TypeScript' }
  ];
  
  // 设置分页总数
  pagination.value.total = 28;
};

onMounted(() => {
  fetchData();
});

// 跳转到文章详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`);
};

// 跳转到分类页
const goToCategory = (id) => {
  router.push(`/category/${id}`);
};

// 跳转到标签页
const goToTag = (id) => {
  router.push(`/tag/${id}`);
};

// 处理搜索
const handleSearch = (value) => {
  if (!value.trim()) {
    message.info('请输入搜索关键词');
    return;
  }
  router.push({
    path: '/search',
    query: { keyword: value }
  });
};

// 处理分页变化
const handlePageChange = (page) => {
  pagination.value.current = page;
  fetchData();
};
</script>

<template>
  <div class="home-container">
    <div class="banner">
      <div class="banner-content">
        <h1>Trae Blog</h1>
        <p>分享技术，探索未知</p>
      </div>
    </div>
    
    <div class="content-wrapper">
      <Row :gutter="24">
        <!-- 文章列表 -->
        <Col :span="16">
          <div class="article-list">
            <h2 class="section-title">最新文章</h2>
            
            <div v-if="loading" class="loading-container">
              <div class="loading-spinner"></div>
              <p>加载中...</p>
            </div>
            
            <div v-else-if="articles.length === 0" class="empty-container">
              <p>暂无文章</p>
            </div>
            
            <Card v-else v-for="article in articles" :key="article.id" class="article-card" :class="{'top-article': article.isTop === 1}" hoverable @click="goToArticle(article.id)">
              <div class="article-cover" v-if="article.coverImage">
                <img :src="article.coverImage" :alt="article.title" />
              </div>
              <div class="article-content">
                <h3 class="article-title">
                  <span v-if="article.isTop === 1" class="top-tag">置顶</span>
                  {{ article.title }}
                </h3>
                <div class="article-meta">
                  <span>{{ article.author }}</span>
                  <span>{{ $formatDate(article.createTime) }}</span>
                  <span>分类: {{ article.categoryName }}</span>
                  <span>浏览: {{ article.viewCount }}</span>
                  <span>评论: {{ article.commentCount }}</span>
                </div>
                <p class="article-summary">{{ article.summary }}</p>
                <div class="article-tags">
                  <Tag v-for="tag in article.tags" :key="tag" color="blue">{{ tag }}</Tag>
                </div>
              </div>
            </Card>
            
            <div class="pagination">
              <Pagination 
                v-model:current="pagination.current" 
                :total="pagination.total" 
                :pageSize="pagination.pageSize"
                @change="handlePageChange"
                show-quick-jumper
              />
            </div>
          </div>
        </Col>
        
        <!-- 侧边栏 -->
        <Col :span="8">
          <div class="sidebar">
            <!-- 搜索框 -->
            <Card class="sidebar-card">
              <template #title>搜索文章</template>
              <div class="search-box">
                <Input.Search
                  v-model:value="searchKeyword"
                  placeholder="输入关键词搜索文章..."
                  enter-button
                  @search="handleSearch"
                />
              </div>
            </Card>
            
            <!-- 热门文章 -->
            <Card class="sidebar-card">
              <template #title>热门文章</template>
              <div class="hot-articles">
                <div v-if="hotArticles.length === 0" class="empty-container">
                  <p>暂无热门文章</p>
                </div>
                <div v-else v-for="(article, index) in hotArticles" :key="article.id" class="hot-article-item" @click="goToArticle(article.id)">
                  <span class="hot-article-index" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</span>
                  <span class="hot-article-title">{{ article.title }}</span>
                  <span class="hot-article-views">{{ article.viewCount }}</span>
                </div>
              </div>
            </Card>
            
            <!-- 分类列表 -->
            <Card class="sidebar-card">
              <template #title>文章分类</template>
              <div class="category-list">
                <div v-if="categories.length === 0" class="empty-container">
                  <p>暂无分类</p>
                </div>
                <div v-else v-for="category in categories" :key="category.id" class="category-item" @click="goToCategory(category.id)">
                  <span class="category-name">{{ category.name }}</span>
                  <span class="category-count">{{ category.count }}</span>
                </div>
              </div>
            </Card>
            
            <!-- 标签云 -->
            <Card class="sidebar-card">
              <template #title>标签云</template>
              <div class="tag-cloud">
                <div v-if="tags.length === 0" class="empty-container">
                  <p>暂无标签</p>
                </div>
                <Tag 
                  v-else
                  v-for="tag in tags" 
                  :key="tag.id" 
                  :color="['blue', 'green', 'orange', 'red', 'purple', 'cyan'][Math.floor(Math.random() * 6)]"
                  class="tag-item"
                  @click="goToTag(tag.id)"
                >
                  {{ tag.name }}
                </Tag>
              </div>
            </Card>
          </div>
        </Col>
      </Row>
    </div>
  </div>
  <BackToTop />
</template>

<style scoped>
.home-container {
  width: 100%;
}

.top-tag {
  display: inline-block;
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 8px;
  font-weight: bold;
  vertical-align: middle;
}

.top-article {
  border-left: 3px solid #ff4d4f;
  background-color: #fff8f8;
}

.banner {
  height: 400px;
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 30px;
}

.banner-content {
  text-align: center;
}

.banner-content h1 {
  font-size: 48px;
  margin-bottom: 10px;
  color: white;
}

.banner-content p {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  position: relative;
  padding-left: 15px;
  border-left: 4px solid #1890ff;
}

.article-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.article-card:hover {
  transform: translateY(-5px);
}

.article-cover img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 15px;
}

.article-title {
  font-size: 20px;
  margin-bottom: 10px;
  color: #333;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.article-summary {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.6;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}

.sidebar-card {
  margin-bottom: 20px;
}

.search-box {
  margin-bottom: 10px;
}

.hot-articles {
  display: flex;
  flex-direction: column;
}

.hot-article-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.hot-article-item:last-child {
  border-bottom: none;
}

.hot-article-index {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 4px;
  margin-right: 10px;
  font-weight: bold;
}

.hot-article-index.top-three {
  background-color: #ff4d4f;
  color: white;
}

.hot-article-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-article-views {
  color: #999;
  font-size: 12px;
}

.category-list {
  display: flex;
  flex-direction: column;
}

.category-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.category-item:last-child {
  border-bottom: none;
}

.category-count {
  background-color: #f0f0f0;
  border-radius: 10px;
  padding: 2px 8px;
  font-size: 12px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #1890ff;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-container {
  text-align: center;
  padding: 30px 0;
  color: #999;
}
</style>