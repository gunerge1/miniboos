<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="brand">miniboos 管理后台</div>
      <el-menu router :default-active="$route.path" class="menu">
        <el-menu-item index="/dashboard"><el-icon>📊</el-icon>数据看板</el-menu-item>
        <el-menu-item index="/companies"><el-icon>🏢</el-icon>企业审核</el-menu-item>
        <el-menu-item index="/jobs"><el-icon>💼</el-icon>职位审核</el-menu-item>
        <el-menu-item index="/users"><el-icon>👥</el-icon>用户管理</el-menu-item>
        <el-menu-item index="/dicts"><el-icon>📖</el-icon>字典管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>平台管理员：只管系统，不管运营明细（手机号脱敏）</span>
        <el-button text @click="logout">退出登录</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const logout = () => {
  localStorage.removeItem('admin_token')
  router.replace('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.brand { height: 60px; display: flex; align-items: center; justify-content: center;
  font-weight: 700; color: #409eff; border-bottom: 1px solid #eee; }
.menu { border-right: none; }
.header { display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid #eee; color: #909399; font-size: 13px; }
</style>
