import api from './index'

export const retrospectApi = {
  getCalendar: (year, month) =>
    api.get('/retrospects/calendar', { params: { year, month } }).then(r => r.data),

  getByDate: (date) =>
    api.get(`/retrospects/date/${date}`).then(r => r.data),

  getById: (id) =>
    api.get(`/retrospects/${id}`).then(r => r.data),

  create: (data) =>
    api.post('/retrospects', data).then(r => r.data),

  update: (id, data) =>
    api.put(`/retrospects/${id}`, data).then(r => r.data),

  delete: (id) =>
    api.delete(`/retrospects/${id}`)
}
