<script setup>
import { ref, reactive, onMounted } from 'vue';
import { message, Form, Input, Button, Upload, Avatar, Tabs, Card, Modal } from 'ant-design-vue';
import { UserOutlined, UploadOutlined, LockOutlined } from '@ant-design/icons-vue';
import userApi from '@/api/user';
import { getBase64 } from '@/utils/file';

// 表单实例
const formRef = ref(null);
const passwordFormRef = ref(null);

// 用户信息
const userInfo = ref({});

// 表单数据
const formState = reactive({
  nickname: '',
  email: '',
  avatar: ''
});

// 密码表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 加载状态
const loading = ref(false);
const passwordLoading = ref(false);

// 头像上传相关
const avatarUrl = ref('');
const previewVisible = ref(false);
const previewImage = ref('');

// 表单校验规则
const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度应为2-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
};

// 密码表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致');
        }
        return Promise.resolve();
      },
      trigger: 'blur'
    }
  ]
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    const res = await userApi.getUserInfo();
    userInfo.value = res;
    
    // 填充表单数据
    formState.nickname = res.nickname || '';
    formState.email = res.email || '';
    formState.avatar = res.avatar || '';
    
    // 设置头像预览
    if (res.avatar) {
      avatarUrl.value = res.avatar;
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    message.error('获取用户信息失败');
  }
};

// 更新用户信息
const updateUserInfo = async () => {
  try {
    await formRef.value.validate();
    
    loading.value = true;
    
    const updateData = {
      ...userInfo.value,
      nickname: formState.nickname,
      email: formState.email,
      avatar: formState.avatar
    };
    
    await userApi.updateUser(userInfo.value.id, updateData);
    message.success('个人信息更新成功');
    
    // 重新获取用户信息
    getUserInfo();
  } catch (error) {
    console.error('更新用户信息失败:', error);
    message.error(error.message || '更新用户信息失败');
  } finally {
    loading.value = false;
  }
};

// 修改密码
const changePassword = async () => {
  try {
    await passwordFormRef.value.validate();
    
    passwordLoading.value = true;
    
    await userApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    });
    
    message.success('密码修改成功');
    
    // 重置表单
    passwordFormRef.value.resetFields();
  } catch (error) {
    console.error('修改密码失败:', error);
    message.error(error.message || '修改密码失败，请检查当前密码是否正确');
  } finally {
    passwordLoading.value = false;
  }
};

// 头像上传前的校验
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('只能上传JPG/PNG格式的图片!');
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('图片大小不能超过2MB!');
  }
  return isJpgOrPng && isLt2M;
};

// 头像上传变化处理
const handleAvatarChange = (info) => {
  if (info.file.status === 'uploading') {
    return;
  }
  
  if (info.file.status === 'done') {
    // 获取上传后的URL
    if (info.file.response && info.file.response.data) {
      formState.avatar = info.file.response.data;
      avatarUrl.value = info.file.response.data;
    }
  } else if (info.file.status === 'error') {
    message.error('头像上传失败');
  }
};

// 自定义上传请求
const customRequest = async ({ file, onSuccess, onError }) => {
  try {
    // 将文件转为base64，用于预览
    getBase64(file, (url) => {
      avatarUrl.value = url;
    });
    
    // 创建FormData对象
    const formData = new FormData();
    formData.append('file', file);
    
    // 调用上传API
    const res = await userApi.uploadAvatar(formData);
    
    // 设置头像URL
    formState.avatar = res.data;
    
    onSuccess(res, file);
  } catch (error) {
    console.error('上传失败:', error);
    onError(error);
  }
};

// 预览头像
const handlePreview = () => {
  previewImage.value = avatarUrl.value;
  previewVisible.value = true;
};

// 关闭预览
const handleCancel = () => {
  previewVisible.value = false;
};

// 组件挂载时获取用户信息
onMounted(() => {
  getUserInfo();
});
</script>

<template>
  <div class="profile-container">
    <a-card title="个人中心" :bordered="false">
      <a-tabs default-active-key="1">
        <!-- 基本信息 -->
        <a-tab-pane key="1" tab="基本信息">
          <div class="profile-form">
            <div class="avatar-wrapper">
              <div class="avatar-title">头像</div>
              <div class="avatar-content">
                <a-avatar 
                  :src="avatarUrl" 
                  :size="100" 
                  @click="handlePreview"
                  class="avatar-image"
                >
                  <template #icon><user-outlined /></template>
                </a-avatar>
                <a-upload
                  name="avatar"
                  list-type="picture-card"
                  class="avatar-uploader"
                  :show-upload-list="false"
                  :before-upload="beforeUpload"
                  :custom-request="customRequest"
                  @change="handleAvatarChange"
                >
                  <div class="upload-button">
                    <upload-outlined />
                    <div class="ant-upload-text">更换头像</div>
                  </div>
                </a-upload>
              </div>
            </div>
            
            <a-form
              :model="formState"
              :rules="rules"
              ref="formRef"
              layout="vertical"
              class="info-form"
            >
              <a-form-item label="用户名" name="username">
                <a-input v-model:value="userInfo.username" disabled />
              </a-form-item>
              
              <a-form-item label="昵称" name="nickname">
                <a-input v-model:value="formState.nickname" placeholder="请输入昵称" />
              </a-form-item>
              
              <a-form-item label="邮箱" name="email">
                <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
              </a-form-item>
              
              <a-form-item>
                <a-button type="primary" @click="updateUserInfo" :loading="loading">
                  保存修改
                </a-button>
              </a-form-item>
            </a-form>
          </div>
        </a-tab-pane>
        
        <!-- 修改密码 -->
        <a-tab-pane key="2" tab="修改密码">
          <a-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            layout="vertical"
            class="password-form"
          >
            <a-form-item label="当前密码" name="oldPassword">
              <a-input-password 
                v-model:value="passwordForm.oldPassword" 
                placeholder="请输入当前密码"
              >
                <template #prefix>
                  <lock-outlined />
                </template>
              </a-input-password>
            </a-form-item>
            
            <a-form-item label="新密码" name="newPassword">
              <a-input-password 
                v-model:value="passwordForm.newPassword" 
                placeholder="请输入新密码"
              >
                <template #prefix>
                  <lock-outlined />
                </template>
              </a-input-password>
            </a-form-item>
            
            <a-form-item label="确认新密码" name="confirmPassword">
              <a-input-password 
                v-model:value="passwordForm.confirmPassword" 
                placeholder="请再次输入新密码"
              >
                <template #prefix>
                  <lock-outlined />
                </template>
              </a-input-password>
            </a-form-item>
            
            <a-form-item>
              <a-button type="primary" @click="changePassword" :loading="passwordLoading">
                修改密码
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
    
    <!-- 头像预览 -->
    <a-modal
      :visible="previewVisible"
      :footer="null"
      @cancel="handleCancel"
    >
      <img alt="头像预览" style="width: 100%" :src="previewImage" />
    </a-modal>
  </div>
</template>

<style scoped>
.profile-container {
  padding: 24px;
  background-color: #f0f2f5;
}

.avatar-wrapper {
  display: flex;
  margin-bottom: 24px;
  align-items: center;
}

.avatar-title {
  width: 80px;
  text-align: right;
  margin-right: 24px;
  color: rgba(0, 0, 0, 0.85);
}

.avatar-content {
  display: flex;
  align-items: center;
}

.avatar-image {
  margin-right: 16px;
  cursor: pointer;
}

.avatar-uploader {
  margin-left: 16px;
}

.upload-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.info-form, .password-form {
  max-width: 500px;
}
</style>