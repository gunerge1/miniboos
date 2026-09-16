<template>
  <div class="page">
    <van-nav-bar title="找职位" class="mb-navbar" />
    <van-search v-model="kw" placeholder="搜职位关键词，如 Java" @search="reload" background="transparent" />
    <van-dropdown-menu class="filters">
      <van-dropdown-item v-model="category" :options="categoryOptions" @change="reload" />
      <van-dropdown-item v-model="city" :options="cityOptions" @change="reload" />
    </van-dropdown-menu>

    <van-list v-model:loading="loading" :finished="finished" finished-text="— 到底啦 —"
              @load="load" offset="100">
      <div v-for="job in list" :key="job.id" class="job-card mb-card" @click="$router.push('/job/' + job.id)">
        <div class="row1">
          <span class="title">{{ job.title }}</span>
          <span class="salary mb-salary">{{ job.salaryMin }}-{{ job.salaryMax }}K</span>
        </div>
        <div class="tags">
          <span class="chip">{{ job.categoryLabel }}</span>
          <span class="chip">{{ job.cityLabel }}</span>
          <span v-if="job.educationLabel" class="chip">{{ job.educationLabel }}</span>
          <span v-if="job.matchScore >= 5" class="chip match">✦ 意向匹配</span>
        </div>
        <div class="company">🏢 {{ job.companyName }} · {{ job.companyIndustryLabel }}</div>
      </div>
    </van-list>

    <van-tabbar route>
      <van-tabbar-item icon="search" to="/">职位</van-tabbar-item>
      <van-tabbar-item icon="notes-o" to="/my-applications">我的投递</van-tabbar-item>
      <van-tabbar-item icon="contact" to="/my-resume">我的简历</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { jobs, dicts } from '../api'

const kw = ref('')
const category = ref('')
const city = ref('')
const categoryOptions = ref([{ text: '全部方向', value: '' }])
const cityOptions = ref([{ text: '全部城市', value: '' }])
const list = ref([])
const page = ref(0)
const loading = ref(false)
const finished = ref(false)

onMounted(async () => {
  try {
    const [cats, cities] = await Promise.all([dicts('category'), dicts('city')])
    categoryOptions.value.push(...cats.data.map(d => ({ text: d.label, value: d.code })))
    cityOptions.value.push(...cities.data.map(d => ({ text: d.label, value: d.code })))
  } catch (e) { /* 字典加载失败不阻塞列表 */ }
})

const reload = () => { list.value = []; page.value = 0; finished.value = false; load() }

const load = async () => {
  loading.value = true
  try {
    const res = await jobs.list({ page: page.value + 1, size: 10, kw: kw.value || undefined,
      category: category.value || undefined, city: city.value || undefined })
    page.value = res.data.page
    list.value.push(...res.data.list)
    if (list.value.length >= res.data.total) finished.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.filters { border-radius: 12px; margin: 0 16px 8px; overflow: hidden; }
.job-card { cursor: pointer; transition: transform .15s ease; }
.job-card:active { transform: scale(.98); }
.row1 { display: flex; justify-content: space-between; align-items: baseline; }
.title { font-size: 17px; font-weight: 700; }
.salary { font-size: 17px; }
.tags { margin: 10px 0; display: flex; gap: 8px; flex-wrap: wrap; }
.chip { font-size: 12px; color: var(--mb-sub); background: var(--mb-bg);
  border-radius: 999px; padding: 3px 10px; }
.chip.match { color: #fff; background: var(--mb-gradient); font-weight: 600; }
.company { color: var(--mb-sub); font-size: 13px; }
</style>
