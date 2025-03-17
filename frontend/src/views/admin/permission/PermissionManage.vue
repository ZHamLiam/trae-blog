<template>
  <div class="permission-manage-container">
    <a-card title="权限管理" :bordered="false">
      <!-- 搜索表单 -->
      <a-form layout="inline" class="search-form">
        <a-form-item label="关键词">
          <a-input
            v-model:value="searchKeyword"
            placeholder="请输入权限名称或编码"
            allow-clear
            style="width: 250px"
            @press-enter="handleSearch"
          />
        </a-form-item>
        
        <a-form-item label="权限类型">
          <a-select
            v-model:value="searchType"
            style="width: 150px"
            placeholder="权限类型"
            allow-clear
          >
            <a-select-option :value="null">全部类型</a-select-option>
            <a-select-option :value="1">菜单</a-select-option>
            <a-select-option :value="2">按钮</a-select-option>
            <a-select-option :value="3">接口</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template> 查询
            </a-button>
            <a-button @click="handleReset">
              <template #icon><ReloadOutlined /></template> 重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
      
      <!-- 操作按钮 -->
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><PlusOutlined /></template> 添加权限
          </a-button>
          <a-button @click="showPermissionTree">
            <template #icon><ApartmentOutlined /></template> 查看权限树
          </a-button>
          <a-button type="primary" danger :disabled="selectedRowKeys.length === 0" @click="handleBatchDelete">
            <template #icon><DeleteOutlined /></template> 批量删除
          </a-button>
        </a-space>
      </div>

      <!-- 权限表格 -->
      <a-table
        :columns="columns"
        :data-source="permissionList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{
          selectedRowKeys: selectedRowKeys,
          onChange: (keys, rows) => {
            selectedRowKeys = keys;
            selectedRows.value = rows;
          }
        }"
        @change="handleTableChange"
        rowKey="id"
      >
        <!-- 类型列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag :color="getTypeColor(record.type)">
              {{ getTypeName(record.type) }}
            </a-tag>
          </template>

          <!-- 状态列 -->
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '正常' : '禁用' }}
            </a-tag>
          </template>

          <!-- 操作列 -->
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" @click="handleEdit(record)">
                编辑
              </a-button>
              <!-- 删除单个按钮已移除，改为批量删除 -->
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 权限表单对话框 -->
      <a-modal
        v-model:visible="modalVisible"
        :title="modalTitle"
        @ok="handleModalOk"
        @cancel="handleModalCancel"
      >
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 20 }"
        >
          <a-form-item label="权限名称" name="name">
            <a-input v-model:value="formState.name" placeholder="请输入权限名称" />
          </a-form-item>
          <a-form-item label="权限编码" name="code">
            <a-input v-model:value="formState.code" placeholder="请输入权限编码" />
          </a-form-item>
          <a-form-item label="权限类型" name="type">
            <a-select v-model:value="formState.type" placeholder="请选择权限类型">
              <a-select-option :value="1">菜单</a-select-option>
              <a-select-option :value="2">按钮</a-select-option>
              <a-select-option :value="3">接口</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="父权限" name="parentId">
            <a-tree-select
              v-model:value="formState.parentId"
              :treeNodeLabelProp="title"
              :tree-data="permissionTreeData"
              placeholder="请选择父权限，不选则为顶级权限"
              :tree-default-expand-all="false"
              :replaceFields="{ children: 'children', label: 'name', key: 'id', value: 'id' }"
              allow-clear
            />
          </a-form-item>
          <a-form-item label="权限路径" name="path" v-if="formState.type === 1 || formState.type === 3">
            <a-input v-model:value="formState.path" placeholder="请输入权限路径" />
          </a-form-item>
          <a-form-item label="组件路径" name="component" v-if="formState.type === 1">
            <a-input v-model:value="formState.component" placeholder="请输入组件路径" />
          </a-form-item>
          <a-form-item label="图标" name="icon" v-if="formState.type === 1">
            <a-input v-model:value="formState.icon" placeholder="请输入图标名称" />
          </a-form-item>
          <a-form-item label="排序值" name="sort">
            <a-input-number
              v-model:value="formState.sort"
              :min="0"
              :max="999"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="状态" name="status">
            <a-radio-group v-model:value="formState.status">
              <a-radio :value="1">正常</a-radio>
              <a-radio :value="0">禁用</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- 权限树展示对话框 -->
      <a-modal
        v-model:visible="treeModalVisible"
        title="权限树结构"
        width="600px"
        :footer="null"
      >
        <a-tree
          :tree-data="permissionTree"
          :defaultExpandAll="true"
          :replaceFields="{ children: 'children', title: 'name', key: 'id' }"
        />
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, getCurrentInstance } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { PlusOutlined, ApartmentOutlined, SearchOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import permissionApi from '@/api/permission';

// 获取当前实例
const { proxy } = getCurrentInstance();

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '权限名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
  },
  {
    title: '权限编码',
    dataIndex: 'code',
    key: 'code',
    ellipsis: true,
  },
  {
    title: '权限路径',
    dataIndex: 'path',
    key: 'path',
    ellipsis: true,
  },
  {
    title: '权限类型',
    dataIndex: 'type',
    key: 'type',
    width: 100,
  },
  {
    title: '排序',
    dataIndex: 'sort',
    key: 'sort',
    width: 80,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    customRender: ({ text }) => {
      return text ? proxy.$formatDate(text) : '';
    }
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
  },
];

// 状态变量
const permissionList = ref([]);
const loading = ref(false);
const searchKeyword = ref('');
const searchType = ref(null);
// 选中的权限ID列表
const selectedRowKeys = ref([]);
const selectedRows = ref([]);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
});

// 表单相关
const formRef = ref(null);
const modalVisible = ref(false);
const modalTitle = ref('添加权限');
const formState = reactive({
  id: null,
  name: '',
  code: '',
  type: 1,
  parentId: 0,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: 1,
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { max: 50, message: '权限名称不能超过50个字符', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { max: 50, message: '权限编码不能超过50个字符', trigger: 'blur' },
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' },
  ],
  path: [
    { max: 200, message: '权限路径不能超过200个字符', trigger: 'blur' },
  ],
  component: [
    { max: 200, message: '组件路径不能超过200个字符', trigger: 'blur' },
  ],
  icon: [
    { max: 50, message: '图标名称不能超过50个字符', trigger: 'blur' },
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' },
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' },
  ],
};

// 权限树相关
const permissionTree = ref([]);
const permissionTreeData = ref([]);
const treeModalVisible = ref(false);

// 获取权限列表
const fetchPermissionList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.current,
      size: pagination.pageSize,
      keyword: searchKeyword.value,
      type: searchType.value,
    };
    const res = await permissionApi.getPermissionList(params);
    permissionList.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    console.error('获取权限列表失败', error);
    message.error('获取权限列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取权限树
const fetchPermissionTree = async () => {
  try {
     const res = await permissionApi.getPermissionTree();
    
    // 处理权限树数据，添加value属性
    const processTreeData = (data) => {
      if (!data) return [];
      return data.map(item => {
        const newItem = { ...item, value: item.id };
        if (item.children && item.children.length > 0) {
          newItem.children = processTreeData(item.children);
        }
        return newItem;
      });
    };
    
    // 处理原始权限树数据
    const processedData = processTreeData(res.data);
    permissionTree.value = processedData;
    
    // 构建权限树选择数据
    const treeData = [{ id: 0, name: '顶级权限', value: 0, children: [] }];
    treeData[0].children = processedData;
    permissionTreeData.value = treeData;
    console.log(permissionTreeData.value);
  } catch (error) {
    console.error('获取权限树失败', error);
    message.error('获取权限树失败');
  }
};

// 表格变化处理
const handleTableChange = (pag) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchPermissionList();
};

// 搜索处理
const handleSearch = () => {
  pagination.current = 1;
  fetchPermissionList();
};

// 重置搜索
const handleReset = () => {
  searchKeyword.value = '';
  searchType.value = null;
  pagination.current = 1;
  fetchPermissionList();
};

// 添加权限
const handleAdd = () => {
  resetForm();
  modalTitle.value = '添加权限';
  modalVisible.value = true;
};

// 编辑权限
const handleEdit = (record) => {
  resetForm();
  modalTitle.value = '编辑权限';
  Object.assign(formState, record);
  modalVisible.value = true;
};

// 删除权限（已弃用单个删除，改为批量删除）
const handleDelete = async (record) => {
  try {
    await permissionApi.deletePermission(record.id);
    message.success('删除成功');
    fetchPermissionList();
    fetchPermissionTree(); // 刷新权限树
  } catch (error) {
    console.error('删除权限失败', error);
    message.error('删除权限失败');
  }
};

// 批量删除权限
const handleBatchDelete = () => {
  console.log(selectedRowKeys.value);
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的权限');
    return;
  }
  
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个权限吗？删除后不可恢复！`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        loading.value = true;
        // 逐个删除选中的权限
        for (const id of selectedRowKeys.value) {
          await permissionApi.deletePermission(id);
        }
        message.success('批量删除权限成功');
        // 清空选中项
        selectedRowKeys.value = [];
        selectedRows.value = [];
        // 刷新数据
        fetchPermissionList();
        fetchPermissionTree();
      } catch (error) {
        console.error('批量删除权限失败', error);
        message.error('批量删除权限失败');
      } finally {
        loading.value = false;
      }
    },
  });
};

// 表单确认
const handleModalOk = async () => {
  try {
    // 表单验证
    await formRef.value.validate();
    
    // 根据是否有ID判断是添加还是编辑
    if (formState.id) {
      // 编辑权限
      await permissionApi.updatePermission(formState.id, formState);
      message.success('更新权限成功');
    } else {
      // 添加权限
      await permissionApi.createPermission(formState);
      message.success('添加权限成功');
    }
    
    // 关闭对话框
    modalVisible.value = false;
    
    // 刷新数据
    fetchPermissionList();
    fetchPermissionTree();
  } catch (error) {
    console.error('保存权限失败', error);
    message.error('保存权限失败：' + (error.message || '未知错误'));
  }
};

// 取消对话框
const handleModalCancel = () => {
  modalVisible.value = false;
  resetForm();
};

// 重置表单
const resetForm = () => {
  formState.id = null;
  formState.name = '';
  formState.code = '';
  formState.type = 1;
  formState.parentId = 0;
  formState.path = '';
  formState.component = '';
  formState.icon = '';
  formState.sort = 0;
  formState.status = 1;
  
  // 如果表单已经创建，则重置验证
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 获取权限类型名称
const getTypeName = (type) => {
  switch (type) {
    case 1: return '菜单';
    case 2: return '按钮';
    case 3: return '接口';
    default: return '未知';
  }
};

// 获取权限类型颜色
const getTypeColor = (type) => {
  switch (type) {
    case 1: return 'blue';
    case 2: return 'green';
    case 3: return 'orange';
    default: return 'default';
  }
};

// 显示权限树
const showPermissionTree = () => {
  treeModalVisible.value = true;
};

// 监听权限类型变化，重置相关字段
watch(() => formState.type, (newType) => {
  if (newType !== 1) {
    formState.component = '';
    formState.icon = '';
  }
  
  if (newType !== 1 && newType !== 3) {
    formState.path = '';
  }
});

// 初始化
onMounted(() => {
  fetchPermissionList();
  fetchPermissionTree();
});
</script>

<style scoped>
.permission-manage-container {
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