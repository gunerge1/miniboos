<template>
  <div class="page">
    <van-nav-bar class="mb-navbar" title="职位管理">
      <template #right>
        <van-icon name="apps-o" size="22" color="#00a6a7" @click="menuShow = true" />
      </template>
    </van-nav-bar>
    <van-pull-refresh v-model="refreshing" @refresh="load">
      <van-empty v-if="!loading && jobs.length === 0 && approved" description="还没有职位，点右下角发布第一个"
                 image="search" />
      <div v-for="j in jobs" :key="j.id" class="mb-card job-card">
        <div class="row1">
          <span class="title">{{ j.title }}</span>
          <van-tag :type="statusType(j.status)" round>{{ statusText(j.status) }}</van-tag>
        </div>
        <div class="row2">
          <span class="salary">{{ j.salaryMin }}-{{ j.salaryMax }}K</span>
          <span class="tag-chip">{{ catLabel(j.category) }}</span>
          <span class="tag-chip">{{ cityLabel(j.city) }}</span>
          <span class="tag-chip" v-if="j.education">{{ eduLabel(j.education) }}</span>
        </div>
        <div class="ops">
          <van-button v-if="j.status === 'ACTIVE' || j.status === 'OFF'" size="small" plain
                      @click="switchJob(j)">{{ j.status === 'ACTIVE' ? '下架' : '上架' }}</van-button>
          <van-button v-if="j.status === 'ACTIVE'" size="small" plain type="primary"
                      @click="$router.push('/candidates/' + j.id)">看投递</van-button>
        </div>
      </div>
    </van-pull-refresh>

    <div class="fab" @click="goPublish"><van-icon name="plus" /><span>发布</span></div>

    <van-tabbar route>
      <van-tabbar-item icon="bag-o" to="/">职位管理</van-tabbar-item>
    </van-tabbar>

    <van-action-sheet v-model:show="menuShow" :actions="menuActions" cancel-text="取消"
                      @select="onMenu" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { jobApi, companyApi, dicts } from '../api'

const router = useRouter()
const jobs = ref([])
const loading = ref(true)
const refreshing = ref(false)
const status = reactive({ companyApproved: false })
const approved = computed(() => status.companyApproved)
const menuShow = ref(false)
const menuActions = [
  { name: '企业认证', key: 'company' },
  { name: '退出登录', key: 'logout', color: '#ee0a24' },
]
const STATUS = {
  PENDING: ['待审核', 'warning'], ACTIVE: ['招聘中', 'success'],
  OFF: ['已下架', 'default'], REJECTED: ['被驳回', 'danger']
}
const statusText = s => (STATUS[s] || [s])[0]
const statusType = s => (STATUS[s] || [])[1] || 'default'

const dictMaps = reactive({ category: {}, city: {}, education: {} })
const catLabel = c => dictMaps.category[c] || c
const cityLabel = c => dictMaps.city[c] || c
const eduLabel = c => dictMaps.education[c] || c

const load = async () => {
  try {
    const c = await companyApi.my()
    status.companyApproved = c.data?.status === 'APPROVED'
    if (!c.data) {
      showToast('请先完成企业认证')
      router.replace('/company')
      return
    }
    const res = await jobApi.my()
    jobs.value = res.data
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const switchJob = async j => {
  try {
    await jobApi.switchStatus(j.id, j.status === 'ACTIVE' ? 'OFF' : 'ACTIVE')
    await load()
  } catch (e) { showToast(e.msg || e) }
}

const goPublish = () => {
  if (!approved.value) { showToast('企业认证通过后才能发布'); return }
  router.push('/publish')
}

const onMenu = action => {
  menuShow.value = false
  if (action.key === 'company') router.push('/company')
  if (action.key === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('nickname')
    router.replace('/login')
  }
}

onMounted(async () => {
  load()
  try {
    const [cats, cities, edus] = await Promise.all([dicts('category'), dicts('city'), dicts('education')])
    for (const d of cats.data) dictMaps.category[d.code] = d.label
    for (const d of cities.data) dictMaps.city[d.code] = d.label
    for (const d of edus.data) dictMaps.education[d.code] = d.label
  } catch (e) { /* 字典失败回退显示code */ }
})
</script>

<style scoped>
.job-card { margin-bottom: 14px; }
.row1 { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
.title { color: var(--mb-title); font-weight: 600; font-size: 16px; flex: 1; min-width: 0; }
.row1 :deep(.van-tag) { padding: 3px 10px; font-size: 12px; flex-shrink: 0; }
.row2 { display: flex; align-items: center; flex-wrap: wrap; gap: 6px; margin-top: 10px; }
.salary { color: var(--mb-salary); font-size: 16px; font-weight: 500; margin-right: 4px; }
.tag-chip { background: var(--mb-chip-bg); color: var(--mb-sub); font-size: 11px;
  padding: 3px 8px; border-radius: 4px; line-height: 1.4; }
.ops { display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px; padding-top: 10px;
  border-top: 1px solid #f2f4f7; }
.fab { position: fixed; right: 20px; bottom: 90px; height: 48px; padding: 0 20px; border-radius: 24px;
  background: var(--mb-primary); color: #fff; display: flex; align-items: center; justify-content: center; gap: 4px;
  font-size: 15px; font-weight: 500; box-shadow: 0 4px 12px rgba(0,166,167,.4); }
</style>
