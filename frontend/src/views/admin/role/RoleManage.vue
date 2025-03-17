<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { PlusOutlined, SearchOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import roleApi from '@/api/role';
import permissionApi from '@/api/permission';

// 获取当前实例
const { proxy } = getCurrentInstance();

// 角色列表数据
const roleList = ref([]);
const total = ref(0);
const loading = ref(false);
const searchKeyword = ref('');
// 选中的角色ID列表
const selectedRowKeys = ref([]);
const selectedRows = ref([]);

// 分页参数
const pagination = ref({
  current: 1,
  pageSize: 10,
});

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '角色名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
  },
  {
    title: '角色编码',
    dataIndex: 'code',
    key: 'code',
    ellipsis: true,
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
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
    customRender: ({ text }) => {
      return text === 1 ? '正常' : '禁用';
    },
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
    width: 280,
    fixed: 'right',
  },
];

// 获取角色列表
const getRoleList = async () => {
  loading.value = true;
  try {
    const res = await roleApi.getRoleList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      keyword: searchKeyword.value,
    });
    if (res.code === 200) {
      roleList.value = res.data.records;
      total.value = res.data.total;
    } else {
      message.error(res.message || '获取角色列表失败');
    }
  } catch (error) {
    console.error('获取角色列表出错:', error);
    message.error('获取角色列表失败');
  } finally {
    loading.value = false;
  }
};

// 表格变化事件处理
const handleTableChange = (pag) => {
  pagination.value.current = pag.current;
  pagination.value.pageSize = pag.pageSize;
  getRoleList();
};

// 搜索角色
const handleSearch = () => {
  pagination.value.current = 1;
  getRoleList();
};

// 重置搜索
const handleReset = () => {
  searchKeyword.value = '';
  pagination.value.current = 1;
  getRoleList();
};

// 角色表单数据
const roleForm = ref({
  id: null,
  name: '',
  code: '',
  description: '',
  sort: 0,
  status: 1,
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { max: 50, message: '角色名称不能超过50个字符', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { max: 50, message: '角色编码不能超过50个字符', trigger: 'blur' },
  ],
  description: [
    { max: 200, message: '角色描述不能超过200个字符', trigger: 'blur' },
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' },
    { type: 'number', message: '排序值必须为数字', trigger: 'blur' },
  ],
};

// 对话框相关
const dialogVisible = ref(false);
const dialogTitle = ref('添加角色');
const formRef = ref(null);

// 打开添加角色对话框
const handleAdd = () => {
  roleForm.value = {
    id: null,
    name: '',
    code: '',
    description: '',
    sort: 0,
    status: 1,
  };
  dialogTitle.value = '添加角色';
  dialogVisible.value = true;
};

// 打开编辑角色对话框
const handleEdit = (record) => {
  roleForm.value = { ...record };
  dialogTitle.value = '编辑角色';
  dialogVisible.value = true;
};

// 提交角色表单
const submitForm = async () => {
  try {
    await formRef.value.validate();
    loading.value = true;
    
    const isEdit = roleForm.value.id !== null;
    let res;
    
    if (isEdit) {
      res = await roleApi.updateRole(roleForm.value.id, roleForm.value);
    } else {
      res = await roleApi.createRole(roleForm.value);
    }
    
    if (res.code === 200) {
      message.success(isEdit ? '编辑角色成功' : '添加角色成功');
      dialogVisible.value = false;
      getRoleList();
    } else {
      message.error(res.message || (isEdit ? '编辑角色失败' : '添加角色失败'));
    }
  } catch (error) {
    console.error(isEdit ? '编辑角色出错:' : '添加角色出错:', error);
    message.error(isEdit ? '编辑角色失败' : '添加角色失败');
  } finally {
    loading.value = false;
  }
};

// 删除角色（已弃用单个删除，改为批量删除）
const handleDelete = (id) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个角色吗？删除后不可恢复！',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        loading.value = true;
        const res = await roleApi.deleteRole(id);
        if (res.code === 200) {
          message.success('删除角色成功');
          getRoleList();
        } else {
          message.error(res.message || '删除角色失败');
        }
      } catch (error) {
        console.error('删除角色出错:', error);
        message.error('删除角色失败');
      } finally {
        loading.value = false;
      }
    },
  });
};

// 批量删除角色
const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的角色');
    return;
  }
  
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个角色吗？删除后不可恢复！`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        loading.value = true;
        // 逐个删除选中的角色
        for (const id of selectedRowKeys.value) {
          await roleApi.deleteRole(id);
        }
        message.success('批量删除角色成功');
        // 清空选中项
        selectedRowKeys.value = [];
        selectedRows.value = [];
        // 刷新数据
        getRoleList();
      } catch (error) {
        console.error('批量删除角色出错:', error);
        message.error('批量删除角色失败');
      } finally {
        loading.value = false;
      }
    },
  });
};

// 权限分配相关
const permissionDialogVisible = ref(false);
const currentRoleId = ref(null);
const permissionTree = ref([]);
const checkedKeys = ref([]);
const expandedKeys = ref([]);

// 处理权限树选择事件
const handlePermissionCheck = (checked, info) => {
  checkedKeys.value = checked;
  // 当选中或取消选中节点时，自动展开或收起节点
  if (info.node.children && info.node.children.length > 0) {
    const nodeKey = info.node.key;
    if (info.checked) {
      // 如果选中，则展开节点
      if (!expandedKeys.value.includes(nodeKey)) {
        expandedKeys.value = [...expandedKeys.value, nodeKey];
      }
    } else {
      // 如果取消选中，可以选择保留展开状态
      // 这里不做处理，保持用户手动展开/收起的状态
    }
  }
};

// 打开权限分配对话框
const handleAssignPermission = async (id) => {
  currentRoleId.value = id;
  permissionDialogVisible.value = true;
  
  try {
    loading.value = true;
    // 获取权限树
    const treeRes = await permissionApi.getPermissionTree();
    if (treeRes.code === 200) {
      permissionTree.value = treeRes.data;
      // 获取角色已有权限
      const permissionRes = await roleApi.getRolePermissionIds(id);
      if (permissionRes.code === 200) {
        checkedKeys.value = permissionRes.data;
        // 初始化展开的节点，包括所有父节点
        const parentIds = [];
        // 提取所有父节点ID
        const findParentIds = (tree) => {
          tree.forEach(node => {
            if (node.children && node.children.length > 0) {
              parentIds.push(node.id);
              findParentIds(node.children);
            }
          });
        };
        findParentIds(treeRes.data);
        expandedKeys.value = [...new Set([...parentIds])];
      }
    }
  } catch (error) {
    console.error('获取权限数据出错:', error);
    message.error('获取权限数据失败');
  } finally {
    loading.value = false;
  }
};

// 提交权限分配
const submitPermissions = async () => {
  try {
    loading.value = true;
    const res = await roleApi.assignPermissions(currentRoleId.value, {
      permissionIds: checkedKeys.value,
    });
    if (res.code === 200) {
      message.success('分配权限成功');
      permissionDialogVisible.value = false;
    } else {
      message.error(res.message || '分配权限失败');
    }
  } catch (error) {
    console.error('分配权限出错:', error);
    message.error('分配权限失败');
  } finally {
    loading.value = false;
  }
};

// 禁用角色
const handleDisable = async (id) => {
  try {
    loading.value = true;
    const res = await roleApi.disableRole(id);
    if (res.code === 200) {
      message.success('禁用角色成功');
      getRoleList();
    } else {
      message.error(res.message || '禁用角色失败');
    }
  } catch (error) {
    console.error('禁用角色出错:', error);
    message.error('禁用角色失败');
  } finally {
    loading.value = false;
  }
};

// 启用角色
const handleEnable = async (id) => {
  try {
    loading.value = true;
    const res = await roleApi.enableRole(id);
    if (res.code === 200) {
      message.success('启用角色成功');
      getRoleList();
    } else {
      message.error(res.message || '启用角色失败');
    }
  } catch (error) {
    console.error('启用角色出错:', error);
    message.error('启用角色失败');
  } finally {
    loading.value = false;
  }
};

// 生命周期钩子
onMounted(() => {
  getRoleList();
});
</script>

<template>
  <div class="role-manage-container">
    <a-card title="角色管理" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchKeyword" class="search-form">
          <a-form-item label="关键词">
            <a-input
              v-model:value="searchKeyword"
              placeholder="角色名称/编码/描述"
              allow-clear
              style="width: 200px"
              @press-enter="handleSearch"
            />
          </a-form-item>
          
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">
                <template #icon><search-outlined /></template> 查询
              </a-button>
              <a-button @click="handleReset">
                <template #icon><reload-outlined /></template> 重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      
      <!-- 操作按钮 -->
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><plus-outlined /></template> 添加角色
          </a-button>
          <a-button type="primary" danger :disabled="selectedRowKeys.length === 0" @click="handleBatchDelete">
            <template #icon><delete-outlined /></template> 批量删除
          </a-button>
        </a-space>
      </div>
      
      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="roleList"
        :loading="loading"
        :pagination="{
          ...pagination,
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条记录`,
        }"
        :row-selection="{
          selectedRowKeys: selectedRowKeys,
          onChange: (keys, rows) => {
            selectedRowKeys = keys;
            selectedRows.value = rows;
          }
        }"
        @change="handleTableChange"
        row-key="id"
        bordered
      >
        <!-- 状态列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '正常' : '禁用' }}
            </a-tag>
          </template>
          
          <!-- 操作列 -->
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleEdit(record)">
              编辑
            </a-button>
            <a-divider type="vertical" />
            <a-button type="link" size="small" @click="handleAssignPermission(record.id)">
              分配权限
            </a-button>
            <a-divider type="vertical" />
            <template v-if="record.status === 1">
              <a-button type="link" size="small" danger @click="handleDisable(record.id)">
                禁用
              </a-button>
            </template>
            <template v-else>
              <a-button type="link" size="small" @click="handleEnable(record.id)">
                启用
              </a-button>
            </template>
            <!-- 删除单个按钮已移除，改为批量删除 -->
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 角色表单对话框 -->
    <a-modal
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @ok="submitForm"
    >
      <a-form
        ref="formRef"
        :model="roleForm"
        :rules="rules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-item label="角色名称" name="name">
          <a-input v-model:value="roleForm.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="角色编码" name="code">
          <a-input v-model:value="roleForm.code" placeholder="请输入角色编码" />
        </a-form-item>
        <a-form-item label="角色描述" name="description">
          <a-textarea
            v-model:value="roleForm.description"
            placeholder="请输入角色描述"
            :rows="3"
          />
        </a-form-item>
        <a-form-item label="排序值" name="sort">
          <a-input-number
            v-model:value="roleForm.sort"
            placeholder="请输入排序值"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="roleForm.status">
            <a-radio :value="1">正常</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 权限分配对话框 -->
    <a-modal
      v-model:visible="permissionDialogVisible"
      :title="'权限分配 - ' + currentRoleId"
      width="600px"
      @ok="submitPermissions"
    >
      <a-tree
        :tree-data="permissionTree"
        :block-node="true"
        :checkable="true"
        :checked-keys="checkedKeys"
        :expanded-keys="expandedKeys"
        :replaceFields="{ children: 'children', title: 'name', key: 'id' }"
        :check-strictly="false"
        :auto-expand-parent="true"
        @check="handlePermissionCheck"
        @expand="(expanded) => expandedKeys = expanded"
      />
    </a-modal>
  </div>
</template>

<style scoped>
.role-manage-container {
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