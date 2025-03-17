<script setup>
import { ref, onMounted, reactive, h } from 'vue';
import { Table, Card, Button, Space, Popconfirm, message, Modal, Form, Input } from 'ant-design-vue';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import tagApi from '../../../api/tag';

// 查询条件
const queryParams = ref({
  keyword: ''
});

// 选中的标签ID列表
const selectedRowKeys = ref([]);

// 批量删除标签
const batchDeleteTags = async () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的标签');
    return;
  }
  
  try {
    await tagApi.batchDeleteTags(selectedRowKeys.value);
    message.success('批量删除成功');
    selectedRowKeys.value = [];
    fetchTags();
  } catch (error) {
    console.error('批量删除失败:', error);
    message.error('批量删除失败，请稍后重试');
  }
};

// 重置查询条件
const resetQuery = () => {
  queryParams.value = {
    keyword: ''
  };
  fetchTags();
};

// 处理查询
const handleQuery = () => {
  pagination.value.current = 1;
  fetchTags();
};

// 标签列表数据
const tags = ref([]);
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

// 模态框可见性
const modalVisible = ref(false);
// 模态框标题
const modalTitle = ref('添加标签');
// 是否为编辑模式
const isEdit = ref(false);
// 表单数据
const formState = reactive({
  id: null,
  name: ''
});

// 表单引用
const formRef = ref();

// 表格列定义
const columns = [
  {
    title: '选择',
    dataIndex: 'id',
    width: 60,
    align: 'center'
  },
  {
    title: '标签名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
  },
  {
    title: '文章数量',
    dataIndex: 'articleCount',
    key: 'articleCount',
    sorter: true
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    customRender: ({ record }) => {
      return h(Space, {}, [
        h(Button, {
          type: 'primary',
          size: 'small',
          onClick: () => editTag(record)
        }, [
          h(EditOutlined),
          ' 编辑'
        ]),
        // 删除按钮已移除，统一使用批量删除
      ]);
    }
  }
];

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  fetchTags();
});

// 获取标签列表
const fetchTags = async (params = {}) => {
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
    
    // 调用API获取标签列表
    const result = await tagApi.getTagList(requestParams);
    
    // 更新数据，根据API返回的数据结构进行适配
    if (result && result.code === 200) {
      // 处理分页数据结构
      if (result.data && result.data.records) {
        // 标准分页结构
        tags.value = result.data.records;
        pagination.value.total = result.data.total || 0;
        pagination.value.current = result.data.current || pagination.value.current;
        pagination.value.pageSize = result.data.size || pagination.value.pageSize;
      } else if (result.data && Array.isArray(result.data)) {
        // 数据直接是数组
        tags.value = result.data;
        pagination.value.total = result.total || tags.value.length || 0;
      } else {
        // 其他情况，尝试直接使用
        tags.value = [];
        pagination.value.total = 0;
      }
    } else {
      tags.value = [];
      pagination.value.total = 0;
    }
  } catch (error) {
    console.error('获取标签列表失败:', error);
    message.error('获取标签列表失败，请稍后重试');
    tags.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

// 处理表格变化（分页、排序等）
const handleTableChange = (pag, filters, sorter) => {
  fetchTags({
    pageSize: pag.pageSize,
    current: pag.current,
    sortField: sorter.field,
    sortOrder: sorter.order
  });
};

// 打开添加标签模态框
const showAddModal = () => {
  isEdit.value = false;
  modalTitle.value = '添加标签';
  formState.id = null;
  formState.name = '';
  modalVisible.value = true;
};

// 编辑标签
const editTag = (record) => {
  isEdit.value = true;
  modalTitle.value = '编辑标签';
  formState.id = record.id;
  formState.name = record.name;
  modalVisible.value = true;
};

// 删除标签
const deleteTag = async (id) => {
  try {
    // 调用API删除标签
    await tagApi.deleteTag(id);
    message.success('标签删除成功');
    fetchTags();
  } catch (error) {
    console.error('删除标签失败:', error);
    message.error('删除标签失败，请稍后重试');
  }
};

// 提交表单
const handleSubmit = () => {
  formRef.value.validate().then(async () => {
    try {
      if (isEdit.value) {
        // 更新标签
        await tagApi.updateTag(formState.id, {
          name: formState.name
        });
        message.success('标签更新成功');
      } else {
        // 添加标签
        await tagApi.addTag({
          name: formState.name
        });
        message.success('标签添加成功');
      }
      modalVisible.value = false;
      fetchTags();
    } catch (error) {
      console.error(isEdit.value ? '更新标签失败:' : '添加标签失败:', error);
      message.error(isEdit.value ? '更新标签失败，请稍后重试' : '添加标签失败，请稍后重试');
    }
  });
};

// 取消表单
const handleCancel = () => {
  modalVisible.value = false;
};
</script>

<template>
  <div class="tag-manage-container">
    <Card title="标签管理" :bordered="false">
      <!-- 查询表单 -->
      <Form layout="inline" :model="queryParams" class="search-form">
        <Form.Item label="关键词">
          <Input
            v-model:value="queryParams.keyword"
            placeholder="请输入标签名称"
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
          <Button type="primary" @click="showAddModal">
            <PlusOutlined /> 添加标签
          </Button>
          <Popconfirm
            title="确定要删除选中的标签吗？"
            @confirm="batchDeleteTags"
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
        :dataSource="tags"
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
    
    <!-- 添加/编辑标签模态框 -->
    <Modal
      :title="modalTitle"
      :visible="modalVisible"
      @cancel="handleCancel"
      :footer="null"
    >
      <Form
        ref="formRef"
        :model="formState"
        layout="vertical"
      >
        <Form.Item
          name="name"
          label="标签名称"
          :rules="[{ required: true, message: '请输入标签名称' }]"
        >
          <Input v-model:value="formState.name" placeholder="请输入标签名称" />
        </Form.Item>
        
        <Form.Item>
          <Button type="primary" @click="handleSubmit">确定</Button>
          <Button style="margin-left: 10px" @click="handleCancel">取消</Button>
        </Form.Item>
      </Form>
    </Modal>
  </div>
</template>

<style scoped>
.tag-manage-container {
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