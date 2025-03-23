<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Row, Col, Pagination, Tag, Input, Button, message } from 'ant-design-vue';
import { SearchOutlined } from '@ant-design/icons-vue';
import articleApi from '@/api/article';

const route = useRoute();
const router = useRouter();

// 搜索关键词
const keyword = ref(route.query.keyword || '');
// 搜索结果
const searchResults = ref([]);
// 分页参数
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
});
// 是否正在搜索
const searching = ref(false);

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  // 如果有关键词，则执行搜索
  if (keyword.value) {
    doSearch();
  }
});

// 执行搜索
const doSearch = async () => {
  if (!keyword.value.trim()) {
    return;
  }
  
  searching.value = true;
  
  try {
    // 通过API获取搜索结果
    const response = await articleApi.getArticleList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      keyword: keyword.value,
      status: 1 // 只获取已发布的文章
    });
    
    if (response.code === 200) {
      searchResults.value = response.data.records || [];
      pagination.value.total = response.data.total || 0;
    } else {
      message.error(response.message || '搜索失败');
      searchResults.value = [];
      pagination.value.total = 0;
    }
  } catch (error) {
    console.error('搜索失败:', error);
    message.error('搜索失败，请稍后再试');
    searchResults.value = [];
    pagination.value.total = 0;
  } finally {
    searching.value = false;
    
    // 更新URL参数，但不重新加载页面
    router.replace({ query: { keyword: keyword.value } });
  }
};

// 处理分页变化
const handlePageChange = (page) => {
  pagination.value.current = page;
  // 实际项目中应该调用API获取对应页的数据
  doSearch();
};

// 跳转到文章详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`);
};
</script>

<template>
  <div class="search-container">
    <div class="content-wrapper">
      <div class="search-header">
        <h1>搜索</h1>
        <div class="search-form">
          <Input.Search
            v-model:value="keyword"
            placeholder="输入关键词搜索文章..."
            enter-button
            :loading="searching"
            @search="doSearch"
            size="large"
          />
        </div>
      </div>
      
      <div class="search-results">
        <h2 class="section-title" v-if="keyword">搜索结果: {{ keyword }}</h2>
        
        <div v-if="searchResults.length > 0">
          <Card v-for="article in searchResults" :key="article.id" class="article-card" hoverable @click="goToArticle(article.id)">
            <div class="article-cover" v-if="article.coverImage">
              <img :src="article.coverImage" :alt="article.title" />
            </div>
            <div class="article-content">
              <h3 class="article-title">{{ article.title }}</h3>
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
        </div>
        <div v-else-if="keyword && !searching" class="empty-list">
          <p>未找到相关文章</p>
        </div>
        
        <div class="pagination" v-if="pagination.total > 0">
          <Pagination 
            v-model:current="pagination.current" 
            :total="pagination.total" 
            :pageSize="pagination.pageSize"
            @change="handlePageChange"
            show-quick-jumper
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-container {
  padding: 20px 0;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-header {
  margin-bottom: 30px;
}

.search-header h1 {
  font-size: 28px;
  margin-bottom: 20px;
}

.search-form {
  max-width: 600px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  position: relative;
  padding-left: 15px;
  border-left: 4px solid #1890ff;
}

.empty-list {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.pagination {
  margin-top: 30px;
  text-align: center;
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
</style>