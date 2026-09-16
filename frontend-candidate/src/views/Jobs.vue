<template>
  <div class="page">
    <van-nav-bar title="找职位" />
    <van-search v-model="kw" placeholder="搜职位关键词" @search="reload" />
    <van-dropdown-menu>
      <van-dropdown-item v-model="category" :options="categoryOptions" @change="reload" />
      <van-dropdown-item v-model="city" :options="cityOptions" @change="reload" />
    </van-dropdown-menu>

    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了"
              @load="load" offset="100">
      <van-cell v-for="job in list" :key="job.id" is-link @click="$router.push('/job/' + job.id)">
        <template #title>
          <div class="job-title">
            <span>{{ job.title }}</span>
            <span class="salary">{{ job.salaryMin }}-{{ job.salaryMax }}K</span>
          </div>
          <div class="job-tags">
            <van-tag plain type="primary">{{ job.categoryLabel }}</van-tag>
            <van-tag plain type="success">{{ job.cityLabel }}</van-tag>
            <van-tag v-if="job.educationLabel" plain>{{ job.educationLabel }}</van-tag>
            <van-tag v-if="job.matchScore >= 5" type="danger">意向匹配</van-tag>
          </div>
          <div class="company">{{ job.companyName }} · {{ job.companyIndustryLabel }}</div>
        </template>
      </van-cell>
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
.job-title { display: flex; justify-content: space-between; font-weight: 600; }
.salary { color: #ee0a24; }
.job-tags { margin: 6px 0; display: flex; gap: 6px; flex-wrap: wrap; }
.company { color: #969799; font-size: 13px; }
</style>
