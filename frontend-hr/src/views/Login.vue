<template>
  <div class="page login-bg">
    <div class="sky">
      <div class="cloud c1" /><div class="cloud c2" />
      <div class="logo">miniboos</div>
      <div class="slogan">招聘端 · 企业认证后开始招人</div>
    </div>
    <div class="login-card">
      <van-form @submit="onSubmit">
        <van-field v-model="form.phone" label="手机号" placeholder="11位手机号" maxlength="11"
                   :rules="[{ required: true, message: '请填手机号' }, { pattern: /^1\d{10}$/, message: '手机号格式不对' }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="6-20位"
                   :rules="[{ required: true, message: '请填密码' }]" />
        <van-field v-model="form.nickname" label="昵称" placeholder="选填" />
        <div class="btns">
          <van-button round block native-type="submit" :loading="loading" class="mb-btn">
            {{ mode === 'login' ? '登录' : '注册并登录' }}
          </van-button>
          <van-button round block plain type="primary" @click="toggleMode" class="gap">
            {{ mode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { auth } from '../api'

const router = useRouter()
const mode = ref('login')
const loading = ref(false)
const form = reactive({ phone: '', password: '', nickname: '' })

const toggleMode = () => { mode.value = mode.value === 'login' ? 'register' : 'login' }

const onSubmit = async () => {
  loading.value = true
  try {
    if (mode.value === 'register') {
      await auth.register({ ...form, role: 'HR' })
      showToast('注册成功，接下来请完成企业认证')
    }
    const res = await auth.login({ phone: form.phone, password: form.password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('role', res.data.role)
    router.replace('/')
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg { background: var(--mb-bg); }
.sky { position: relative; padding: 70px 0 50px; text-align: center;
  background: var(--mb-gradient); border-radius: 0 0 40px 40px; overflow: hidden; }
.sky .logo { color: #fff; font-size: 38px; font-weight: 800; letter-spacing: 1px; }
.slogan { color: rgba(255,255,255,.9); margin-top: 10px; letter-spacing: 2px; }
.cloud { position: absolute; border-radius: 50%; background: rgba(255,255,255,.15); }
.c1 { width: 140px; height: 140px; top: -50px; left: -30px; }
.c2 { width: 90px; height: 90px; bottom: -20px; right: 30px; }
.login-card { background: #fff; border-radius: var(--mb-radius); box-shadow: var(--mb-shadow);
  margin: -28px 20px 0; padding: 24px 8px 16px; position: relative; }
.btns { margin: 20px 12px 8px; }
.gap { margin-top: 12px; }
</style>
