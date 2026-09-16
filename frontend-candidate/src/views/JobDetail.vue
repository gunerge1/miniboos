<template>
  <div class="page" v-if="job">
    <van-nav-bar title="职位详情" left-arrow @click-left="$router.back()" class="mb-navbar" />
    <div class="mb-card head-card">
      <div class="row1">
        <div class="title">{{ job.title }}</div>
        <div class="salary">{{ job.salaryMin }}-{{ job.salaryMax }}K<span class="unit">/月</span></div>
      </div>
      <div class="tags">
        <span>{{ job.categoryLabel }}</span><span class="dot">·</span>
        <span>{{ job.cityLabel }}</span>
        <span v-if="job.educationLabel"><span class="dot">·</span>{{ job.educationLabel }}</span>
      </div>
    </div>
    <div class="mb-card company-card">
      <div class="company-name">{{ job.companyName }}</div>
      <div class="company-sub">{{ job.companyIndustryLabel }}</div>
    </div>
    <div class="mb-card">
      <div class="section-title">职位描述</div>
      <div class="jd">{{ job.description }}</div>
    </div>
    <div class="action">
      <van-button round block class="mb-btn" :loading="applying" @click="apply">一键投递</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { jobs, appApi } from '../api'

const route = useRoute()
const router = useRouter()
const job = ref(null)
const applying = ref(false)

onMounted(async () => {
  try {
    const res = await jobs.detail(route.params.id)
    job.value = res.data
  } catch (e) {
    showToast(e.msg || e)
    router.back()
  }
})

const apply = async () => {
  applying.value = true
  try {
    await appApi.apply(route.params.id)
    await showDialog({ title: '🎉 投递成功', message: '可以去"我的投递"和HR开聊了' })
    router.push('/my-applications')
  } catch (e) {
    if (String(e.msg || e).includes('简历')) {
      await showDialog({ title: '先完善简历', message: '发布简历后才能投递哦' })
      router.push('/my-resume')
    } else {
      showToast(e.msg || e)
    }
  } finally {
    applying.value = false
  }
}
</script>

<style scoped>
.head-card { padding: 18px 16px; }
.row1 { display: flex; justify-content: space-between; align-items: baseline; gap: 12px; }
.title { color: var(--mb-title); font-size: 21px; font-weight: 600; }
.salary { color: var(--mb-salary); font-size: 22px; font-weight: 500; white-space: nowrap; }
.unit { font-size: 13px; }
.tags { margin-top: 10px; color: var(--mb-text); font-size: 13px; }
.dot { margin: 0 6px; color: #c8cdd6; }
.company-card { display: flex; align-items: center; justify-content: space-between; }
.company-name { font-weight: 600; font-size: 15px; }
.company-sub { color: var(--mb-sub); font-size: 13px; }
.section-title { font-weight: 600; margin-bottom: 8px; }
.jd { white-space: pre-wrap; line-height: 1.9; color: var(--mb-text); }
.action { margin: 20px 16px 40px; }
</style>
