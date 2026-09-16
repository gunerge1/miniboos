<template>
  <div>
    <div class="hero mb-hero">
      <div class="hero-title">今天，平台运转如何？</div>
      <div class="hero-sub">看板数字 = PRD四把尺的实时读数</div>
    </div>
    <el-row :gutter="16">
      <el-col :span="5" v-for="item in cards" :key="item.label">
        <el-card class="mb-stat-card">
          <div class="stat">
            <div class="mb-icon-badge">{{ item.icon }}</div>
            <div>
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card class="note">
      <div>🎯 成功标尺对照（PRD第9节）：企业入驻10+ / 牛人注册500+ / 简历沉淀300+</div>
      <div class="sub">触发时间框：上线后1个月（企业）/ 2个月（牛人与简历）</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '../api'

const cards = ref([
  { label: '企业数', value: 0, icon: '🏢' }, { label: '职位数', value: 0, icon: '💼' },
  { label: '投递量', value: 0, icon: '📨' }, { label: '简历沉淀', value: 0, icon: '📄' },
  { label: '牛人数', value: 0, icon: '👥' }
])

onMounted(async () => {
  const res = await admin.stats()
  const s = res.data
  cards.value[0].value = s.companyCount
  cards.value[1].value = s.jobCount
  cards.value[2].value = s.applicationCount
  cards.value[3].value = s.resumeCount
  cards.value[4].value = s.candidateCount
})
</script>

<style scoped>
.mb-hero { background: var(--mb-gradient); border-radius: var(--mb-radius); color: #fff;
  padding: 22px 24px; margin-bottom: 16px; border: none; }
.hero-title { font-size: 20px; font-weight: 700; }
.hero-sub { font-size: 13px; opacity: .9; margin-top: 6px; }
.stat { display: flex; align-items: center; gap: 12px; }
.stat-value { font-size: 26px; font-weight: 800; line-height: 1.1; }
.stat-label { color: var(--mb-sub); font-size: 13px; margin-top: 2px; }
.note { margin-top: 16px; border-radius: var(--mb-radius); border: none; box-shadow: var(--mb-shadow); color: var(--mb-text); }
.sub { color: var(--mb-sub); font-size: 13px; margin-top: 6px; }
</style>
