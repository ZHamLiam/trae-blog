<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Row, Col, Pagination, Tag, Divider, message, Skeleton, Button } from 'ant-design-vue';
import { EyeOutlined, CalendarOutlined, TagOutlined, HomeOutlined } from '@ant-design/icons-vue';
import articleApi from '@/api/article';
import tagApi from '@/api/tag';
import BackToTop from '@/components/BackToTop.vue';

const route = useRoute();
const router = useRouter();
const tagId = ref(route.params.id);

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

// 加载状态
const loading = ref(false);

// 获取标签信息
const fetchTagInfo = async () => {
  try {
    const result = await tagApi.getTagById(tagId.value);
    if (result && result.code === 200) {
      tag.value = result.data;
    } else {
      message.error('获取标签信息失败');
    }
  } catch (error) {
    console.error('获取标签信息失败:', error);
    message.error('获取标签信息失败，请稍后重试');
  }
};

// 获取标签下的文章列表
const fetchArticles = async () => {
  loading.value = true;
  try {
    const result = await articleApi.getArticleList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      tagId: tagId.value,
      status: 1 // 只获取已发布的文章
    });
    
    if (result && result.code === 200) {
      articles.value = result.data.records || [];
      pagination.value.total = result.data.total || 0;
    } else {
      message.error('获取文章列表失败');
      articles.value = [];
      pagination.value.total = 0;
    }
  } catch (error) {
    console.error('获取文章列表失败:', error);
    message.error('获取文章列表失败，请稍后重试');
    articles.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

// 处理分页变化
const handlePageChange = (page) => {
  pagination.value.current = page;
  fetchArticles();
};

// 跳转到文章详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`);
};

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    tagId.value = newId;
    pagination.value.current = 1;
    fetchTagInfo();
    fetchArticles();
  }
});

onMounted(() => {
  fetchTagInfo();
  fetchArticles();
});
</script>

<template>
  <div class="tag-list-container">
    <!-- 返回首页按钮 -->
    <div class="home-button-container">
      <Button type="primary" @click="goToHome" class="home-button">
        <HomeOutlined /> 返回首页
      </Button>
    </div>
    
    <div class="content-wrapper">
      <div class="tag-header">
        <h1 class="tag-title">标签: {{ tag.name }}</h1>
        <div class="tag-description" v-if="tag.description">{{ tag.description }}</div>
        <div class="tag-meta" v-if="tag.id">
          <span><TagOutlined /> 标签ID: {{ tag.id }}</span>
          <span><EyeOutlined /> 文章数量: {{ pagination.total }}</span>
          <span><CalendarOutlined /> 创建时间: {{ tag.createTime }}</span>
        </div>
      </div>
      
      <div class="article-list">
        <h2 class="section-title">标签文章</h2>
        
        <Skeleton :loading="loading" active :paragraph="{ rows: 5 }">
          <template v-if="articles.length > 0">
            <Row :gutter="[16, 16]">
              <Col :xs="24" :sm="24" :md="12" :lg="8" :xl="8" v-for="article in articles" :key="article.id">
                <Card class="article-card" :class="{'top-article': article.isTop === 1}" hoverable @click="goToArticle(article.id)">
                  <div class="article-info">
                    <div class="article-header">
                      <h3 class="article-title">
                        <span v-if="article.isTop === 1" class="top-tag">置顶</span>
                        {{ article.title }}
                      </h3>
                    </div>
                    <p class="article-summary">{{ article.summary }}</p>
                    <div class="article-meta">
                      <span v-if="article.categoryName" class="category"><TagOutlined /> {{ article.categoryName }}</span>
                      <span class="time"><CalendarOutlined /> {{ article.createTime?.split(' ')[0] }}</span>
                      <span class="views"><EyeOutlined /> {{ article.viewCount }}</span>
                    </div>
                  </div>
                </Card>
              </Col>
            </Row>
          </template>
          <div v-else class="empty-list">
            <p>该标签下暂无文章</p>
          </div>
        </Skeleton>
        
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
    
    <!-- 返回顶部按钮 -->
    <BackToTop />
  </div>
</template>

<style scoped>
.home-button-container {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
}

.home-button {
  display: flex;
  align-items: center;
  gap: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.home-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.tag-list-container {
  padding: 20px 0;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.tag-header {
  margin-bottom: 30px;
  padding: 30px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.tag-header:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.tag-title {
  font-size: 32px;
  margin-bottom: 15px;
  color: #333;
  border-left: 4px solid #1890ff;
  padding-left: 15px;
}

.tag-description {
  color: #666;
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.tag-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #999;
  font-size: 14px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  position: relative;
  padding-left: 15px;
  border-left: 4px solid #1890ff;
  color: #333;
}

.article-card {
  height: 100%;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.article-header {
  margin-bottom: 10px;
}

.article-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.article-summary {
  color: #666;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  color: #999;
  font-size: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px dashed #eee;
}

.empty-list {
  text-align: center;
  padding: 60px 0;
  color: #999;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
  transform: scale(1);
  transition: transform 0.2s;
}

.article-title:hover .top-tag {
  transform: scale(1.1);
}

.top-article {
  border-left: 3px solid #ff4d4f;
  background-color: #fff8f8;
}

.pagination {
  margin-top: 30px;
  text-align: center;
  background-color: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

@media (max-width: 768px) {
  .tag-title {
    font-size: 24px;
  }
  
  .tag-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .article-card {
    margin-bottom: 15px;
  }
}
</style>

// 返回首页
const goToHome = () => {
  router.push('/');
};