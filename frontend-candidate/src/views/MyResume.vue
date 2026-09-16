<template>
  <div class="page">
    <van-nav-bar title="我的简历" />
    <van-cell-group inset title="基本信息">
      <van-field v-model="form.name" label="姓名" placeholder="真实姓名" />
      <van-field name="photo" label="简历照片">
        <template #input>
          <div class="photo-box" @click="pickPhoto">
            <img v-if="form.photo" :src="form.photo" class="photo" />
            <van-icon v-else name="camera-o" size="40" color="#dcdee0" />
          </div>
          <input ref="fileInput" type="file" accept="image/*" hidden @change="onPhoto" />
        </template>
      </van-field>
    </van-cell-group>

    <van-cell-group inset title="求职意向（意向匹配的原料）">
      <van-field v-model="categoryLabel" is-link readonly label="期望方向" placeholder="选择岗位方向"
                 @click="showCategory = true" />
      <van-field v-model="cityLabel" is-link readonly label="期望城市" placeholder="选择城市"
                 @click="showCity = true" />
      <van-field v-model="salaryText" label="期望薪资" placeholder="如 20-30（K/月）" />
      <van-field v-model="form.intro" rows="3" autosize type="textarea" label="自我介绍" placeholder="一段话说清你是谁" />
    </van-cell-group>

    <van-popup v-model:show="showCategory" round position="bottom">
      <van-picker :columns="categoryCols" @confirm="onCategory" @cancel="showCategory = false" />
    </van-popup>
    <van-popup v-model:show="showCity" round position="bottom">
      <van-picker :columns="cityCols" @confirm="onCity" @cancel="showCity = false" />
    </van-popup>

    <van-cell-group inset title="项目经历">
      <van-swipe-cell v-for="exp in experiences" :key="exp.id" class="exp">
        <van-cell :title="exp.projectName" :label="`${exp.startDate || ''} ~ ${exp.endDate || '至今'}`"
                  is-link @click="editExp(exp)" />
        <template #right>
          <van-button square type="danger" text="删除" @click="delExp(exp)" />
        </template>
      </van-swipe-cell>
      <div class="add-exp">
        <van-button size="small" plain type="primary" icon="plus" @click="editExp(null)">添加经历</van-button>
      </div>
    </van-cell-group>

    <van-cell-group inset title="发布">
      <van-cell center title="对外发布简历" label="发布后才能投递职位">
        <template #right-icon>
          <van-switch v-model="published" size="22" />
        </template>
      </van-cell>
    </van-cell-group>

    <div class="action">
      <van-button round block type="primary" :loading="saving" @click="save">保存简历</van-button>
    </div>

    <van-tabbar route>
      <van-tabbar-item icon="search" to="/">职位</van-tabbar-item>
      <van-tabbar-item icon="notes-o" to="/my-applications">我的投递</van-tabbar-item>
      <van-tabbar-item icon="contact" to="/my-resume">我的简历</van-tabbar-item>
    </van-tabbar>

    <van-dialog v-model:show="expShow" :title="editing ? '编辑经历' : '添加经历'" show-cancel-button
                @confirm="saveExp">
      <van-cell-group>
        <van-field v-model="expForm.projectName" label="项目名" placeholder="如：电商系统重构" />
        <van-field v-model="expForm.startDate" label="开始" placeholder="2024-01" />
        <van-field v-model="expForm.endDate" label="结束" placeholder="留空=至今" />
        <van-field v-model="expForm.description" rows="2" autosize type="textarea" label="描述" />
      </van-cell-group>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import { dicts, resumeApi } from '../api'
import { compressPhoto } from '../utils/photo'

const form = ref({ name: '', photo: '', expectCategory: '', expectCity: '', intro: '' })
const salaryText = ref('')
const published = ref(false)
const experiences = ref([])
const saving = ref(false)
const fileInput = ref(null)

const categories = ref([])
const cities = ref([])
const showCategory = ref(false)
const showCity = ref(false)
const categoryCols = computed(() => categories.value.map(d => ({ text: d.label, value: d.code })))
const cityCols = computed(() => cities.value.map(d => ({ text: d.label, value: d.code })))
const categoryLabel = computed(() =>
  (categories.value.find(d => d.code === form.value.expectCategory) || {}).label || '')
const cityLabel = computed(() =>
  (cities.value.find(d => d.code === form.value.expectCity) || {}).label || '')

const expShow = ref(false)
const editing = ref(null)
const expForm = ref({ projectName: '', startDate: '', endDate: '', description: '' })

onMounted(async () => {
  const [cats, cts] = await Promise.all([dicts('category'), dicts('city')])
  categories.value = cats.data
  cities.value = cts.data
  const res = await resumeApi.get()
  if (res.data) {
    const d = res.data
    form.value = { name: d.name, photo: d.photo || '', expectCategory: d.expectCategory || '',
      expectCity: d.expectCity || '', intro: d.intro || '' }
    if (d.expectSalaryMin && d.expectSalaryMax) salaryText.value = `${d.expectSalaryMin}-${d.expectSalaryMax}`
    published.value = d.published === 1
    experiences.value = d.experiences || []
  }
})

const pickPhoto = () => fileInput.value.click()
const onPhoto = async e => {
  const file = e.target.files[0]
  if (!file) return
  try {
    form.value.photo = await compressPhoto(file)
    showToast('照片已压缩')
  } catch (err) {
    showToast(err.message)
  }
  e.target.value = ''
}

const onCategory = ({ selectedOptions }) => {
  form.value.expectCategory = selectedOptions[0].value
  showCategory.value = false
}
const onCity = ({ selectedOptions }) => {
  form.value.expectCity = selectedOptions[0].value
  showCity.value = false
}

const editExp = exp => {
  editing.value = exp
  expForm.value = exp ? { ...exp } : { projectName: '', startDate: '', endDate: '', description: '' }
  expShow.value = true
}

const saveExp = async () => {
  if (!expForm.value.projectName) { showToast('项目名必填'); return }
  if (editing.value) {
    await resumeApi.updateExp(editing.value.id, expForm.value)
  } else {
    await resumeApi.addExp(expForm.value)
  }
  const res = await resumeApi.get()
  experiences.value = res.data?.experiences || []
}

const delExp = async exp => {
  await resumeApi.delExp(exp.id)
  experiences.value = experiences.value.filter(e => e.id !== exp.id)
}

const save = async () => {
  const m = salaryText.value.match(/^(\d+)\s*-\s*(\d+)$/)
  saving.value = true
  try {
    await resumeApi.save({
      ...form.value,
      expectSalaryMin: m ? +m[1] : null,
      expectSalaryMax: m ? +m[2] : null,
      published: published.value ? 1 : 0
    })
    showToast('已保存')
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.photo-box { width: 72px; height: 96px; border: 1px dashed #dcdee0; border-radius: 6px;
  display: flex; align-items: center; justify-content: center; overflow: hidden; }
.photo { width: 100%; height: 100%; object-fit: cover; }
.exp { margin-bottom: 2px; }
.add-exp { padding: 10px 16px; }
.action { margin: 20px 16px 70px; }
</style>
