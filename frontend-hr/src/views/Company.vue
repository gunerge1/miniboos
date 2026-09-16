<template>
  <div class="page">
    <van-nav-bar title="企业认证" left-arrow @click-left="$router.back()" />
    <template v-if="company">
      <van-cell-group inset>
        <van-cell title="企业名称" :value="company.name" />
        <van-cell title="所属行业" :value="company.industry" />
        <van-cell title="执照号" :value="company.licenseNo || '-'" />
        <van-cell title="审核状态">
          <template #value>
            <van-tag :type="tagType(company.status)">{{ statusText(company.status) }}</van-tag>
          </template>
        </van-cell>
        <van-cell v-if="company.status === 'REJECTED'" title="驳回理由" :value="company.rejectReason" />
      </van-cell-group>
      <div class="tip" v-if="company.status === 'PENDING'">平台审核中，通过后即可发布职位</div>
    </template>
    <template v-else>
      <van-form @submit="submit">
        <van-cell-group inset>
          <van-field v-model="form.name" label="企业名称" placeholder="营业执照上的全称" required
                     :rules="[{ required: true, message: '必填' }]" />
          <van-field v-model="industryLabel" is-link readonly label="所属行业" placeholder="选择行业" required
                     @click="showPicker = true" />
          <van-field v-model="form.licenseNo" label="执照号" placeholder="统一社会信用代码（选填）" />
        </van-cell-group>
        <div class="action">
          <van-button round block type="primary" native-type="submit" :loading="loading">提交认证</van-button>
        </div>
      </van-form>
      <van-popup v-model:show="showPicker" round position="bottom">
        <van-picker :columns="cols" @confirm="onPick" @cancel="showPicker = false" />
      </van-popup>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import { companyApi, dicts } from '../api'

const company = ref(null)
const form = ref({ name: '', industry: '', licenseNo: '' })
const industries = ref([])
const showPicker = ref(false)
const loading = ref(false)
const cols = computed(() => industries.value.map(d => ({ text: d.label, value: d.code })))
const industryLabel = computed(() =>
  (industries.value.find(d => d.code === form.value.industry) || {}).label || '')

const STATUS = { PENDING: ['待审核', 'warning'], APPROVED: ['已认证', 'success'], REJECTED: ['已驳回', 'danger'] }
const statusText = s => (STATUS[s] || [s])[0]
const tagType = s => (STATUS[s] || [])[1] || 'default'

onMounted(async () => {
  const res = await companyApi.my()
  company.value = res.data
  if (!company.value) {
    const d = await dicts('industry')
    industries.value = d.data
  }
})

const onPick = ({ selectedOptions }) => {
  form.value.industry = selectedOptions[0].value
  showPicker.value = false
}

const submit = async () => {
  if (!form.value.industry) { showToast('请选择行业'); return }
  loading.value = true
  try {
    await companyApi.submit(form.value)
    showToast('已提交，等待平台审核')
    const res = await companyApi.my()
    company.value = res.data
  } catch (e) {
    showToast(e.msg || e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.tip { text-align: center; color: #969799; margin-top: 16px; }
.action { margin: 24px 16px; }
</style>
