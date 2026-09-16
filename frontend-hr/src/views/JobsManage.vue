<template>
  <div class="page">
    <van-nav-bar class="mb-navbar" title="职位管理" />
    <van-pull-refresh v-model="refreshing" @refresh="load">
      <van-empty v-if="!loading && jobs.length === 0 && approved" description="还没有职位，点下方发布第一个"
                 image="search" />
      <van-cell v-for="j in jobs" :key="j.id">
        <template #title>
          <div class="row">
            <span class="title">{{ j.title }}</span>
            <van-tag :type="statusType(j.status)">{{ statusText(j.status) }}</van-tag>
          </div>
          <div class="sub">{{ j.city }} · {{ j.salaryMin }}-{{ j.salaryMax }}K</div>
        </template>
        <template #right-icon>
          <div class="ops">
            <van-button v-if="j.status === 'ACTIVE' || j.status === 'OFF'" size="small" plain type="primary"
                       @click="switchJob(j)">{{ j.status === 'ACTIVE' ? '下架' : '上架' }}</van-button>
            <van-button v-if="j.status === 'ACTIVE'" size="small" plain type="success"
                       @click="$router.push('/candidates/' + j.id)">看投递</van-button>
          </div>
        </template>
      </van-cell>
    </van-pull-refresh>
    <div class="fab" @click="goPublish"><van-icon name="plus" /></div>
    <van-tabbar route>
      <van-tabbar-item icon="bag-o" to="/">职位管理</van-tabbar-item>
      <van-tabbar-item icon="shop-o" to="/company">企业认证</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { jobApi, companyApi } from '../api'

const router = useRouter()
const jobs = ref([])
const loading = ref(true)
const refreshing = ref(false)
const status = reactive({ companyApproved: false })
const approved = computed(() => status.companyApproved)
const STATUS = {
  PENDING: ['待审核', 'warning'], ACTIVE: ['招聘中', 'success'],
  OFF: ['已下架', 'default'], REJECTED: ['被驳回', 'danger']
}
const statusText = s => (STATUS[s] || [s])[0]
const statusType = s => (STATUS[s] || [])[1] || 'default'

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

onMounted(load)
</script>

<style scoped>
.row { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 600; }
.sub { color: #969799; font-size: 13px; margin-top: 4px; }
.ops { display: flex; gap: 6px; }
.fab { position: fixed; right: 20px; bottom: 90px; width: 52px; height: 52px; border-radius: 50%;
  background: #1989fa; color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 26px; box-shadow: 0 4px 12px rgba(25,137,250,.4); }
</style>
