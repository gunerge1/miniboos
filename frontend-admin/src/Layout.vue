<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="brand"><span class="mb-gradient-text">miniboos</span> 管理后台</div>
      <el-menu router :default-active="$route.path" class="menu" :rounded="true">
        <el-menu-item index="/dashboard"><span class="mi">📊</span>数据看板</el-menu-item>
        <el-menu-item index="/companies"><span class="mi">🏢</span>企业审核</el-menu-item>
        <el-menu-item index="/jobs"><span class="mi">💼</span>职位审核</el-menu-item>
        <el-menu-item index="/users"><span class="mi">👥</span>用户管理</el-menu-item>
        <el-menu-item index="/dicts"><span class="mi">📖</span>字典管理</el-menu-item>
      </el-menu>
      <div class="aside-foot">v1.0 · 清新系</div>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="header-tip">平台管理员 · 只管系统，不管运营明细（手机号脱敏）</span>
        <el-button text @click="logout">退出登录</el-button>
      </el-header>
      <el-main class="main"><router-view /></el-main>
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
.aside { background: #fff; border-right: 1px solid #eef2f7;
  display: flex; flex-direction: column; }
.brand { height: 64px; display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 16px; color: var(--mb-text);
  border-bottom: 1px solid #eef2f7; }
.menu { border-right: none; flex: 1; padding: 8px; }
.menu :deep(.el-menu-item) { border-radius: 10px; margin-bottom: 4px; height: 46px; }
.menu :deep(.el-menu-item.is-active) { background: var(--el-color-primary-light-9); font-weight: 600; }
.mi { margin-right: 8px; }
.aside-foot { padding: 14px; text-align: center; color: #c0c8d4; font-size: 12px; }
.header { display: flex; justify-content: space-between; align-items: center;
  background: #fff; border-bottom: 1px solid #eef2f7; }
.header-tip { color: var(--mb-sub); font-size: 13px; }
.main { background: var(--mb-bg); }
</style>
