import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 15000 })

api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('admin_token')
  if (token) cfg.headers.Authorization = 'Bearer ' + token
  return cfg
})

api.interceptors.response.use(
  resp => resp.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('admin_token')
      location.hash = '#/login'
    }
    return Promise.reject(err.response?.data?.msg || '网络异常')
  }
)

export const auth = { login: data => api.post('/auth/login', data) }

export const admin = {
  companies: params => api.get('/admin/companies', { params }),
  auditCompany: (id, data) => api.put(`/admin/companies/${id}/audit`, data),
  jobs: params => api.get('/admin/jobs', { params }),
  auditJob: (id, data) => api.put(`/admin/jobs/${id}/audit`, data),
  users: params => api.get('/admin/users', { params }),
  updateUserStatus: (id, status) => api.put(`/admin/users/${id}/status`, { status }),
  stats: () => api.get('/admin/stats'),
  dicts: type => api.get('/admin/dicts', { params: type ? { type } : {} }),
  addDict: data => api.post('/admin/dicts', data),
  updateDict: (id, data) => api.put(`/admin/dicts/${id}`, data),
  deleteDict: id => api.delete(`/admin/dicts/${id}`)
}

export default api
