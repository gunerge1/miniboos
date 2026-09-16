import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('./views/Login.vue') },
  {
    path: '/',
    component: () => import('./Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('./views/Dashboard.vue') },
      { path: 'companies', component: () => import('./views/CompaniesAudit.vue') },
      { path: 'jobs', component: () => import('./views/JobsAudit.vue') },
      { path: 'users', component: () => import('./views/UsersManage.vue') },
      { path: 'dicts', component: () => import('./views/DictsManage.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach(to => {
  if (to.path !== '/login' && !localStorage.getItem('admin_token')) return '/login'
})

export default router
