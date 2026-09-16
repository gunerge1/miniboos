<template>
  <div class="page login-page">
    <!-- BOSS式极简登录：白底+青色品牌+一条青色顶线 -->
    <div class="brand-bar" />
    <div class="brand">
      <div class="identity-icon"><van-icon name="user-o" size="34" color="#00a6a7" /></div>
      <div class="logo mb-logo">miniboos</div>
      <div class="badge badge-c">牛人端</div>
      <div class="slogan">牛人找工作 · 先投递再开聊</div>
    </div>

    <div class="form-wrap">
      <van-form @submit="onSubmit">
        <van-field v-model="form.phone" label="手机号" placeholder="11位手机号" maxlength="11"
                   :rules="[{ required: true, message: '请填手机号' }, { pattern: /^1\d{10}$/, message: '手机号格式不对' }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="6-20位"
                   :rules="[{ required: true, message: '请填密码' }]" />
        <van-field v-model="form.nickname" label="昵称" placeholder="选填，别人这样叫你" />
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
      await auth.register({ ...form, role: 'CANDIDATE' })
      showToast('注册成功')
    }
    const res = await auth.login({ phone: form.phone, password: form.password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('role', res.data.role)
    localStorage.setItem('nickname', res.data.nickname)
    router.replace('/')
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { background: #fff; min-height: 100vh; }
.brand-bar { height: 4px; background: var(--mb-primary); }
.brand { text-align: center; padding: 48px 0 32px; }
.identity-icon { width: 72px; height: 72px; border-radius: 50%; background: #ecf8f8;
  display: flex; align-items: center; justify-content: center; margin: 0 auto 16px; }
.logo { font-size: 34px; font-weight: 800; }
.badge { display: inline-block; margin-top: 10px; padding: 3px 14px; border-radius: 12px;
  font-size: 12px; letter-spacing: 2px; }
.badge-c { background: var(--mb-primary); color: #fff; }
.slogan { color: var(--mb-sub); margin-top: 12px; font-size: 14px; }
.form-wrap { margin: 0 20px; background: #fff; }
.btns { margin: 24px 4px 8px; }
.gap { margin-top: 12px; }
</style>
