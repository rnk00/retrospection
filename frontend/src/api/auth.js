import api from './index'

export const authApi = {
  getMe: () => api.get('/auth/me').then(r => r.data)
}
