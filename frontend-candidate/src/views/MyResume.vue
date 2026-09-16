<template>
  <div class="page">
    <van-nav-bar title="我的简历" class="mb-navbar" />
    <!-- BOSS式个人卡：头像左、姓名+求职属性右 -->
    <van-cell-group inset class="profile-card">
      <div class="profile-head">
        <div class="avatar-box" @click="pickPhoto">
          <img v-if="form.photo" :src="form.photo" class="avatar" />
          <van-icon v-else name="user-o" size="34" color="#c8cdd6" />
          <div class="avatar-cam"><van-icon name="camera-o" size="12" color="#fff" /></div>
        </div>
        <div class="profile-info">
          <input v-model="form.name" class="name-input" placeholder="点击填写姓名" maxlength="12" />
          <div class="attr-line">
            <template v-if="categoryLabel || cityLabel || salaryText">
              <span v-if="categoryLabel">{{ categoryLabel }}</span>
              <span v-if="cityLabel"> · {{ cityLabel }}</span>
              <span v-if="salaryText"> · {{ salaryText }}K</span>
            </template>
            <span v-else class="attr-empty">完善求职意向，提升匹配度</span>
          </div>
        </div>
      </div>
      <input ref="fileInput" type="file" accept="image/*" hidden @change="onPhoto" />
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

    <van-cell-group inset v-if="loaded && published">
      <van-cell title="✅ 简历已发布" label="对外可见，可投递职位；再次保存即自动更新" />
    </van-cell-group>

    <div class="action">
      <div class="btn-row">
        <van-button v-if="!published" round plain type="default" :loading="saving"
                    @click="save(false)" class="half">暂存草稿</van-button>
        <van-button round type="primary" :loading="saving" @click="save(true)" class="half">
          {{ published ? '更新简历' : '发布简历' }}
        </van-button>
      </div>
      <van-button round block plain type="danger" class="logout" @click="logout">退出登录</van-button>
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
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { dicts, resumeApi } from '../api'
import { compressPhoto } from '../utils/photo'

const router = useRouter()
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('nickname')
  router.replace('/login')
}

const form = ref({ name: '', photo: '', expectCategory: '', expectCity: '', intro: '' })
const salaryText = ref('')
const published = ref(false)
const loaded = ref(false)
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
  loaded.value = true
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

const save = async publish => {
  const m = salaryText.value.match(/^(\d+)\s*-\s*(\d+)$/)
  saving.value = true
  try {
    await resumeApi.save({
      ...form.value,
      expectSalaryMin: m ? +m[1] : null,
      expectSalaryMax: m ? +m[2] : null,
      published: publish ? 1 : 0
    })
    if (publish && !published.value) {
      published.value = true
      showToast('🎉 简历已发布，可以去投递了')
    } else {
      showToast(publish ? '简历已更新' : '草稿已暂存')
    }
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
/* BOSS式个人卡：头像左、信息右 */
.profile-card { margin-top: 12px; }
.profile-head { display: flex; align-items: center; gap: 14px; padding: 16px; }
.avatar-box { position: relative; width: 64px; height: 86px; border-radius: 8px; background: var(--mb-chip-bg);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0; cursor: pointer; }
.avatar { width: 100%; height: 100%; border-radius: 8px; object-fit: cover; }
.avatar-cam { position: absolute; right: -7px; bottom: -7px; width: 22px; height: 22px; border-radius: 50%;
  background: var(--mb-primary); display: flex; align-items: center; justify-content: center; border: 2px solid #fff; }
.profile-info { flex: 1; min-width: 0; }
.name-input { border: none; outline: none; font-size: 20px; font-weight: 600; color: var(--mb-title);
  background: transparent; width: 100%; padding: 0; font-family: inherit; }
.name-input::placeholder { color: #c8cdd6; font-weight: 400; }
.attr-line { margin-top: 8px; color: var(--mb-sub); font-size: 12px; line-height: 1.5; }
.attr-empty { color: #c8cdd6; }
.exp { margin-bottom: 2px; }
.add-exp { padding: 10px 16px; }
.action { margin: 20px 16px 90px; }
.btn-row { display: flex; gap: 12px; }
.half { flex: 1; }
.logout { margin-top: 12px; }
</style>
