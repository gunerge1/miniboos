<template>
  <div class="page">
    <div class="login-card">
      <div class="logo"><span class="mb-gradient-text">miniboos</span></div>
      <div class="sub">管理后台 · 平台治理中枢</div>
      <el-form @submit.prevent="login">
        <el-form-item>
          <el-input v-model="phone" placeholder="管理员手机号" maxlength="11" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="密码" show-password size="large" />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="login">登 录</el-button>
      </el-form>
      <div class="tip">管理员账号由系统种子内置，不开放注册</div>
    </div>
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
  background: var(--mb-gradient); position: relative; overflow: hidden; }
.page::before { content: ''; position: absolute; width: 400px; height: 400px; border-radius: 50%;
  background: rgba(255,255,255,.08); top: -120px; right: -80px; }
.page::after { content: ''; position: absolute; width: 260px; height: 260px; border-radius: 50%;
  background: rgba(255,255,255,.08); bottom: -60px; left: -40px; }
.login-card { width: 380px; background: #fff; border-radius: var(--mb-radius);
  box-shadow: 0 20px 60px rgba(0,0,0,.15); padding: 36px 32px 24px; position: relative; }
.logo { text-align: center; font-size: 30px; font-weight: 800; }
.sub { text-align: center; color: var(--mb-sub); font-size: 13px; margin: 6px 0 24px; }
.tip { color: #909399; font-size: 12px; text-align: center; margin-top: 14px; }
</style>
