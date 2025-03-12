<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Row, Col, Pagination, Tag, Divider } from 'ant-design-vue';

const route = useRoute();
const router = useRouter();
const tagId = route.params.id;

// 标签信息
const tag = ref({});
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
  // 这里将来会通过API获取标签信息和该标签下的文章列表
  console.log('标签ID:', tagId);
  
  // 模拟标签数据
  tag.value = {
    id: tagId,
    name: '示例标签'
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
  <div class="tag-list-container">
    <div class="content-wrapper">
      <div class="tag-header">
        <h1>标签: {{ tag.name }}</h1>
      </div>
      
      <div class="article-list">
        <h2 class="section-title">标签文章</h2>
        
        <div v-if="articles.length > 0">
          <!-- 文章列表内容将在这里显示 -->
        </div>
        <div v-else class="empty-list">
          <p>该标签下暂无文章</p>
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
.tag-list-container {
  padding: 20px 0;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.tag-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.tag-header h1 {
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