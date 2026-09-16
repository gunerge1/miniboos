import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('token')
  if (token) cfg.headers.Authorization = 'Bearer ' + token
  return cfg
})

api.interceptors.response.use(
  resp => resp.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      location.hash = '#/login'
    }
    return Promise.reject(err.response?.data?.msg || '网络异常，请稍后再试')
  }
)

export const auth = {
  register: data => api.post('/auth/register', data),
  login: data => api.post('/auth/login', data)
}

export const dicts = type => api.get('/dicts?type=' + type)

export const jobs = {
  list: params => api.get('/jobs', { params }),
  detail: id => api.get(`/jobs/${id}`)
}

export const resumeApi = {
  get: () => api.get('/my/resume'),
  save: data => api.put('/my/resume', data),
  addExp: data => api.post('/my/resume/experiences', data),
  updateExp: (id, data) => api.put(`/my/resume/experiences/${id}`, data),
  delExp: id => api.delete(`/my/resume/experiences/${id}`)
}

export const appApi = {
  apply: jobId => api.post('/applications', { jobId }),
  my: () => api.get('/my/applications'),
  messages: appId => api.get(`/applications/${appId}/messages`),
  send: (appId, content) => api.post(`/applications/${appId}/messages`, { content })
}

export default api
