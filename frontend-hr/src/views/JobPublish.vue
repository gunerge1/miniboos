<template>
  <div class="page">
    <van-nav-bar title="发布职位" left-arrow @click-left="$router.back()" />
    <van-form @submit="submit">
      <van-cell-group inset>
        <van-field v-model="form.title" label="职位名" placeholder="如：Java后端工程师" required
                   :rules="[{ required: true, message: '必填' }]" />
        <van-field v-model="categoryLabel" is-link readonly label="岗位方向" placeholder="选择" required
                   @click="show.category = true" />
        <van-field v-model="cityLabel" is-link readonly label="工作城市" placeholder="选择" required
                   @click="show.city = true" />
        <van-field v-model="educationLabel" is-link readonly label="学历要求" placeholder="选择（可不限）"
                   @click="show.education = true" />
        <van-field v-model="salaryText" label="薪资范围" placeholder="如 15-25（K/月）" required
                   :rules="[{ required: true, message: '必填' }]" />
        <van-field v-model="form.description" rows="5" autosize type="textarea" label="职位描述"
                   placeholder="职责与要求" required :rules="[{ required: true, message: '必填' }]" />
      </van-cell-group>
      <div class="action">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          提交发布（发布后待平台审核）
        </van-button>
      </div>
    </van-form>

    <van-popup v-model:show="show.category" round position="bottom">
      <van-picker :columns="cols.category" @confirm="p => pick('category', p)" @cancel="show.category = false" />
    </van-popup>
    <van-popup v-model:show="show.city" round position="bottom">
      <van-picker :columns="cols.city" @confirm="p => pick('city', p)" @cancel="show.city = false" />
    </van-popup>
    <van-popup v-model:show="show.education" round position="bottom">
      <van-picker :columns="cols.education" @confirm="p => pick('education', p)" @cancel="show.education = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { jobApi, dicts } from '../api'

const router = useRouter()
const form = ref({ title: '', category: '', city: '', education: '', salaryMin: null, salaryMax: null, description: '' })
const salaryText = ref('')
const show = reactive({ category: false, city: false, education: false })
const loading = ref(false)

const data = reactive({ category: [], city: [], education: [] })
const cols = computed(() => ({
  category: data.category.map(d => ({ text: d.label, value: d.code })),
  city: data.city.map(d => ({ text: d.label, value: d.code })),
  education: data.education.map(d => ({ text: d.label, value: d.code }))
}))
const labelOf = (type, code) => (data[type].find(d => d.code === code) || {}).label || ''
const categoryLabel = computed(() => labelOf('category', form.value.category))
const cityLabel = computed(() => labelOf('city', form.value.city))
const educationLabel = computed(() => labelOf('education', form.value.education))

onMounted(async () => {
  const [c, ci, e] = await Promise.all([dicts('category'), dicts('city'), dicts('education')])
  data.category = c.data; data.city = ci.data; data.education = e.data
})

const pick = (type, { selectedOptions }) => {
  form.value[type] = selectedOptions[0].value
  show[type] = false
}

const submit = async () => {
  const m = salaryText.value.match(/^(\d+)\s*-\s*(\d+)$/)
  if (!m) { showToast('薪资格式：15-25'); return }
  if (!form.value.category) { showToast('请选岗位方向'); return }
  if (!form.value.city) { showToast('请选城市'); return }
  loading.value = true
  try {
    await jobApi.publish({ ...form.value, salaryMin: +m[1], salaryMax: +m[2] })
    showToast('已提交，待平台审核')
    router.replace('/')
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.action { margin: 24px 16px; }
</style>
