import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useUserStore } from '@/stores/user'
import { createPinia, setActivePinia } from 'pinia'
import * as userApi from '@/api/user'

vi.mock('@/api/user', () => ({
  login: vi.fn(),
  getProfile: vi.fn(),
  register: vi.fn(),
  updateProfile: vi.fn(),
  updatePassword: vi.fn(),
  recharge: vi.fn()
}))

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    global.localStorage.clear()
    vi.clearAllMocks()
  })

  describe('初始状态', () => {
    it('应该有正确的初始状态', () => {
      const store = useUserStore()
      
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
      expect(store.isLoggedIn).toBe(false)
      expect(store.isAdmin).toBe(false)
    })
  })

  describe('setToken', () => {
    it('应该正确设置 token 并保存到 localStorage', () => {
      const store = useUserStore()
      const testToken = 'test-token-123'
      
      store.setToken(testToken)
      
      expect(store.token).toBe(testToken)
      expect(localStorage.getItem('token')).toBe(testToken)
      expect(store.isLoggedIn).toBe(true)
    })
  })

  describe('clearToken', () => {
    it('应该清除 token 和 userInfo', () => {
      const store = useUserStore()
      
      store.setToken('test-token')
      store.userInfo = { id: 1, username: 'test' }
      
      store.clearToken()
      
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
      expect(localStorage.getItem('token')).toBe(null)
      expect(store.isLoggedIn).toBe(false)
    })
  })

  describe('loginAction', () => {
    it('登录成功应该设置 token 并获取用户信息', async () => {
      const store = useUserStore()
      const credentials = { username: 'testuser', password: 'password123' }
      const mockToken = 'mock-jwt-token'
      const mockUserInfo = { id: 1, username: 'testuser', role: 0 }
      
      userApi.login.mockResolvedValue({ data: { token: mockToken } })
      userApi.getProfile.mockResolvedValue({ data: mockUserInfo })
      
      const result = await store.loginAction(credentials)
      
      expect(userApi.login).toHaveBeenCalledWith(credentials)
      expect(store.token).toBe(mockToken)
      expect(store.userInfo).toEqual(mockUserInfo)
      expect(result).toEqual({ data: { token: mockToken } })
    })

    it('登录失败应该抛出错误', async () => {
      const store = useUserStore()
      const credentials = { username: 'testuser', password: 'wrongpassword' }
      const error = new Error('用户名或密码错误')
      
      userApi.login.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('用户名或密码错误')
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
    })

    it('登录成功但获取用户信息失败-应该保留token但userInfo为null', async () => {
      const store = useUserStore()
      const credentials = { username: 'testuser', password: 'password123' }
      const mockToken = 'mock-jwt-token'
      const error = new Error('获取用户信息失败')
      
      userApi.login.mockResolvedValue({ data: { token: mockToken } })
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('获取用户信息失败')
      
      expect(store.token).toBe(mockToken)
      expect(localStorage.getItem('token')).toBe(mockToken)
      expect(store.userInfo).toBe(null)
      expect(store.isLoggedIn).toBe(true)
    })

    it('登录成功但获取用户信息失败-网络错误', async () => {
      const store = useUserStore()
      const credentials = { username: 'testuser', password: 'password123' }
      const mockToken = 'mock-jwt-token'
      const error = new Error('Network Error')
      
      userApi.login.mockResolvedValue({ data: { token: mockToken } })
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('Network Error')
      
      expect(store.token).toBe(mockToken)
      expect(store.userInfo).toBe(null)
    })

    it('登录成功但获取用户信息失败-未授权', async () => {
      const store = useUserStore()
      const credentials = { username: 'testuser', password: 'password123' }
      const mockToken = 'mock-jwt-token'
      const error = new Error('未授权')
      
      userApi.login.mockResolvedValue({ data: { token: mockToken } })
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('未授权')
      
      expect(store.token).toBe(mockToken)
      expect(store.userInfo).toBe(null)
    })

    it('登录失败-账户被禁用', async () => {
      const store = useUserStore()
      const credentials = { username: 'disableduser', password: 'password123' }
      const error = new Error('账户已被禁用')
      
      userApi.login.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('账户已被禁用')
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
      expect(store.isLoggedIn).toBe(false)
    })

    it('登录失败-网络错误', async () => {
      const store = useUserStore()
      const credentials = { username: 'testuser', password: 'password123' }
      const error = new Error('Network Error')
      
      userApi.login.mockRejectedValue(error)
      
      await expect(store.loginAction(credentials)).rejects.toThrow('Network Error')
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
    })
  })

  describe('fetchUserInfo', () => {
    it('应该正确获取用户信息', async () => {
      const store = useUserStore()
      const mockUserInfo = { id: 1, username: 'testuser', role: 0 }
      
      userApi.getProfile.mockResolvedValue({ data: mockUserInfo })
      
      const result = await store.fetchUserInfo()
      
      expect(userApi.getProfile).toHaveBeenCalled()
      expect(store.userInfo).toEqual(mockUserInfo)
      expect(result).toEqual({ data: mockUserInfo })
    })

    it('获取用户信息失败应该抛出错误', async () => {
      const store = useUserStore()
      const error = new Error('未授权')
      
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.fetchUserInfo()).rejects.toThrow('未授权')
      expect(store.userInfo).toBe(null)
    })

    it('获取用户信息失败-网络错误', async () => {
      const store = useUserStore()
      const error = new Error('Network Error')
      
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.fetchUserInfo()).rejects.toThrow('Network Error')
    })

    it('获取用户信息失败-token过期', async () => {
      const store = useUserStore()
      const error = new Error('Token已过期')
      
      userApi.getProfile.mockRejectedValue(error)
      
      await expect(store.fetchUserInfo()).rejects.toThrow('Token已过期')
    })

    it('获取用户信息成功-管理员角色', async () => {
      const store = useUserStore()
      const mockUserInfo = { id: 1, username: 'admin', role: 1 }
      
      userApi.getProfile.mockResolvedValue({ data: mockUserInfo })
      
      await store.fetchUserInfo()
      
      expect(store.userInfo).toEqual(mockUserInfo)
      expect(store.isAdmin).toBe(true)
    })
  })

  describe('logout', () => {
    it('应该清除所有用户状态', () => {
      const store = useUserStore()
      
      store.setToken('test-token')
      store.userInfo = { id: 1, username: 'test' }
      
      store.logout()
      
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
      expect(localStorage.getItem('token')).toBe(null)
    })

    it('登出后再次登出不应该报错', () => {
      const store = useUserStore()
      
      store.logout()
      store.logout()
      
      expect(store.token).toBe('')
      expect(store.userInfo).toBe(null)
    })
  })

  describe('isAdmin', () => {
    it('用户角色为 1 时应该返回 true', () => {
      const store = useUserStore()
      
      store.userInfo = { id: 1, username: 'admin', role: 1 }
      
      expect(store.isAdmin).toBe(true)
    })

    it('用户角色不为 1 时应该返回 false', () => {
      const store = useUserStore()
      
      store.userInfo = { id: 1, username: 'user', role: 0 }
      
      expect(store.isAdmin).toBe(false)
    })

    it('用户信息为空时应该返回 false', () => {
      const store = useUserStore()
      
      store.userInfo = null
      
      expect(store.isAdmin).toBe(false)
    })

    it('用户角色为其他值时应该返回 false', () => {
      const store = useUserStore()
      
      store.userInfo = { id: 1, username: 'user', role: 2 }
      
      expect(store.isAdmin).toBe(false)
    })
  })

  describe('从 localStorage 恢复 token', () => {
    it('初始化时应该从 localStorage 读取 token', () => {
      localStorage.setItem('token', 'saved-token')
      
      const store = useUserStore()
      
      expect(store.token).toBe('saved-token')
      expect(store.isLoggedIn).toBe(true)
    })

    it('localStorage 为空时 token 应该为空', () => {
      const store = useUserStore()
      
      expect(store.token).toBe('')
      expect(store.isLoggedIn).toBe(false)
    })
  })

  describe('isLoggedIn', () => {
    it('有 token 时应该返回 true', () => {
      const store = useUserStore()
      
      store.setToken('test-token')
      
      expect(store.isLoggedIn).toBe(true)
    })

    it('没有 token 时应该返回 false', () => {
      const store = useUserStore()
      
      expect(store.isLoggedIn).toBe(false)
    })

    it('token 为空字符串时应该返回 false', () => {
      const store = useUserStore()
      
      store.token = ''
      
      expect(store.isLoggedIn).toBe(false)
    })
  })
})
