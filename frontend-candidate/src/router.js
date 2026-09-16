import { createRouter, createWebHashHistory } from 'vue-router'

// hash路由：静态托管零配置（EdgeOne不用配history回退）
const routes = [
  { path: '/login', component: () => import('./views/Login.vue') },
  { path: '/', component: () => import('./views/Jobs.vue') },
  { path: '/job/:id', component: () => import('./views/JobDetail.vue') },
  { path: '/my-applications', component: () => import('./views/MyApplications.vue') },
  { path: '/chat/:appId', component: () => import('./views/Chat.vue') },
  { path: '/my-resume', component: () => import('./views/MyResume.vue') }
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach(to => {
  if (to.path !== '/login' && !localStorage.getItem('token')) return '/login'
})

export default router
