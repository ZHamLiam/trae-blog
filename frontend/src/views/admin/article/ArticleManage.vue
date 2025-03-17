<script setup>
import { ref, onMounted, h, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { Table, Card, Button, Space, Popconfirm, message, Tag, Modal, Form, Input, Select } from 'ant-design-vue';
import { PlusOutlined, EditOutlined, DeleteOutlined, CheckOutlined, StopOutlined, VerticalAlignTopOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import articleApi from '../../../api/article';
import categoryApi from '../../../api/category';
import tagApi from '../../../api/tag';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';

// 查询条件
const queryParams = ref({
  categoryId: undefined,
  tagId: undefined,
  status: undefined,
  keyword: ''
});

// 分类和标签数据
const categories = ref([]);
const tags = ref([]);

// 选中的文章ID列表
const selectedRowKeys = ref([]);

// 获取分类列表
const fetchCategories = async () => {
  try {
    const result = await categoryApi.getAllCategories();
    if (result && result.code === 200) {
      categories.value = result.data;
    }
  } catch (error) {
    console.error('获取分类列表失败:', error);
  }
};

// 获取标签列表
const fetchTags = async () => {
  try {
    const result = await tagApi.getAllTags();
    if (result && result.code === 200) {
      tags.value = result.data;
    }
  } catch (error) {
    console.error('获取标签列表失败:', error);
  }
};

// 批量删除文章
const batchDeleteArticles = async () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的文章');
    return;
  }
  
  try {
    await articleApi.batchDeleteArticles(selectedRowKeys.value);
    message.success('批量删除成功');
    selectedRowKeys.value = [];
    fetchArticles();
  } catch (error) {
    console.error('批量删除失败:', error);
    message.error('批量删除失败，请稍后重试');
  }
};

// 重置查询条件
const resetQuery = () => {
  queryParams.value = {
    categoryId: undefined,
    tagId: undefined,
    status: undefined,
    keyword: ''
  };
  fetchArticles();
};

// 处理查询
const handleQuery = () => {
  pagination.value.current = 1;
  fetchArticles();
};

const router = useRouter();
const { proxy } = getCurrentInstance();

// 文章列表数据
const articles = ref([]);
// 加载状态
const loading = ref(false);
// 分页参数
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true
});

// 文章预览相关
const previewVisible = ref(false);
const previewTitle = ref('');
const previewContent = ref('');
const previewLoading = ref(false);

// 表格列定义
const columns = [
  {
    title: '选择',
    dataIndex: 'id',
    width: 60,
    align: 'center'
  },
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title',
    ellipsis: true,
    customRender: ({ text, record }) => {
      return h('a', {
        onClick: () => previewArticle(record.id),
        class: 'article-link'
      }, text);
    }
  },
  {
    title: '分类',
    dataIndex: 'categoryName',
    key: 'categoryName'
  },
  {
    title: '标签',
    dataIndex: 'tags',
    key: 'tags',
    customRender: ({ text }) => {
      return h('span', {}, 
        text && text.map(tag => 
          h(Tag, { color: 'blue', key: tag }, tag)
        )
      );
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    customRender: ({ text }) => {
      if (text === 1) {
        return h(Tag, { color: 'green' }, '已发布');
      } else {
        return h(Tag, { color: 'orange' }, '草稿');
      }
    }
  },
  {
    title: '置顶',
    dataIndex: 'isTop',
    key: 'isTop',
    customRender: ({ text }) => {
      if (text === 1) {
        return h(Tag, { color: 'red' }, '已置顶');
      } else {
        return h(Tag, { color: 'default' }, '未置顶');
      }
    }
  },
  {
    title: '浏览量',
    dataIndex: 'viewCount',
    key: 'viewCount',
    sorter: true
  },
  {
    title: '评论数',
    dataIndex: 'commentCount',
    key: 'commentCount',
    sorter: true
  },
  {
    title: '发布时间',
    dataIndex: 'createTime',
    key: 'createTime',
    sorter: true,
    customRender: ({ text }) => {
      return h('span', {}, text ? proxy.$formatDate(text) : '');
    }
  },
  {
    title: '操作',
    key: 'action',
    width: 300,
    customRender: ({ record }) => {
      return h('div', {}, [
        h(Space, {}, [
          // 编辑按钮
          h(Button, {
            type: 'primary',
            size: 'small',
            onClick: () => editArticle(record.id)
          }, [
            h(EditOutlined),
            ' 编辑'
          ]),
          
          // 状态切换按钮
          record.status === 0 ?
            h(Button, {
              type: 'primary',
              size: 'small',
              onClick: () => updateArticleStatus(record.id, 1)
            }, [
              h(CheckOutlined),
              ' 发布'
            ]) :
            h(Button, {
              type: 'default',
              size: 'small',
              onClick: () => updateArticleStatus(record.id, 0)
            }, [
              h(StopOutlined),
              ' 下架'
            ]),
          
          // 置顶切换按钮
          record.isTop === 0 ?
            h(Button, {
              type: 'default',
              size: 'small',
              onClick: () => updateArticleTopStatus(record.id, 1)
            }, [
              h(VerticalAlignTopOutlined),
              ' 置顶'
            ]) :
            h(Button, {
              type: 'default',
              size: 'small',
              danger: true,
              onClick: () => updateArticleTopStatus(record.id, 0)
            }, [
              h(VerticalAlignTopOutlined),
              ' 取消置顶'
            ]),
          
          // 删除按钮已移除，统一使用批量删除
        ])
      ]);
    }
  }
];

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  fetchArticles();
  fetchCategories();
  fetchTags();
});

// 获取文章列表
const fetchArticles = async (params = {}) => {
  loading.value = true;
  
  try {
    // 合并分页参数和查询条件
    const requestParams = {
      page: params.current || pagination.value.current,
      size: params.pageSize || pagination.value.pageSize,
      ...queryParams.value,  // 添加查询条件
      ...params
    };
    delete requestParams.current; // 删除current参数
    delete requestParams.pageSize; // 删除pageSize参数，使用size
    
    // 调用API获取文章列表
    const result = await articleApi.getArticleList(requestParams);
    
    // 更新数据，根据API返回的数据结构进行适配
    if (result && result.code === 200) {
      // 处理分页数据结构
      if (result.data && result.data.records) {
        // 标准分页结构
        articles.value = result.data.records;
        pagination.value.total = result.data.total || 0;
        pagination.value.current = result.data.current || pagination.value.current;
        pagination.value.pageSize = result.data.size || pagination.value.pageSize;
      } else if (result.data && Array.isArray(result.data)) {
        // 数据直接是数组
        articles.value = result.data;
        pagination.value.total = result.total || articles.value.length || 0;
      } else {
        // 其他情况，尝试直接使用
        articles.value = [];
        pagination.value.total = 0;
      }
    } else {
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

// 处理表格变化（分页、排序等）
const handleTableChange = (pag, filters, sorter) => {
  fetchArticles({
    pageSize: pag.pageSize,
    current: pag.current,
    sortField: sorter.field,
    sortOrder: sorter.order
  });
};

// 添加文章
const addArticle = () => {
  router.push('/admin/article/edit');
};

// 编辑文章
const editArticle = (id) => {
  router.push(`/admin/article/edit/${id}`);
};

// 删除文章
const deleteArticle = async (id) => {
  try {
    // 调用API删除文章
    await articleApi.deleteArticle(id);
    message.success('文章删除成功');
    // 重新加载文章列表
    fetchArticles();
  } catch (error) {
    console.error('删除文章失败:', error);
    message.error('删除文章失败，请稍后重试');
  }
};

// 更新文章状态
const updateArticleStatus = async (id, status) => {
  try {
    // 调用API更新文章状态
    await articleApi.updateArticleStatus(id, status);
    message.success(status === 1 ? '文章发布成功' : '文章已设为草稿');
    // 重新加载文章列表
    fetchArticles();
  } catch (error) {
    console.error('更新文章状态失败:', error);
    message.error('更新文章状态失败，请稍后重试');
  }
};

// 更新文章置顶状态
const updateArticleTopStatus = async (id, isTop) => {
  try {
    // 调用API更新文章置顶状态
    await articleApi.updateArticleTopStatus(id, isTop);
    message.success(isTop === 1 ? '文章已置顶' : '文章已取消置顶');
    // 重新加载文章列表
    fetchArticles();
  } catch (error) {
    console.error('更新文章置顶状态失败:', error);
    message.error('更新文章置顶状态失败，请稍后重试');
  }
};

// 预览文章内容
const previewArticle = async (articleId) => {
  previewLoading.value = true;
  previewVisible.value = true;
  
  try {
    const response = await articleApi.getArticleById(articleId);
    
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
</script>

<template>
  <div class="article-manage-container">
    <Card title="文章管理" :bordered="false" class="admin-card">
      <!-- 查询表单 -->
      <Form layout="inline" :model="queryParams" class="admin-search-form">
        <Form.Item label="分类">
          <Select
            v-model:value="queryParams.categoryId"
            placeholder="请选择分类"
            allowClear
            style="width: 200px"
          >
            <Select.Option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </Select.Option>
          </Select>
        </Form.Item>
        
        <Form.Item label="标签">
          <Select
            v-model:value="queryParams.tagId"
            placeholder="请选择标签"
            allowClear
            style="width: 200px"
          >
            <Select.Option v-for="tag in tags" :key="tag.id" :value="tag.id">
              {{ tag.name }}
            </Select.Option>
          </Select>
        </Form.Item>
        
        <Form.Item label="状态">
          <Select
            v-model:value="queryParams.status"
            placeholder="请选择状态"
            allowClear
            style="width: 200px"
          >
            <Select.Option :value="1">已发布</Select.Option>
            <Select.Option :value="0">草稿</Select.Option>
          </Select>
        </Form.Item>
        
        <Form.Item label="关键词">
          <Input
            v-model:value="queryParams.keyword"
            placeholder="请输入关键词"
            allowClear
            style="width: 200px"
          />
        </Form.Item>
        
        <Form.Item>
          <Space>
            <Button type="primary" @click="handleQuery">
              <SearchOutlined /> 查询
            </Button>
            <Button @click="resetQuery">
              <ReloadOutlined /> 重置
            </Button>
          </Space>
        </Form.Item>
      </Form>
      
      <!-- 操作按钮 -->
      <div class="table-operations">
        <Space>
          <Button type="primary" @click="addArticle">
            <PlusOutlined /> 添加文章
          </Button>
          <Popconfirm
            title="确定要删除选中的文章吗？"
            @confirm="batchDeleteArticles"
            okText="确定"
            cancelText="取消"
          >
            <Button type="primary" danger :disabled="selectedRowKeys.length === 0">
              <DeleteOutlined /> 批量删除
            </Button>
          </Popconfirm>
        </Space>
      </div>
      
      <Table
        :columns="columns"
        :dataSource="articles"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        rowKey="id"
        :rowSelection="{
          selectedRowKeys: selectedRowKeys,
          onChange: (keys) => selectedRowKeys = keys
        }"
        class="admin-table"
      />
    </Card>
    
    <!-- 文章预览弹窗 -->
    <Modal
      v-model:visible="previewVisible"
      :title="previewTitle"
      :width="800"
      @cancel="closePreview"
      :footer="null"
    >
      <a-spin :spinning="previewLoading">
        <md-preview :modelValue="previewContent" />
      </a-spin>
    </Modal>
  </div>
</template>

<style scoped>
.article-manage-container {
  width: 100%;
}

.article-link {
  cursor: pointer;
  color: #1890ff;
}

.article-link:hover {
  text-decoration: underline;
}

.search-form {
  margin-bottom: 16px;
  background-color: #fafafa;
  padding: 16px;
  border-radius: 4px;
}

.table-operations {
  margin-bottom: 16px;
}
</style>