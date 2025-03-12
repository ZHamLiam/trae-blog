<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Row, Col, Pagination, Tag, Divider } from 'ant-design-vue';

const route = useRoute();
const router = useRouter();
const categoryId = route.params.id;

// 分类信息
const category = ref({});
// 文章列表
const articles = ref([]);
// 分页参数
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
});

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  // 这里将来会通过API获取分类信息和该分类下的文章列表
  console.log('分类ID:', categoryId);
  
  // 模拟分类数据
  category.value = {
    id: categoryId,
    name: '示例分类',
    description: '这是一个示例分类描述'
  };
  
  // 模拟文章列表数据
  articles.value = [];
  
  // 设置分页总数
  pagination.value.total = 0;
});

// 处理分页变化
const handlePageChange = (page) => {
  pagination.value.current = page;
  // 实际项目中应该调用API获取对应页的数据
  console.log('加载第', page, '页数据');
};

// 跳转到文章详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`);
};
</script>

<template>
  <div class="category-list-container">
    <div class="content-wrapper">
      <div class="category-header">
        <h1>{{ category.name }}</h1>
        <p v-if="category.description">{{ category.description }}</p>
      </div>
      
      <div class="article-list">
        <h2 class="section-title">分类文章</h2>
        
        <div v-if="articles.length > 0">
          <!-- 文章列表内容将在这里显示 -->
        </div>
        <div v-else class="empty-list">
          <p>该分类下暂无文章</p>
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
.category-list-container {
  padding: 20px 0;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.category-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.category-header h1 {
  font-size: 28px;
  margin-bottom: 10px;
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