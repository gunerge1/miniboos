<template>
  <div class="page">
    <div class="logo">miniboos</div>
    <div class="slogan">牛人找工作，先投递再开聊</div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.phone" label="手机号" placeholder="11位手机号" maxlength="11"
                   :rules="[{ required: true, message: '请填手机号' }, { pattern: /^1\d{10}$/, message: '手机号格式不对' }]" />
        <van-field v-model="form.password" type="password" label="密码" placeholder="6-20位"
                   :rules="[{ required: true, message: '请填密码' }]" />
        <van-field v-model="form.nickname" label="昵称" placeholder="选填" />
      </van-cell-group>
      <div class="btns">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          {{ mode === 'login' ? '登录' : '注册并登录' }}
        </van-button>
        <van-button round block plain type="primary" @click="toggleMode" class="gap">
          {{ mode === 'login' ? '没有账号？去注册' : '已有账号？去登录' }}
        </van-button>
      </div>
    </van-form>
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
.page { padding: 60px 0 30px; }
.logo { text-align: center; font-size: 34px; font-weight: 700; color: #1989fa; }
.slogan { text-align: center; color: #969799; margin: 8px 0 40px; }
.btns { margin: 24px 16px; }
.gap { margin-top: 12px; }
</style>
