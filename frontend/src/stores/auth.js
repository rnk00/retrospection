import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  const initialized = ref(false)

  const isLoggedIn = computed(() => !!user.value)

  async function init() {
    if (token.value) {
      try {
        user.value = await authApi.getMe()
      } catch {
        logout()
      }
    }
    initialized.value = true
  }

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
  }

  return { user, token, isLoggedIn, initialized, init, setToken, logout }
})
