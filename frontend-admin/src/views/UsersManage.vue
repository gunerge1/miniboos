<template>
  <div>
    <div style="margin-bottom:12px; display:flex; gap:10px">
      <el-radio-group v-model="role" @change="load">
        <el-radio-button value="">全部角色</el-radio-button>
        <el-radio-button value="CANDIDATE">牛人</el-radio-button>
        <el-radio-button value="HR">HR</el-radio-button>
      </el-radio-group>
      <el-input v-model="kw" placeholder="搜昵称/手机号" style="width:220px" clearable @keyup.enter="load" />
      <el-button @click="load">搜索</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="phoneMasked" label="手机号（脱敏）" width="160" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="role" label="角色" width="110">
        <template #default="{ row }">
          {{ row.role === 'CANDIDATE' ? '牛人' : 'HR' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '已禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="170">
        <template #default="{ row }">{{ (row.createdAt || '').slice(0, 10) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="110">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="danger" @click="toggle(row, 0)">禁用</el-button>
          <el-button v-else size="small" type="success" @click="toggle(row, 1)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { admin } from '../api'

const role = ref('')
const kw = ref('')
const list = ref([])

const load = async () => {
  const res = await admin.users({ role: role.value || undefined, kw: kw.value || undefined, page: 1, size: 50 })
  list.value = res.data.list
}

const toggle = async (row, status) => {
  await admin.updateUserStatus(row.id, status)
  row.status = status
  ElMessage.success(status === 1 ? '已启用' : '已禁用')
}

onMounted(load)
</script>
