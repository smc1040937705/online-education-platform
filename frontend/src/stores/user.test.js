import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from './user'
import * as userApi from '@/api/user'

vi.mock('@/api/user', () => ({
  login: vi.fn(),
  getProfile: vi.fn()
}))

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  describe('初始化', () => {
    it('应该正确初始化状态', () => {
      const store = useUserStore()
      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
      expect(store.isLoggedIn).toBe(false)
      expect(store.isAdmin).toBe(false)
    })

    it('应该从 localStorage 恢复 token', () => {
      localStorage.setItem('token', 'test-token')
      const store = useUserStore()
      expect(store.token).toBe('test-token')
      expect(store.isLoggedIn).toBe(true)
    })
  })

  describe('setToken', () => {
    it('应该设置 token 并保存到 localStorage', () => {
      const store = useUserStore()
      store.setToken('new-token')
      expect(store.token).toBe('new-token')
      expect(localStorage.getItem('token')).toBe('new-token')
      expect(store.isLoggedIn).toBe(true)
    })
  })

  describe('clearToken', () => {
    it('应该清除 token 和用户信息', () => {
      const store = useUserStore()
      store.setToken('test-token')
      store.userInfo = { id: 1, username: 'test' }
      
      store.clearToken()
      
      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
      expect(localStorage.getItem('token')).toBeNull()
      expect(store.isLoggedIn).toBe(false)
    })
  })

  describe('loginAction', () => {
    it('应该成功登录并获取用户信息', async () => {
      const store = useUserStore()
      const credentials = { username: 'test', password: '123456' }
      const loginResponse = { data: { token: 'jwt-token-123' } }
      const profileResponse = { data: { id: 1, username: 'test', role: 0 } }
      
      userApi.login.mockResolvedValue(loginResponse)
      userApi.getProfile.mockResolvedValue(profileResponse)
      
      const result = await store.loginAction(credentials)
      
      expect(userApi.login).toHaveBeenCalledWith(credentials)
      expect(userApi.getProfile).toHaveBeenCalled()
      expect(store.token).toBe('jwt-token-123')
      expect(store.userInfo).toEqual({ id: 1, username: 'test', role: 0 })
      expect(result).toEqual(loginResponse)
    })

    it('登录失败时应该抛出错误', async () => {
      const store = useUserStore()
      const credentials = { username: 'test', password: 'wrong' }
      const error = new Error('用户名或密码错误')
      
      userApi.login.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('用户名或密码错误')
      expect(store.token).toBe('')
      expect(store.isLoggedIn).toBe(false)
    })
  })

  describe('fetchUserInfo', () => {
    it('应该成功获取用户信息', async () => {
      const store = useUserStore()
      const profileResponse = { data: { id: 1, username: 'test', role: 1 } }
      
      userApi.getProfile.mockResolvedValue(profileResponse)
      
      const result = await store.fetchUserInfo()
      
      expect(userApi.getProfile).toHaveBeenCalled()
      expect(store.userInfo).toEqual({ id: 1, username: 'test', role: 1 })
      expect(store.isAdmin).toBe(true)
      expect(result).toEqual(profileResponse)
    })

    it('获取用户信息失败时应该抛出错误', async () => {
      const store = useUserStore()
      const error = new Error('未授权')
      
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.fetchUserInfo()).rejects.toThrow('未授权')
    })
  })

  describe('logout', () => {
    it('应该清除所有用户数据', () => {
      const store = useUserStore()
      store.setToken('test-token')
      store.userInfo = { id: 1, username: 'test' }
      
      store.logout()
      
      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
      expect(store.isLoggedIn).toBe(false)
      expect(localStorage.getItem('token')).toBeNull()
    })
  })

  describe('isAdmin 计算属性', () => {
    it('当 role 为 1 时应该返回 true', () => {
      const store = useUserStore()
      store.userInfo = { id: 1, username: 'admin', role: 1 }
      expect(store.isAdmin).toBe(true)
    })

    it('当 role 为 0 时应该返回 false', () => {
      const store = useUserStore()
      store.userInfo = { id: 1, username: 'user', role: 0 }
      expect(store.isAdmin).toBe(false)
    })

    it('当 userInfo 为 null 时应该返回 false', () => {
      const store = useUserStore()
      expect(store.isAdmin).toBe(false)
    })
  })
})
