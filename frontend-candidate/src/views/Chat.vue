<template>
  <div class="page">
    <van-nav-bar title="与HR沟通" left-arrow @click-left="$router.back()" class="mb-navbar" />
    <van-pull-refresh v-model="refreshing" @refresh="load" class="msg-area">
      <div class="bubble-wrap">
        <div v-for="m in messages" :key="m.id" :class="['bubble', m.senderRole === 'CANDIDATE' ? 'mine' : 'theirs']">
          <div class="sender">{{ m.senderName }}</div>
          <div class="text">{{ m.content }}</div>
          <div class="time">{{ m.createdAt?.slice(5, 16).replace('T', ' ') }}</div>
        </div>
      </div>
    </van-pull-refresh>
    <div class="input-bar">
      <van-field v-model="text" placeholder="输入留言（刷新可见新消息）" class="input" />
      <van-button type="primary" :loading="sending" @click="send">发送</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { showToast } from 'vant'
import { appApi } from '../api'

const route = useRoute()
const messages = ref([])
const text = ref('')
const sending = ref(false)
const refreshing = ref(false)
let timer = null

const load = async () => {
  try {
    const res = await appApi.messages(route.params.appId)
    messages.value = res.data
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    refreshing.value = false
  }
}

const send = async () => {
  if (!text.value.trim()) return
  sending.value = true
  try {
    await appApi.send(route.params.appId, text.value.trim())
    text.value = ''
    await load()
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  load()
  timer = setInterval(load, 10000) // 留言板模式：10秒轻轮询（PRD裁决）
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.msg-area { min-height: calc(100vh - 110px); }
.bubble-wrap { padding: 12px; }
.bubble { max-width: 78%; margin-bottom: 14px; }
.bubble.mine { margin-left: auto; }
.sender { font-size: 12px; color: var(--mb-sub); margin-bottom: 3px; }
.text { padding: 10px 14px; border-radius: 16px 16px 16px 4px; background: #fff;
  box-shadow: var(--mb-shadow); word-break: break-all; }
.mine .text { background: var(--mb-gradient); color: #fff; border-radius: 16px 16px 4px 16px; }
.time { font-size: 11px; color: #c8c9cc; margin-top: 3px; }
.input-bar { position: fixed; bottom: 0; left: 0; right: 0; display: flex; gap: 8px;
  padding: 8px 12px; background: #fff; box-shadow: 0 -2px 12px rgba(0,0,0,.06); }
.input { flex: 1; }
</style>
