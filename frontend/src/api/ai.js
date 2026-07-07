import api from './index'

export const aiApi = {
  suggestTry: (keep, problem) =>
    api.post('/ai/suggest-try', { keep, problem }).then(r => r.data),

  getGuide: (field) =>
    api.get(`/ai/guide/${field}`).then(r => r.data)
}
