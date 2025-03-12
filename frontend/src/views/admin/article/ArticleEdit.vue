<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Form, Input, Button, Select, message, Card, Upload } from 'ant-design-vue';
import { PlusOutlined, UploadOutlined } from '@ant-design/icons-vue';
import articleApi from '../../../api/article';
import categoryApi from '../../../api/category';
import tagApi from '../../../api/tag';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const route = useRoute();
const router = useRouter();
const articleId = route.params.id;
const isEdit = !!articleId;

// 表单数据
const formState = ref({
  title: '',
  content: '',
  summary: '',
  categoryId: undefined,
  tags: [],
  coverImage: ''
});

// 分类列表
const categories = ref([]);
// 标签列表
const tags = ref([]);
// 提交中状态
const submitting = ref(false);

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  // 获取分类和标签数据
  fetchCategoriesAndTags();
  
  // 如果是编辑模式，获取文章详情
  if (isEdit) {
    fetchArticleDetail();
  }
});

// 获取分类和标签数据
const fetchCategoriesAndTags = async () => {
  try {
    // 获取所有分类
    const categoriesResult = await categoryApi.getAllCategories();
    categories.value = categoriesResult.data || [];
    
    // 获取所有标签
    const tagsResult = await tagApi.getAllTags();
    tags.value = tagsResult.data || [];
  } catch (error) {
    console.error('获取分类和标签数据失败:', error);
    message.error('获取分类和标签数据失败，请稍后重试');
    categories.value = [];
    tags.value = [];
  }
};

// 获取文章详情
const fetchArticleDetail = async () => {
  try {
    // 获取文章详情
    const result = await articleApi.getArticleById(articleId);
    if (result && result.data) {
      // 填充表单数据
      const article = result.data;
      formState.value.title = article.title;
      formState.value.content = article.content;
      formState.value.summary = article.summary || '';
      formState.value.categoryId = article.categoryId;
      formState.value.tags = article.tags ? article.tags.map(tag => tag.id) : [];
      formState.value.coverImage = article.coverImage || '';
    }
  } catch (error) {
    console.error('获取文章详情失败:', error);
    message.error('获取文章详情失败，请稍后重试');
  }
};

// 提交表单
const onSubmit = async () => {
  submitting.value = true;
  
  try {
    const articleData = {
      title: formState.value.title,
      content: formState.value.content,
      summary: formState.value.summary,
      categoryId: formState.value.categoryId,
      tags: formState.value.tags,
      coverImage: formState.value.coverImage
    };
    
    if (isEdit) {
      // 更新文章
      await articleApi.updateArticle(articleId, articleData);
      message.success('文章更新成功');
    } else {
      // 添加文章
      await articleApi.addArticle(articleData);
      message.success('文章发布成功');
    }
    
    // 跳转到文章列表页
    router.push('/admin/article');
  } catch (error) {
    console.error(isEdit ? '更新文章失败:' : '发布文章失败:', error);
    message.error(isEdit ? '更新文章失败，请稍后重试' : '发布文章失败，请稍后重试');
  } finally {
    submitting.value = false;
  }

};

// 返回列表页
const goBack = () => {
  router.push('/admin/article');
};
</script>

<template>
  <div class="article-edit-container">
    <Card :title="isEdit ? '编辑文章' : '添加文章'" :bordered="false">
      <Form layout="vertical" :model="formState">
        <Form.Item label="文章标题" name="title" required>
          <Input v-model:value="formState.title" placeholder="请输入文章标题" />
        </Form.Item>
        
        <Form.Item label="文章分类" name="categoryId" required>
          <Select
            v-model:value="formState.categoryId"
            placeholder="请选择文章分类"
            :options="categories.map(c => ({ value: c.id, label: c.name }))"
          />
        </Form.Item>
        
        <Form.Item label="文章标签" name="tags">
          <Select
            v-model:value="formState.tags"
            mode="multiple"
            placeholder="请选择文章标签"
            :options="tags.map(t => ({ value: t.id, label: t.name }))"
          />
        </Form.Item>
        
        <Form.Item label="封面图片" name="coverImage">
          <Input v-model:value="formState.coverImage" placeholder="请输入封面图片URL" />
          <!-- 实际项目中可以添加图片上传功能 -->
        </Form.Item>
        
        <Form.Item label="文章摘要" name="summary">
          <Input.TextArea v-model:value="formState.summary" rows="4" placeholder="请输入文章摘要" />
        </Form.Item>
        
        <Form.Item label="文章内容" name="content" required>
          <MdEditor
            v-model="formState.content"
            height="500px"
            :toolbars="[
              'bold', 'italic', 'strikethrough', 'title', 'sub', 'sup', 'quote',
              'unorderedList', 'orderedList', 'codeRow', 'code', 'link', 'image',
              'table', 'revoke', 'next', 'save', 'preview'
            ]"
            placeholder="请输入文章内容（支持Markdown格式）"
          />
        </Form.Item>
        
        <Form.Item>
          <Button type="primary" :loading="submitting" @click="onSubmit">
            {{ isEdit ? '更新文章' : '发布文章' }}
          </Button>
          <Button style="margin-left: 10px" @click="goBack">取消</Button>
        </Form.Item>
      </Form>
    </Card>
  </div>
</template>

<style scoped>
.article-edit-container {
  width: 100%;
}

/* 确保Markdown编辑器在表单中正确显示 */
:deep(.md-editor) {
  margin-bottom: 16px;
}

:deep(.md-editor-preview-wrapper) {
  padding: 16px;
}
</style>