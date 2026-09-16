<template>
  <div class="page">
    <el-card class="card">
      <h2>miniboos 管理后台</h2>
      <el-form @submit.prevent="login">
        <el-form-item>
          <el-input v-model="phone" placeholder="管理员手机号" maxlength="11" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="login">登录</el-button>
      </el-form>
      <div class="tip">管理员账号由系统种子内置，不开放注册</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { auth } from '../api'

const router = useRouter()
const phone = ref('')
const password = ref('')
const loading = ref(false)

const login = async () => {
  loading.value = true
  try {
    const res = await auth.login({ phone: phone.value, password: password.value })
    if (res.data.role !== 'ADMIN') {
      ElMessage.error('该账号不是管理员')
      return
    }
    localStorage.setItem('admin_token', res.data.token)
    router.replace('/')
  } catch (e) {
    ElMessage.error(e.msg || e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page { height: 100vh; display: flex; align-items: center; justify-content: center;
  background: #f0f2f5; }
.card { width: 360px; }
.tip { color: #909399; font-size: 12px; text-align: center; margin-top: 12px; }
</style>
