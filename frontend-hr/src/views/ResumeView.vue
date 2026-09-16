<template>
  <div class="page">
    <van-nav-bar title="牛人简历" left-arrow @click-left="$router.back()" class="mb-navbar" />
    <div class="mb-card head" v-if="resume">
      <img v-if="resume.photo" :src="resume.photo" class="photo" />
      <div v-else class="photo photo-empty">未上传</div>
      <div class="info">
        <div class="name">{{ resume.name || '未具名' }}</div>
        <div class="want">
          期望：{{ resume.expectCategory || '-' }} · {{ resume.expectCity || '-' }} ·
          {{ resume.expectSalaryMin }}-{{ resume.expectSalaryMax }}K
        </div>
      </div>
    </div>
    <div class="mb-card" v-if="resume">
      <div class="section">自我介绍</div>
      <div class="content">{{ resume.intro || '（空）' }}</div>
    </div>
    <div class="mb-card" v-if="resume">
      <div class="section">项目经历（{{ resume.experiences.length }}）</div>
      <div v-for="e in resume.experiences" :key="e.id" class="exp">
        <div class="exp-title">{{ e.projectName }}</div>
        <div class="exp-time">{{ e.startDate }} ~ {{ e.endDate || '至今' }}</div>
        <div class="exp-desc">{{ e.description }}</div>
      </div>
      <van-empty v-if="resume.experiences.length === 0" description="无项目经历" image-size="60" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { showToast } from 'vant'
import { jobApi } from '../api'

const route = useRoute()
const resume = ref(null)

onMounted(async () => {
  try {
    const res = await jobApi.resumeOf(route.params.appId)
    resume.value = res.data
  } catch (e) {
    showToast(e.msg || e)
  }
})
</script>

<style scoped>
.head { display: flex; gap: 14px; align-items: center; }
.photo { width: 72px; height: 96px; border-radius: 8px; object-fit: cover;
  background: var(--mb-bg); }
.photo-empty { display: flex; align-items: center; justify-content: center;
  color: var(--mb-sub); font-size: 12px; }
.name { font-size: 20px; font-weight: 700; }
.want { color: var(--mb-sub); font-size: 13px; margin-top: 8px; line-height: 1.6; }
.section { font-weight: 700; margin-bottom: 10px; }
.content { color: var(--mb-text); line-height: 1.8; white-space: pre-wrap; }
.exp { padding: 10px 0; border-bottom: 1px solid #f0f2f5; }
.exp:last-of-type { border-bottom: none; }
.exp-title { font-weight: 600; }
.exp-time { color: var(--mb-sub); font-size: 12px; margin: 4px 0; }
.exp-desc { color: var(--mb-text); font-size: 13px; line-height: 1.7; white-space: pre-wrap; }
</style>
