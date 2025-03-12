<script setup>
import { ref, onMounted, h, getCurrentInstance } from 'vue';
import { Table, Card, Button, Space, Popconfirm, message, Tag } from 'ant-design-vue';
import { EditOutlined, DeleteOutlined, LockOutlined, UnlockOutlined } from '@ant-design/icons-vue';
import userApi from '../../../api/user';

// 获取当前实例
const { proxy } = getCurrentInstance();

// 用户列表数据
const users = ref([]);
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
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
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
        color: text === 'active' ? 'green' : 'red'
      }, text === 'active' ? '正常' : '禁用');
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
      
      if (record.status === 'active') {
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
    ...filters
  });
};

// 编辑用户
const editUser = (id) => {
  // 实际项目中应该跳转到用户编辑页面或打开编辑模态框
  message.info(`编辑用户ID: ${id}`);
};

// 禁用用户
const lockUser = (id) => {
  // 实际项目中应该调用API禁用用户
  message.success('用户已禁用');
  fetchUsers();
};

// 启用用户
const unlockUser = (id) => {
  // 实际项目中应该调用API启用用户
  message.success('用户已启用');
  fetchUsers();
};

// 删除用户
const deleteUser = (id) => {
  // 实际项目中应该调用API删除用户
  message.success('用户删除成功');
  fetchUsers();
};
</script>

<template>
  <div class="user-manage-container">
    <Card title="用户管理" :bordered="false">
      <Table
        :columns="columns"
        :dataSource="users"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        rowKey="id"
      />
    </Card>
  </div>
</template>

<style scoped>
.user-manage-container {
  width: 100%;
}
</style>