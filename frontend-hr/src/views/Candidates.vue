<template>
  <div class="page">
    <van-nav-bar class="mb-navbar" title="投递列表" left-arrow @click-left="$router.back()" />
    <van-pull-refresh v-model="refreshing" @refresh="load">
      <van-empty v-if="!loading && list.length === 0" description="还没有牛人投递" />
      <van-cell v-for="c in list" :key="c.applicationId">
        <template #title>
          <div class="row">
            <span class="name">{{ c.candidateName || '未具名牛人' }}
              <van-tag v-if="c.matchScore >= 5" type="danger" class="match">意向匹配 {{ c.matchScore }}分</van-tag>
            </span>
            <van-tag :type="statusType(c.status)">{{ statusText(c.status) }}</van-tag>
          </div>
          <div class="sub">
            期望：{{ c.expectCategoryLabel || '-' }} · {{ c.expectCityLabel || '-' }} ·
            {{ c.expectSalaryMin }}-{{ c.expectSalaryMax }}K
          </div>
          <div class="sub time">投递于 {{ c.appliedAt?.slice(0, 10) }}</div>
        </template>
        <template #label>
          <div class="ops">
            <van-button size="small" plain type="primary" @click="chat(c)">沟通</van-button>
            <van-button v-if="can(c.status, 'VIEWED')" size="small" plain @click="set(c, 'VIEWED')">标记已看</van-button>
            <van-button v-if="can(c.status, 'INTERVIEW')" size="small" plain type="warning"
                       @click="set(c, 'INTERVIEW')">约面</van-button>
            <van-button v-if="can(c.status, 'OFFER')" size="small" plain type="success"
                       @click="set(c, 'OFFER')">发offer</van-button>
            <van-button v-if="can(c.status, 'REJECTED')" size="small" plain type="danger"
                       @click="set(c, 'REJECTED')">婉拒</van-button>
          </div>
        </template>
      </van-cell>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { jobApi } from '../api'

const route = useRoute()
const router = useRouter()
const list = ref([])
const loading = ref(true)
const refreshing = ref(false)

const NEXT = {
  SUBMITTED: ['VIEWED', 'INTERVIEW', 'OFFER', 'REJECTED'],
  VIEWED: ['INTERVIEW', 'OFFER', 'REJECTED'],
  INTERVIEW: ['OFFER', 'REJECTED'],
  OFFER: [], REJECTED: []
}
const can = (from, to) => (NEXT[from] || []).includes(to)
const STATUS = { SUBMITTED: ['待查看', 'default'], VIEWED: ['已查看', 'primary'],
  INTERVIEW: ['约面', 'warning'], OFFER: ['已发offer', 'success'], REJECTED: ['不合适', 'danger'] }
const statusText = s => (STATUS[s] || [s])[0]
const statusType = s => (STATUS[s] || [])[1] || 'default'

const load = async () => {
  try {
    const res = await jobApi.candidates(route.params.jobId)
    list.value = res.data
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const set = async (c, status) => {
  try {
    await jobApi.updateAppStatus(c.applicationId, status)
    c.status = status
    showToast('已更新')
  } catch (e) { showToast(e.msg || e) }
}

const chat = c => router.push('/chat/' + c.applicationId)

onMounted(load)
</script>

<style scoped>
.row { display: flex; justify-content: space-between; align-items: center; }
.name { font-weight: 600; }
.match { margin-left: 6px; }
.sub { color: #969799; font-size: 13px; margin-top: 4px; }
.time { font-size: 12px; }
.ops { display: flex; gap: 6px; margin-top: 8px; flex-wrap: wrap; }
</style>
