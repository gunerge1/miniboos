import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 15000 })

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

export const companyApi = {
  submit: data => api.post('/companies', data),
  my: () => api.get('/my/company')
}

export const jobApi = {
  publish: data => api.post('/jobs', data),
  my: () => api.get('/my/jobs'),
  switchStatus: (id, status) => api.put(`/jobs/${id}/status`, { status }),
  candidates: id => api.get(`/my/jobs/${id}/applications`),
  updateAppStatus: (id, status) => api.put(`/applications/${id}/status`, { status }),
  resumeOf: appId => api.get(`/applications/${appId}/resume`)
}

export const messageApi = {
  list: appId => api.get(`/applications/${appId}/messages`),
  send: (appId, content) => api.post(`/applications/${appId}/messages`, { content })
}

export default api
