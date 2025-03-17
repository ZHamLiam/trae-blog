<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { Form, Input, Button, Card, message } from 'ant-design-vue';
import { UserOutlined, LockOutlined, MailOutlined, UserAddOutlined } from '@ant-design/icons-vue';
import userApi from '@/api/user';

const router = useRouter();

// 表单数据
const formState = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
});

// 加载状态
const loading = ref(false);

// 表单规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 验证确认密码
function validateConfirmPassword(rule, value) {
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
}

// 提交表单
const onFinish = async (values) => {
  loading.value = true;
  
  try {
    // 调用注册API
    const result = await userApi.register({
      username: values.username,
      nickname: values.nickname,
      email: values.email,
      password: values.password
    });
    
    if (result && result.code === 200) {
      message.success('注册成功，请登录');
      router.push('/login');
    } else {
      message.error(result.message || '注册失败，请稍后重试');
    }
  } catch (error) {
    console.error('注册失败:', error);
    message.error('注册失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 跳转到登录页
const goToLogin = () => {
  router.push('/login');
};
</script>

<template>
  <div class="register-container">
    <Card class="register-card" title="注册 Trae Blog">
      <Form
        :model="formState"
        :rules="rules"
        @finish="onFinish"
        layout="vertical"
      >
        <Form.Item name="username">
          <Input 
            v-model:value="formState.username" 
            placeholder="用户名" 
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </Input>
        </Form.Item>
        
        <Form.Item name="nickname">
          <Input 
            v-model:value="formState.nickname" 
            placeholder="昵称" 
            size="large"
          >
            <template #prefix>
              <UserAddOutlined />
            </template>
          </Input>
        </Form.Item>
        
        <Form.Item name="email">
          <Input 
            v-model:value="formState.email" 
            placeholder="邮箱" 
            size="large"
          >
            <template #prefix>
              <MailOutlined />
            </template>
          </Input>
        </Form.Item>
        
        <Form.Item name="password">
          <Input.Password 
            v-model:value="formState.password" 
            placeholder="密码" 
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </Input.Password>
        </Form.Item>
        
        <Form.Item name="confirmPassword">
          <Input.Password 
            v-model:value="formState.confirmPassword" 
            placeholder="确认密码" 
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </Input.Password>
        </Form.Item>
        
        <Form.Item>
          <Button 
            type="primary" 
            html-type="submit" 
            :loading="loading" 
            block
            size="large"
          >
            注册
          </Button>
        </Form.Item>
        
        <div class="login-link">
          已有账号？ <a @click="goToLogin">立即登录</a>
        </div>
      </Form>
    </Card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.register-card {
  width: 400px;
}

.login-link {
  text-align: center;
  margin-top: 16px;
}
</style>