<template>
  <div class="page">
    <van-nav-bar title="我的投递" />
    <van-pull-refresh v-model="refreshing" @refresh="load">
      <van-empty v-if="!loading && list.length === 0" description="还没有投递，去找个心仪职位吧" />
      <van-cell v-for="a in list" :key="a.id" is-link @click="$router.push('/chat/' + a.id)">
        <template #title>
          <div class="row">
            <span class="title">{{ a.jobTitle }}</span>
            <van-tag :type="tagType(a.status)">{{ statusText(a.status) }}</van-tag>
          </div>
          <div class="sub">{{ a.companyName }} · {{ a.createdAt?.slice(0, 10) }}</div>
        </template>
      </van-cell>
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
.row { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 600; }
.sub { color: #969799; font-size: 13px; margin-top: 4px; }
</style>
