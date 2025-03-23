<script setup>
import { ref, onMounted, h, getCurrentInstance } from 'vue';
import { Table, Card, Button, Space, Popconfirm, message, Tag, Form, Input, Select } from 'ant-design-vue';
import { DeleteOutlined, CheckOutlined, StopOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import UserAvatar from '../../../components/UserAvatar.vue';
import commentApi from '../../../api/comment';

// 查询条件
const queryParams = ref({
  articleId: undefined,
  status: undefined,
  keyword: ''
});

// 选中的评论ID列表
const selectedRowKeys = ref([]);

// 批量删除评论
const batchDeleteComments = async () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的评论');
    return;
  }
  
  try {
    await commentApi.batchDeleteComments(selectedRowKeys.value);
    message.success('批量删除成功');
    selectedRowKeys.value = [];
    fetchComments();
  } catch (error) {
    console.error('批量删除失败:', error);
    message.error('批量删除失败，请稍后重试');
  }
};

// 重置查询条件
const resetQuery = () => {
  queryParams.value = {
    articleId: undefined,
    status: undefined,
    keyword: ''
  };
  fetchComments();
};

// 处理查询
const handleQuery = () => {
  pagination.value.current = 1;
  fetchComments();
};

// 获取当前实例
const { proxy } = getCurrentInstance();

// 评论列表数据
const comments = ref([]);
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

// 表格列定义
const columns = [
  {
    title: '选择',
    dataIndex: 'id',
    width: 60,
    align: 'center'
  },
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
    dataIndex: 'userName',
    key: 'userName',
    customRender: ({ text, record }) => {
      return h('div', { style: { display: 'flex', alignItems: 'center', gap: '8px' } }, [
        h(UserAvatar, {
          username: text,
          src: record.userAvatar,
          size: 'small'
        }),
        h('span', {}, text)
      ]);
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    customRender: ({ text }) => {
      return h(Tag, {
        color: text === 1 ? 'green' : text === 0 ? 'orange' : 'red'
      }, text === 1 ? '已审核' : text === 0 ? '待审核' : '已拒绝');
    }
  },
  {
    title: '评论时间',
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
    width: 250,
    customRender: ({ record }) => {
      const buttons = [];
      
      if (record.status !== 1 && record.status !== 'approved') {
        buttons.push(
          h(Button, {
            type: 'primary',
            size: 'small',
            onClick: () => approveComment(record.id)
          }, [
            h(CheckOutlined),
            ' 发布'
          ])
        );
      }
      
      if (record.status !== 2 && record.status !== 'rejected') {
        buttons.push(
          h(Button, {
            type: 'primary',
            danger: true,
            size: 'small',
            onClick: () => rejectComment(record.id)
          }, [
            h(StopOutlined),
            ' 拒绝'
          ])
        );
      }
      
      // 删除按钮已移除，统一使用批量删除
      
      return h(Space, {}, buttons);
    }
  }
];

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  fetchComments();
});

// 获取评论列表
const fetchComments = async (params = {}) => {
  loading.value = true;
  
  try {
    // 合并分页参数，将current转换为page
    const queryParams = {
      page: params.current || pagination.value.current,
      size: params.pageSize || pagination.value.pageSize,
      ...params
    };
    
    
    
    // 调用API获取评论列表
    const result = await commentApi.getCommentList(queryParams);
    
    // 添加调试日志，查看API返回的数据结构
    console.log('评论列表API返回数据:', result);
    
    // 更新数据，根据API返回的数据结构进行适配
    if (result && result.code === 200) {
      // 如果API返回了标准的成功响应结构
      comments.value = Array.isArray(result.data.records) ? result.data.records : 
                      (Array.isArray(result.data) ? result.data : []);
      pagination.value.total = result.data.total || result.total || 0;
      pagination.value.current=result.data.current || result.current || 1;
    } else {
      // 直接尝试使用返回的数据
      comments.value = Array.isArray(result.data) ? result.data : 
                      (Array.isArray(result) ? result : []);
      pagination.value.total = result.total || 0;
    }
  } catch (error) {
    console.error('获取评论列表失败:', error);
    message.error('获取评论列表失败，请稍后重试');
    comments.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

// 处理表格变化（分页、排序等）
const handleTableChange = (pag, filters, sorter) => {
  fetchComments({
    pageSize: pag.pageSize,
    current: pag.current,
    sortField: sorter.field,
    sortOrder: sorter.order,
    ...filters
  });
};

// 通过评论
const approveComment = async (id) => {
  try {
    // 调用API通过评论
    await commentApi.updateCommentStatus(id, 1);
    message.success('评论已发布');
    fetchComments();
  } catch (error) {
    console.error('发布评论失败:', error);
    message.error('发布评论失败，请稍后重试');
  }
};

// 拒绝评论
const rejectComment = async (id) => {
  try {
    // 调用API拒绝评论
    await commentApi.updateCommentStatus(id, 2);
    message.success('评论已被拒绝');
    fetchComments();
  } catch (error) {
    console.error('拒绝评论失败:', error);
    message.error('拒绝评论失败，请稍后重试');
  }
};

// 删除评论
const deleteComment = async (id) => {
  try {
    // 调用API删除评论
    await commentApi.deleteComment(id);
    message.success('评论删除成功');
    fetchComments();
  } catch (error) {
    console.error('删除评论失败:', error);
    message.error('删除评论失败，请稍后重试');
  }
};
</script>

<template>
  <div class="comment-manage-container">
    <Card title="评论管理" :bordered="false">
      <!-- 查询表单 -->
      <Form layout="inline" :model="queryParams" class="search-form">
        <Form.Item label="文章ID">
          <Input
            v-model:value="queryParams.articleId"
            placeholder="请输入文章ID"
            allowClear
            style="width: 200px"
          />
        </Form.Item>
        
        <Form.Item label="状态">
          <Select
            v-model:value="queryParams.status"
            placeholder="请选择状态"
            allowClear
            style="width: 200px"
          >
            <Select.Option value="1">已发布</Select.Option>
            <Select.Option value="0">待审核</Select.Option>
            <Select.Option value="2">已拒绝</Select.Option>
          </Select>
        </Form.Item>
        
        <Form.Item label="关键词">
          <Input
            v-model:value="queryParams.keyword"
            placeholder="请输入评论内容关键词"
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
          <Popconfirm
            title="确定要删除选中的评论吗？"
            @confirm="batchDeleteComments"
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
        :dataSource="comments"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        rowKey="id"
        :rowSelection="{
          selectedRowKeys: selectedRowKeys,
          onChange: (keys) => selectedRowKeys = keys
        }"
      />
    </Card>
  </div>
</template>

<style scoped>
.comment-manage-container {
  width: 100%;
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