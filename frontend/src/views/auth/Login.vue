<script setup>
import { ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Form, Input, Button, Checkbox, Card, message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import userApi from '@/api/user';

const router = useRouter();
const route = useRoute();

// 表单数据
const formState = reactive({
  username: '',
  password: '',
  remember: true
});

// 加载状态
const loading = ref(false);

// 表单规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ]
};

// 提交表单
const onFinish = async (values) => {
  loading.value = true;
  
  try {
    // 调用登录API
    const res = await userApi.login({
      username: values.username,
      password: values.password
    });
    
    // 保存token
    localStorage.setItem('token', res.token);
    
    // 登录成功后跳转
    const redirect = route.query.redirect || '/';
    router.push(redirect);
    
    message.success('登录成功');
  } catch (error) {
    console.error('登录失败:', error);
    message.error(error.message || '登录失败，请检查用户名和密码');
  } finally {
    loading.value = false;
  }
};

// 跳转到注册页
const goToRegister = () => {
  router.push('/register');
};
</script>

<template>
  <div class="login-container">
    <Card class="login-card" title="登录 Trae Blog">
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
        
        <Form.Item>
          <div class="login-options">
            <Checkbox v-model:checked="formState.remember">记住我</Checkbox>
            <a class="login-forgot">忘记密码</a>
          </div>
        </Form.Item>
        
        <Form.Item>
          <Button 
            type="primary" 
            html-type="submit" 
            :loading="loading" 
            block
            size="large"
          >
            登录
          </Button>
        </Form.Item>
        
        <div class="login-register">
          还没有账号？ <a @click="goToRegister">立即注册</a>
        </div>
      </Form>
    </Card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
}

.login-options {
  display: flex;
  justify-content: space-between;
}

.login-forgot {
  float: right;
}

.login-register {
  text-align: center;
  margin-top: 16px;
}
</style>