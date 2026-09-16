<template>
  <div>
    <el-alert type="info" :closable="false" style="margin-bottom:12px"
      title="运营词汇进字典，系统词汇进代码（状态机/角色永不入字典）——删除推荐用停用代替" />
    <el-tabs v-model="tab" @tab-change="load">
      <el-tab-pane v-for="t in TYPES" :key="t.value" :label="t.label" :name="t.value" />
    </el-tabs>

    <el-button type="primary" size="small" style="margin-bottom:10px" @click="edit(null)">新增字典项</el-button>
    <el-table :data="list" border>
      <el-table-column prop="dictType" label="类型" width="110" />
      <el-table-column prop="code" label="code（存储值）" width="140" />
      <el-table-column prop="label" label="显示名" />
      <el-table-column prop="sort" label="排序" width="70" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggle(row)">
            {{ row.status === 1 ? '停用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="del(row)">删</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editing ? '编辑' : '新增'" width="420">
      <el-form label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="form.dictType" :disabled="!!editing" style="width:100%">
            <el-option v-for="t in TYPES" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="code">
          <el-input v-model="form.code" :disabled="!!editing" placeholder="英文小写，如 ai_dev" />
        </el-form-item>
        <el-form-item label="显示名">
          <el-input v-model="form.label" placeholder="如 人工智能开发" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { admin } from '../api'

const TYPES = [
  { label: '岗位方向', value: 'category' }, { label: '行业', value: 'industry' },
  { label: '城市', value: 'city' }, { label: '学历', value: 'education' }
]
const tab = ref('category')
const list = ref([])
const dialog = ref(false)
const editing = ref(null)
const form = ref({ dictType: 'category', code: '', label: '', sort: 0 })

const load = async () => {
  const res = await admin.dicts(tab.value)
  list.value = res.data
}

const edit = row => {
  editing.value = row
  form.value = row ? { ...row } : { dictType: tab.value, code: '', label: '', sort: 0 }
  dialog.value = true
}

const save = async () => {
  try {
    if (editing.value) {
      await admin.updateDict(editing.value.id, { label: form.value.label, sort: form.value.sort })
    } else {
      await admin.addDict(form.value)
    }
    ElMessage.success('已保存')
    dialog.value = false
    await load()
  } catch (e) { ElMessage.error(e.msg || e) }
}

const toggle = async row => {
  await admin.updateDict(row.id, { label: row.label, sort: row.sort, status: row.status === 1 ? 0 : 1 })
  await load()
}

const del = async row => {
  try {
    await ElMessageBox.confirm('删除后存量数据的该值将无label翻译，建议用停用。确定删？', '确认', { type: 'warning' })
  } catch { return }
  await admin.deleteDict(row.id)
  await load()
}

onMounted(load)
</script>
