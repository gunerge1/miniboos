<template>
  <div class="page" v-if="job">
    <van-nav-bar title="职位详情" left-arrow @click-left="$router.back()" class="mb-navbar" />
    <div class="hero">
      <div class="title">{{ job.title }}</div>
      <div class="salary mb-salary">{{ job.salaryMin }}-{{ job.salaryMax }}K<span class="unit">/月</span></div>
      <div class="tags">
        <span class="chip-light">{{ job.categoryLabel }}</span>
        <span class="chip-light">{{ job.cityLabel }}</span>
        <span v-if="job.educationLabel" class="chip-light">{{ job.educationLabel }}</span>
      </div>
    </div>
    <div class="mb-card company-card">
      <div class="company-name">🏢 {{ job.companyName }}</div>
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
.hero { background: var(--mb-gradient); color: #fff; padding: 20px 20px 26px;
  border-radius: 0 0 32px 32px; }
.title { font-size: 22px; font-weight: 700; }
.salary { color: #fff; font-size: 24px; margin-top: 8px; }
.unit { font-size: 13px; opacity: .85; margin-left: 4px; }
.tags { margin-top: 12px; display: flex; gap: 8px; }
.chip-light { font-size: 12px; background: rgba(255,255,255,.2); border-radius: 999px; padding: 3px 10px; }
.company-card { display: flex; align-items: center; justify-content: space-between; }
.company-name { font-weight: 600; }
.company-sub { color: var(--mb-sub); font-size: 13px; }
.section-title { font-weight: 700; margin-bottom: 8px; }
.jd { white-space: pre-wrap; line-height: 1.9; color: var(--mb-text); }
.action { margin: 20px 16px 40px; }
</style>
