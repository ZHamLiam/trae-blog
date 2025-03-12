<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Row, Col, Pagination, Tag, Input, Button } from 'ant-design-vue';
import { SearchOutlined } from '@ant-design/icons-vue';

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
const doSearch = () => {
  if (!keyword.value.trim()) {
    return;
  }
  
  searching.value = true;
  
  // 这里将来会通过API获取搜索结果
  console.log('搜索关键词:', keyword.value);
  
  // 模拟搜索结果
  setTimeout(() => {
    searchResults.value = [];
    pagination.value.total = 0;
    searching.value = false;
    
    // 更新URL参数，但不重新加载页面
    router.replace({ query: { keyword: keyword.value } });
  }, 500);
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
          <!-- 搜索结果列表将在这里显示 -->
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
</style>