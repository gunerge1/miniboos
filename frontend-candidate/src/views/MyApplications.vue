<template>
  <div class="page">
    <van-nav-bar title="我的投递" class="mb-navbar" />
    <van-pull-refresh v-model="refreshing" @refresh="load">
      <van-empty v-if="!loading && list.length === 0" description="还没有投递，去找个心仪职位吧" />
      <div v-for="a in list" :key="a.id" class="mb-card app-card" @click="$router.push('/chat/' + a.id)">
        <div class="row">
          <span class="title">{{ a.jobTitle }}</span>
          <van-tag :type="tagType(a.status)" round>{{ statusText(a.status) }}</van-tag>
        </div>
        <div class="sub"><span>🏢 {{ a.companyName }} · {{ a.createdAt?.slice(0, 10) }}</span><span class="go-chat">继续沟通 ›</span></div>
      </div>
    </van-pull-refresh>
    <van-tabbar route>
      <van-tabbar-item icon="search" to="/">职位</van-tabbar-item>
      <van-tabbar-item icon="notes-o" to="/my-applications">我的投递</van-tabbar-item>
      <van-tabbar-item icon="contact" to="/my-resume">我的简历</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { appApi } from '../api'

const list = ref([])
const loading = ref(true)
const refreshing = ref(false)

const STATUS = {
  SUBMITTED: ['待查看', 'default'],
  VIEWED: ['已查看', 'primary'],
  INTERVIEW: ['约面', 'warning'],
  OFFER: ['已发offer', 'success'],
  REJECTED: ['不合适', 'danger']
}
const statusText = s => (STATUS[s] || [s])[0]
const tagType = s => (STATUS[s] || [])[1] || 'default'

const load = async () => {
  try {
    const res = await appApi.my()
    list.value = res.data
  } finally {
    loading.value = false
    refreshing.value = false
  }
}
onMounted(load)
</script>

<style scoped>
.app-card { cursor: pointer; transition: transform .15s ease; }
.app-card:active { transform: scale(.98); }
.row { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 700; font-size: 16px; }
.sub { color: var(--mb-sub); font-size: 13px; margin-top: 8px; display: flex; justify-content: space-between; align-items: center; }
.go-chat { color: var(--mb-primary); font-size: 13px; }
</style>
