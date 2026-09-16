<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="5" v-for="item in cards" :key="item.label">
        <el-card>
          <el-statistic :title="item.label" :value="item.value" />
        </el-card>
      </el-col>
    </el-row>
    <el-card class="note">
      <div>📊 成功标尺对照（PRD第9节）：企业入驻10+ / 牛人注册500+ / 简历沉淀300+</div>
      <div class="sub">看板数字 = 运营标尺的实时读数</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { admin } from '../api'

const cards = ref([
  { label: '企业数', value: 0 }, { label: '职位数', value: 0 },
  { label: '投递量', value: 0 }, { label: '简历沉淀', value: 0 }, { label: '牛人数', value: 0 }
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
.note { margin-top: 16px; color: #606266; }
.sub { color: #909399; font-size: 13px; margin-top: 6px; }
</style>
