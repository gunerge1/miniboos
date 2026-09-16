<template>
  <div class="page" v-if="job">
    <van-nav-bar title="职位详情" left-arrow @click-left="$router.back()" />
    <van-cell-group inset>
      <div class="head">
        <div class="title">{{ job.title }} <span class="salary">{{ job.salaryMin }}-{{ job.salaryMax }}K</span></div>
        <div class="tags">
          <van-tag plain type="primary">{{ job.categoryLabel }}</van-tag>
          <van-tag plain type="success">{{ job.cityLabel }}</van-tag>
          <van-tag v-if="job.educationLabel" plain>{{ job.educationLabel }}</van-tag>
        </div>
        <div class="company">{{ job.companyName }} · {{ job.companyIndustryLabel }}</div>
      </div>
      <van-divider>职位描述</van-divider>
      <div class="jd">{{ job.description }}</div>
    </van-cell-group>
    <div class="action">
      <van-button round block type="primary" :loading="applying" @click="apply">一键投递</van-button>
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
    await showDialog({ title: '投递成功', message: '可以去"我的投递"和HR开聊了' })
    router.push('/my-applications')
  } catch (e) {
    // 简历没发布等业务拦截，给出明确引导
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
.head { padding: 16px; }
.title { font-size: 20px; font-weight: 700; }
.salary { color: #ee0a24; font-size: 16px; margin-left: 8px; }
.tags { margin: 8px 0; display: flex; gap: 6px; }
.company { color: #969799; }
.jd { padding: 0 16px 16px; white-space: pre-wrap; line-height: 1.8; }
.action { margin: 24px 16px; }
</style>
