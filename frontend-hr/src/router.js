import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('./views/Login.vue') },
  { path: '/', component: () => import('./views/JobsManage.vue') },
  { path: '/company', component: () => import('./views/Company.vue') },
  { path: '/publish', component: () => import('./views/JobPublish.vue') },
  { path: '/candidates/:jobId', component: () => import('./views/Candidates.vue') },
  { path: '/resume/:appId', component: () => import('./views/ResumeView.vue') },
  { path: '/chat/:appId', component: () => import('./views/Chat.vue') }
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach(to => {
  if (to.path !== '/login' && !localStorage.getItem('token')) return '/login'
})

export default router
