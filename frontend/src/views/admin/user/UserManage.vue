<script setup>
import { ref, reactive, onMounted, h, getCurrentInstance } from 'vue';
import { Table, Card, Button, Space, Popconfirm, message, Tag, Modal, Checkbox } from 'ant-design-vue';
import { EditOutlined, DeleteOutlined, LockOutlined, UnlockOutlined, TeamOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import userApi from '../../../api/user';
import roleApi from '@/api/role';

// 获取当前实例
const { proxy } = getCurrentInstance();

// 用户列表数据
const users = ref([]);
// 加载状态
const loading = ref(false);
// 搜索参数
const searchKeyword = ref('');
const searchStatus = ref(null);
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
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
    ellipsis: true,
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
    ellipsis: true,
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    ellipsis: true,
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
    customRender: ({ text }) => {
      return h(Tag, {
        color: text === 'admin' ? 'red' : 'blue'
      }, text === 'admin' ? '管理员' : '普通用户');
    }
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    customRender: ({ text }) => {
      return h(Tag, {
        color: text === 1 ? 'green' : 'red'
      }, text === 1 ? '正常' : '禁用');
    }
  },
  {
    title: '注册时间',
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
      const buttons = [
        h(Button, {
          type: 'primary',
          size: 'small',
          onClick: () => editUser(record.id)
        }, [
          h(EditOutlined),
          ' 编辑'
        ])
      ];
      
      if (record.status === 1) {
        buttons.push(
          h(Button, {
            type: 'primary',
            danger: true,
            size: 'small',
            onClick: () => lockUser(record.id)
          }, [
            h(LockOutlined),
            ' 禁用'
          ])
        );
      } else {
        buttons.push(
          h(Button, {
            type: 'primary',
            size: 'small',
            onClick: () => unlockUser(record.id)
          }, [
            h(UnlockOutlined),
            ' 启用'
          ])
        );
      }
      
      // 添加分配角色按钮
      buttons.push(
        h(Button, {
          type: 'primary',
          size: 'small',
          onClick: () => handleAssignRole(record.id)
        }, [
          h(TeamOutlined),
          ' 分配角色'
        ])
      );
      
      buttons.push(
        h(Popconfirm, {
          title: '确定要删除这个用户吗？',
          okText: '确定',
          cancelText: '取消',
          onConfirm: () => deleteUser(record.id)
        }, [
          h(Button, {
            type: 'primary',
            danger: true,
            size: 'small'
          }, [
            h(DeleteOutlined),
            ' 删除'
          ])
        ])
      );
      
      return h(Space, {}, buttons);
    }
  }
];

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  fetchUsers();
});

// 获取用户列表
const fetchUsers = async (params = {}) => {
  loading.value = true;
  
  try {
    // 合并分页参数，将current转换为page
    const queryParams = {
      page: params.current || pagination.value.current,
      size: params.pageSize || pagination.value.pageSize,
      ...params
    };
    delete queryParams.current; // 删除current参数
    delete queryParams.pageSize; // 删除pageSize参数，使用size
    
    // 调用API获取用户列表
    const result = await userApi.getUserList(queryParams);
    
    // 更新数据，根据API返回的数据结构进行适配
    if (result && result.code === 200) {
      // 处理分页数据结构
      if (result.data && result.data.records) {
        // 标准分页结构
        users.value = result.data.records;
        pagination.value.total = result.data.total || 0;
        pagination.value.current = result.data.current || pagination.value.current;
        pagination.value.pageSize = result.data.size || pagination.value.pageSize;
      } else if (result.data && Array.isArray(result.data)) {
        // 数据直接是数组
        users.value = result.data;
        pagination.value.total = result.total || users.value.length || 0;
      } else {
        // 其他情况，尝试直接使用
        users.value = [];
        pagination.value.total = 0;
      }
    } else {
      users.value = [];
      pagination.value.total = 0;
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    message.error('获取用户列表失败，请稍后重试');
    users.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

// 处理表格变化（分页、排序等）
const handleTableChange = (pag, filters, sorter) => {
  fetchUsers({
    pageSize: pag.pageSize,
    current: pag.current,
    sortField: sorter.field,
    sortOrder: sorter.order,
    keyword: searchKeyword.value,
    status: searchStatus.value,
    ...filters
  });
};

// 搜索处理
const handleSearch = () => {
  pagination.value.current = 1;
  fetchUsers({
    keyword: searchKeyword.value,
    status: searchStatus.value
  });
};

// 重置搜索
const handleReset = () => {
  searchKeyword.value = '';
  searchStatus.value = null;
  pagination.value.current = 1;
  fetchUsers();
};

// 编辑用户表单相关
const userFormRef = ref(null);
const userModalVisible = ref(false);
const userModalTitle = ref('编辑用户');
const userFormState = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  status: 'active'
});

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名不能超过50个字符', trigger: 'blur' },
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 50, message: '昵称不能超过50个字符', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' },
  ],
};

// 编辑用户
const editUser = async (id) => {
  // 重置表单
  resetUserForm();
  userModalTitle.value = '编辑用户';
  
  // 获取用户详情
  try {
    loading.value = true;
    const result = await userApi.getUserById(id);
    if (result && result.code === 200 && result.data) {
      // 填充表单数据
      Object.assign(userFormState, result.data);
      // 显示编辑对话框
      userModalVisible.value = true;
    } else {
      message.error('获取用户信息失败');
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    message.error('获取用户信息失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 重置用户表单
const resetUserForm = () => {
  userFormState.id = null;
  userFormState.username = '';
  userFormState.nickname = '';
  userFormState.email = '';
  userFormState.avatar = '';
  userFormState.status = 'active';
  
  // 如果表单已经创建，则重置验证
  if (userFormRef.value) {
    userFormRef.value.resetFields();
  }
};

// 保存用户信息
const handleUserModalOk = async () => {
  try {
    // 表单验证
    await userFormRef.value.validate();
    
    // 调用API更新用户信息
    await userApi.updateUser(userFormState.id, userFormState);
    message.success('更新用户成功');
    
    // 关闭对话框
    userModalVisible.value = false;
    
    // 刷新用户列表
    fetchUsers();
  } catch (error) {
    console.error('保存用户信息失败:', error);
    message.error('保存用户信息失败：' + (error.message || '未知错误'));
  }
};

// 取消用户编辑
const handleUserModalCancel = () => {
  userModalVisible.value = false;
  resetUserForm();
};

// 禁用用户
const lockUser = async (id) => {
  try {
    await userApi.disableUser(id);
    message.success('用户已禁用');
    fetchUsers();
  } catch (error) {
    console.error('禁用用户失败:', error);
    message.error('禁用用户失败，请稍后重试');
  }
};

// 启用用户
const unlockUser = async (id) => {
  try {
    await userApi.enableUser(id);
    message.success('用户已启用');
    fetchUsers();
  } catch (error) {
    console.error('启用用户失败:', error);
    message.error('启用用户失败，请稍后重试');
  }
};

// 删除用户
const deleteUser = async (id) => {
  try {
    await userApi.deleteUser(id);
    message.success('用户删除成功');
    fetchUsers();
  } catch (error) {
    console.error('删除用户失败:', error);
    message.error('删除用户失败，请稍后重试');
  }
};

// 角色分配相关
const roleDialogVisible = ref(false);
const currentUserId = ref(null);
const allRoles = ref([]);
const selectedRoleIds = ref([]);
const roleLoading = ref(false);

// 获取所有角色列表
const fetchAllRoles = async () => {
  try {
    roleLoading.value = true;
    const res = await roleApi.getAllRoles();
    if (res.code === 200) {
      allRoles.value = res.data;
    } else {
      message.error(res.message || '获取角色列表失败');
    }
  } catch (error) {
    console.error('获取角色列表失败:', error);
    message.error('获取角色列表失败，请稍后重试');
  } finally {
    roleLoading.value = false;
  }
};

// 打开角色分配对话框
const handleAssignRole = async (userId) => {
  currentUserId.value = userId;
  roleDialogVisible.value = true;
  
  // 获取所有角色
  await fetchAllRoles();
  
  // 获取用户当前角色
  try {
    roleLoading.value = true;
    const res = await userApi.getUserRoleIds(userId);
    if (res.code === 200) {
      selectedRoleIds.value = res.data;
    } else {
      selectedRoleIds.value = [];
      message.error(res.message || '获取用户角色失败');
    }
  } catch (error) {
    console.error('获取用户角色失败:', error);
    message.error('获取用户角色失败，请稍后重试');
    selectedRoleIds.value = [];
  } finally {
    roleLoading.value = false;
  }
};

// 角色选择变更
const handleRoleChange = (roleId, checked) => {
  if (checked) {
    // 添加角色
    if (!selectedRoleIds.value.includes(roleId)) {
      selectedRoleIds.value.push(roleId);
    }
  } else {
    // 移除角色
    selectedRoleIds.value = selectedRoleIds.value.filter(id => id !== roleId);
  }
};

// 保存用户角色分配
const saveUserRoles = async () => {
  try {
    roleLoading.value = true;
    const res = await userApi.assignRoles(currentUserId.value, {
      roleIds: selectedRoleIds.value
    });
    
    if (res.code === 200) {
      message.success('角色分配成功');
      roleDialogVisible.value = false;
      fetchUsers(); // 刷新用户列表
    } else {
      message.error(res.message || '角色分配失败');
    }
  } catch (error) {
    console.error('角色分配失败:', error);
    message.error('角色分配失败，请稍后重试');
  } finally {
    roleLoading.value = false;
  }
};
</script>

<template>
  <div class="user-manage-container">
    <Card title="用户管理" :bordered="false" class="admin-card">
      <!-- 搜索表单 -->
      <a-form layout="inline" class="search-form">
        <a-form-item label="关键词">
          <a-input
            v-model:value="searchKeyword"
            placeholder="请输入用户名、昵称或邮箱"
            allow-clear
            style="width: 250px"
            @press-enter="handleSearch"
          />
        </a-form-item>
        
        <a-form-item label="状态">
          <a-select
            v-model:value="searchStatus"
            style="width: 150px"
            placeholder="用户状态"
            allow-clear
          >
            <a-select-option :value="null">全部状态</a-select-option>
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
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
      
      <Table
        :columns="columns"
        :dataSource="users"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        rowKey="id"
        class="admin-table"
      />
    </Card>
    
    <!-- 角色分配对话框 -->
    <Modal
      v-model:visible="roleDialogVisible"
      title="分配用户角色"
      width="500px"
      :confirmLoading="roleLoading"
      @ok="saveUserRoles"
    >
      <div v-if="roleLoading" class="loading-container">
        <a-spin />
      </div>
      <div v-else class="role-checkbox-group">
        <a-checkbox-group v-model:value="selectedRoleIds">
          <div v-for="role in allRoles" :key="role.id" class="role-checkbox-item">
            <a-checkbox :value="role.id">
              {{ role.name }}
              <span class="role-code">({{ role.code }})</span>
            </a-checkbox>
          </div>
        </a-checkbox-group>
        <div v-if="allRoles.length === 0" class="no-roles">
          暂无可分配的角色
        </div>
      </div>
    </Modal>
    
    <!-- 用户编辑对话框 -->
    <Modal
      v-model:visible="userModalVisible"
      :title="userModalTitle"
      @ok="handleUserModalOk"
      @cancel="handleUserModalCancel"
    >
      <a-form
        ref="userFormRef"
        :model="userFormState"
        :rules="userRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
        class="admin-form"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="userFormState.username" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item label="昵称" name="nickname">
          <a-input v-model:value="userFormState.nickname" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="userFormState.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="头像" name="avatar">
          <a-input v-model:value="userFormState.avatar" placeholder="请输入头像URL" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="userFormState.status">
            <a-radio :value="1">正常</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </Modal>
  </div>
</template>

<style scoped>
.user-manage-container {
  width: 100%;
}

.search-form {
  margin-bottom: 16px;
  background-color: var(--component-bg);
  padding: 16px;
  border-radius: 4px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.role-checkbox-group {
  max-height: 300px;
  overflow-y: auto;
}

.role-checkbox-item {
  margin-bottom: 10px;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.role-checkbox-item:hover {
  background-color: #f5f5f5;
}

.role-code {
  color: #999;
  font-size: 12px;
  margin-left: 5px;
}

.no-roles {
  text-align: center;
  color: #999;
  padding: 20px 0;
}
</style>