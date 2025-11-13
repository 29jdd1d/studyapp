<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>考研学习管理系统</h2>
          <p>系统后台管理</p>
        </div>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-tips">
        <p>默认账号: admin / admin123</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../../api/auth'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // Since the backend may not have a separate admin login endpoint yet,
        // we'll simulate it for now or use a workaround
        // For demonstration, we'll accept admin/admin123
        if (loginForm.username === 'admin' && loginForm.password === 'admin123') {
          const token = 'demo_admin_token_' + Date.now()
          localStorage.setItem('admin_token', token)
          localStorage.setItem('admin_username', loginForm.username)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error('用户名或密码错误')
        }
        
        // Uncomment this when backend admin login is ready:
        // const response = await login(loginForm.username, loginForm.password)
        // if (response.data && response.data.token) {
        //   localStorage.setItem('admin_token', response.data.token)
        //   localStorage.setItem('admin_username', loginForm.username)
        //   ElMessage.success('登录成功')
        //   router.push('/')
        // }
      } catch (error) {
        console.error('Login error:', error)
        ElMessage.error('登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 450px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 24px;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-tips {
  text-align: center;
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.login-tips p {
  margin: 5px 0;
}
</style>
