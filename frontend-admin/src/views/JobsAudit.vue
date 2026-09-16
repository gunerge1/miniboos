<template>
  <div>
    <el-radio-group v-model="status" @change="load" style="margin-bottom:12px">
      <el-radio-button value="PENDING">待审核</el-radio-button>
      <el-radio-button value="ACTIVE">招聘中</el-radio-button>
      <el-radio-button value="OFF">已下架</el-radio-button>
      <el-radio-button value="REJECTED">已驳回</el-radio-button>
      <el-radio-button value="">全部</el-radio-button>
    </el-radio-group>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="职位名" />
      <el-table-column prop="category" label="方向" width="90" />
      <el-table-column prop="city" label="城市" width="90" />
      <el-table-column label="薪资(K)" width="110">
        <template #default="{ row }">{{ row.salaryMin }}-{{ row.salaryMax }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="tagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING'">
            <el-button size="small" type="success" @click="audit(row, 'APPROVED')">通过上架</el-button>
            <el-button size="small" type="danger" @click="audit(row, 'REJECTED')">驳回</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { admin } from '../api'

const status = ref('PENDING')
const list = ref([])

const STATUS = { PENDING: ['待审核', 'warning'], ACTIVE: ['招聘中', 'success'],
  OFF: ['已下架', 'info'], REJECTED: ['已驳回', 'danger'] }
const statusText = s => (STATUS[s] || [s])[0]
const tagType = s => (STATUS[s] || [])[1] || 'info'

const load = async () => {
  const res = await admin.jobs({ status: status.value || undefined, page: 1, size: 50 })
  list.value = res.data.list
}

const audit = async (row, result) => {
  let reason = ''
  if (result === 'REJECTED') {
    try {
      const r = await ElMessageBox.prompt('请填写驳回理由', '驳回职位')
      reason = r.value
    } catch { return }
  }
  try {
    await admin.auditJob(row.id, { result, reason })
    ElMessage.success('已操作')
    await load()
  } catch (e) { ElMessage.error(e.msg || e) }
}

onMounted(load)
</script>
