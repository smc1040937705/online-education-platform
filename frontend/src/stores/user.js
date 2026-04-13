import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getProfile } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const loginAction = async (credentials) => {
    const res = await login(credentials)
    setToken(res.data.token)
    await fetchUserInfo()
    return res
  }

  const fetchUserInfo = async () => {
    const res = await getProfile()
    userInfo.value = res.data
    return res
  }

  const logout = () => {
    clearToken()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    setToken,
    clearToken,
    loginAction,
    fetchUserInfo,
    logout
  }
})
