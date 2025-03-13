<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Avatar, Tag, Divider, Button, Comment, Form, Input, message } from 'ant-design-vue';
import { LikeOutlined, MessageOutlined, EyeOutlined, ClockCircleOutlined, UserOutlined } from '@ant-design/icons-vue';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import articleApi from '@/api/article';
import commentApi from '@/api/comment';
import ReadingProgress from '@/components/ReadingProgress.vue';
import BackToTop from '@/components/BackToTop.vue';

const route = useRoute();
const router = useRouter();
const articleId = route.params.id;

// 文章数据
const article = ref({});
// 评论列表
const comments = ref([]);
// 相关文章
const relatedArticles = ref([]);
// 评论内容
const commentContent = ref('');
// 评论提交中
const submitting = ref(false);
// 加载状态
const loading = ref(false);
// 文章封面图
const defaultCover = 'https://picsum.photos/1200/400';
// 预计阅读时间（按照每分钟300字计算）
const readingTime = computed(() => {
  if (!article.value.content) return 0;
  const wordCount = article.value.content.length;
  return Math.ceil(wordCount / 300);
});

// 获取文章详情
const fetchArticleDetail = async () => {
  loading.value = true;
  try {
    const result = await articleApi.getArticleById(articleId);
    if (result && result.code === 200) {
      article.value = result.data;
      // 获取相关文章
      fetchRelatedArticles();
    } else {
      message.error('获取文章详情失败');
    }
  } catch (error) {
    console.error('获取文章详情失败:', error);
    message.error('获取文章详情失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 获取文章评论
const fetchComments = async () => {
  try {
    const result = await commentApi.getCommentList({
      articleId: articleId,
      page: 1,
      size: 50
    });
    if (result && result.code === 200) {
      comments.value = result.data.records || [];
    } else {
      message.error('获取评论失败');
    }
  } catch (error) {
    console.error('获取评论失败:', error);
    message.error('获取评论失败，请稍后重试');
  }
};

// 获取相关文章
const fetchRelatedArticles = async () => {
  try {
    // 这里可以根据文章分类或标签获取相关文章
    const result = await articleApi.getArticleList({
      categoryId: article.value.categoryId,
      page: 1,
      size: 5
    });
    if (result && result.code === 200) {
      // 过滤掉当前文章
      relatedArticles.value = (result.data.records || [])
        .filter(item => item.id !== parseInt(articleId))
        .slice(0, 3);
    }
  } catch (error) {
    console.error('获取相关文章失败:', error);
  }
};

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    message.warning('评论内容不能为空');
    return;
  }
  
  submitting.value = true;
  
  try {
    const result = await commentApi.addComment({
      articleId: articleId,
      content: commentContent.value
    });
    
    if (result && result.code === 200) {
      // 重新获取评论列表
      fetchComments();
      commentContent.value = '';
      message.success('评论发布成功');
      // 更新文章评论数
      article.value.commentCount += 1;
    } else {
      message.error(result.message || '评论发布失败');
    }
  } catch (error) {
    console.error('评论发布失败:', error);
    message.error('评论发布失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

// 点赞文章
const likeArticle = async () => {
  try {
    // 实际项目中应该调用API进行点赞
    // 这里简单模拟点赞效果
    article.value.likeCount += 1;
    message.success('点赞成功');
  } catch (error) {
    console.error('点赞失败:', error);
    message.error('点赞失败，请稍后重试');
  }
};

// 跳转到文章详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`);
};

onMounted(() => {
  fetchArticleDetail();
  fetchComments();
  // 确保页面滚动到顶部
  window.scrollTo(0, 0);
});
</script>

<template>
  <ReadingProgress />
  
  <!-- 文章头图区域 -->
  <div class="article-hero" v-if="article.id">
    <div class="article-hero-background" :style="{ backgroundImage: `url(${article.coverImage || defaultCover})` }"></div>
    <div class="article-hero-overlay"></div>
    <div class="article-hero-content">
      <div class="article-category" v-if="article.categoryName">
        <Tag color="green">{{ article.categoryName }}</Tag>
      </div>
      <h1 class="article-hero-title">{{ article.title }}</h1>
      <div class="article-hero-meta">
        <div class="author-info">
          <Avatar :src="article.authorAvatar || 'https://joeschmoe.io/api/v1/random'" :size="40" />
          <div class="publish-time"><ClockCircleOutlined /> {{ $formatDate(article.createTime) }}</div>
        </div>
        <div class="article-stats">
          <span><EyeOutlined /> {{ article.viewCount || 0 }} 阅读</span>
          <span><LikeOutlined /> {{ article.likeCount || 0 }} 点赞</span>
          <span><MessageOutlined /> {{ article.commentCount || 0 }} 评论</span>
          <span><ClockCircleOutlined /> {{ readingTime }} 分钟阅读</span>
        </div>
      </div>
      <div class="article-tags" v-if="article.tags && article.tags.length">
        <Tag v-for="tag in article.tags" :key="tag" color="blue">{{ tag }}</Tag>
      </div>
    </div>
  </div>
  
  <div class="article-detail-container">
    <div class="content-wrapper">
      <!-- 文章内容 -->
      <Card class="article-card" :loading="loading">
        <template v-if="article.id">
          <div class="article-content">
            <MdPreview :modelValue="article.content" />
          </div>
          
          <div class="article-actions">
            <Button type="primary" @click="likeArticle">
              <LikeOutlined /> 点赞 ({{ article.likeCount || 0 }})
            </Button>
          </div>
        </template>
      </Card>
      
      <!-- 评论区 -->
      <Card class="comment-card">
        <template #title>
          <div class="comment-title">评论 ({{ article.commentCount || 0 }})</div>
        </template>
        
        <!-- 评论表单 -->
        <div class="comment-form">
          <Form>
            <Form.Item>
              <Input.TextArea 
                v-model:value="commentContent" 
                :rows="4" 
                placeholder="写下你的评论..."
              />
            </Form.Item>
            <Form.Item>
              <Button 
                type="primary" 
                :loading="submitting" 
                @click="submitComment"
              >
                发表评论
              </Button>
            </Form.Item>
          </Form>
        </div>
        
        <Divider />
        
        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-if="comments.length > 0">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <Comment
                :author="comment.username"
                :avatar="comment.userAvatar || 'https://joeschmoe.io/api/v1/random'"
                :content="comment.content"
                :datetime="comment.createTime"
              >
                <template #actions>
                  <span><LikeOutlined /> {{ comment.likeCount || 0 }}</span>
                  <span>回复</span>
                </template>
                
                <!-- 嵌套评论 -->
                <template v-if="comment.children && comment.children.length > 0">
                  <Comment
                    v-for="reply in comment.children"
                    :key="reply.id"
                    :author="reply.username"
                    :avatar="reply.userAvatar || 'https://joeschmoe.io/api/v1/random'"
                    :content="reply.content"
                    :datetime="reply.createTime"
                  >
                    <template #actions>
                      <span><LikeOutlined /> {{ reply.likeCount || 0 }}</span>
                      <span>回复</span>
                    </template>
                  </Comment>
                </template>
              </Comment>
            </div>
          </div>
          <div v-else class="empty-comment">
            <p>暂无评论，快来发表第一条评论吧！</p>
          </div>
        </div>
      </Card>
    </div>
    
    <!-- 侧边栏 -->
    <div class="sidebar">
      <!-- 作者信息卡片 -->
      <Card class="sidebar-card author-card" v-if="article.id">
        <template #title>作者信息</template>
        <div class="author-card-content">
          <Avatar :src="article.authorAvatar || 'https://joeschmoe.io/api/v1/random'" :size="64" />
          <div class="author-name">{{ article.author || '匿名' }}</div>
          <div class="author-bio">{{ article.authorBio || '这个人很懒，什么都没有留下...' }}</div>
          <div class="author-stats">
            <div class="stat-item">
              <div class="stat-value">{{ article.authorArticleCount || 0 }}</div>
              <div class="stat-label">文章</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ article.authorFollowers || 0 }}</div>
              <div class="stat-label">粉丝</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ article.authorLikes || 0 }}</div>
              <div class="stat-label">获赞</div>
            </div>
          </div>
        </div>
      </Card>
      
      <Card class="sidebar-card">
        <template #title>相关文章</template>
        <div class="related-articles">
          <div v-if="relatedArticles.length > 0">
            <div v-for="article in relatedArticles" :key="article.id" class="related-article-item" @click="goToArticle(article.id)">
              <span class="related-article-title">{{ article.title }}</span>
              <span class="related-article-views"><EyeOutlined /> {{ article.viewCount || 0 }}</span>
            </div>
          </div>
          <div v-else class="empty-related">
            <p>暂无相关文章</p>
          </div>
        </div>
      </Card>
    </div>
  </div>
  <BackToTop />
</template>

<style scoped>
.article-hero {
  position: relative;
  height: 400px;
  width: 100%;
  margin-bottom: 30px;
  overflow: hidden;
}

.article-hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  filter: blur(5px);
  transform: scale(1.1);
}

.article-hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0,0,0,0.5) 0%, rgba(0,0,0,0.7) 100%);
}

.article-hero-content {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
}

.article-category {
  margin-bottom: 16px;
}

.article-hero-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.article-hero-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.publish-time {
  font-size: 14px;
  opacity: 0.8;
  margin-left: 8px;
  display: flex;
  align-items: center;
}

.publish-time .anticon {
  margin-right: 4px;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: bold;
  font-size: 16px;
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0,0,0,0.5);
}

.publish-time {
  font-size: 14px;
  opacity: 0.8;
}

.article-stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.article-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-detail-container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: flex;
  gap: 24px;
}

.content-wrapper {
  flex: 1;
}

.sidebar {
  width: 300px;
}

@media (max-width: 768px) {
  .article-detail-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
}

.article-card {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.article-content {
  margin-bottom: 24px;
  line-height: 1.8;
}

.article-actions {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.comment-card {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.comment-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.comment-form {
  margin-bottom: 24px;
  background-color: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
}

.comment-item {
  margin-bottom: 16px;
  transition: transform 0.2s;
}

.comment-item:hover {
  transform: translateX(5px);
}

.empty-comment, .empty-related {
  text-align: center;
  color: #999;
  padding: 20px 0;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.sidebar-card {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
}

.sidebar-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.sidebar-card-title, .author-card-title {
  font-weight: bold;
  color: #333;
}

.author-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 10px 0;
}

.author-name {
  margin-top: 12px;
  margin-bottom: 4px;
  font-size: 18px;
  color: #333;
}

.author-bio {
  color: #666;
  margin-bottom: 16px;
  font-size: 14px;
}

.author-stats {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-top: 10px;
  background-color: #f9f9f9;
  padding: 10px 0;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.related-articles {
  max-height: 400px;
  overflow-y: auto;
  padding: 0 5px;
}

.related-article-item {
  display: flex;
  flex-direction: column;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.related-article-item:hover {
  background-color: #f0f8ff;
  padding-left: 8px;
}

.related-article-title {
  font-size: 14px;
  margin-bottom: 5px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-article-views {
  font-size: 12px;
  color: #999;
}
</style>