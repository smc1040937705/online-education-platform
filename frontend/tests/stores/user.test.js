import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '@/stores/user'
import * as userApi from '@/api/user'

vi.mock('@/api/user', () => ({
  login: vi.fn(),
  getProfile: vi.fn()
}))

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    localStorage.clear()
  })

  describe('State Initialization', () => {
    it('should initialize with default values when no token in localStorage', () => {
      const store = useUserStore()
      expect(store.token).toBe('')
      expect(store.userInfo).toBeNull()
    })

    it('should initialize with token from localStorage', () => {
      localStorage.setItem('token', 'test-token-123')
      const store = useUserStore()
      expect(store.token).toBe('test-token-123')
    })
  })

  describe('Getters', () => {
    it('isLoggedIn should return true when token exists', () => {
      const store = useUserStore()
      store.token = 'valid-token'
      expect(store.isLoggedIn).toBe(true)
    })

    it('isLoggedIn should return false when token is empty', () => {
      const store = useUserStore()
      store.token = ''
      expect(store.isLoggedIn).toBe(false)
    })

    it('isAdmin should return true when user role is 1', () => {
      const store = useUserStore()
      store.userInfo = { role: 1 }
      expect(store.isAdmin).toBe(true)
    })

    it('isAdmin should return false when user role is not 1', () => {
      const store = useUserStore()
      store.userInfo = { role: 0 }
      expect(store.isAdmin).toBe(false)
    })

    it('isAdmin should return false when userInfo is null', () => {
      const store = useUserStore()
      store.userInfo = null
      expect(store.isAdmin).toBe(false)
    })
  })

  describe('Actions', () => {
    describe('setToken', () => {
      it('should update token and save to localStorage', () => {
        const store = useUserStore()
        store.setToken('new-token-456')
        expect(store.token).toBe('new-token-456')
        expect(localStorage.setItem).toHaveBeenCalledWith('token', 'new-token-456')
      })
    })

    describe('clearToken', () => {
      it('should clear token, userInfo and remove from localStorage', () => {
        const store = useUserStore()
        store.token = 'existing-token'
        store.userInfo = { id: 1, username: 'test' }
        
        store.clearToken()
        
        expect(store.token).toBe('')
        expect(store.userInfo).toBeNull()
        expect(localStorage.removeItem).toHaveBeenCalledWith('token')
      })
    })

    describe('logout', () => {
      it('should call clearToken', () => {
        const store = useUserStore()
        store.token = 'existing-token'
        store.userInfo = { id: 1, username: 'test' }
        
        store.logout()
        
        expect(store.token).toBe('')
        expect(store.userInfo).toBeNull()
        expect(localStorage.removeItem).toHaveBeenCalledWith('token')
      })
    })

    describe('loginAction', () => {
      it('should login successfully and fetch user info', async () => {
        const mockCredentials = { username: 'test', password: '123456' }
        const mockLoginResponse = {
          code: 200,
          data: { token: 'login-token-789' },
          message: 'success'
        }
        const mockProfileResponse = {
          code: 200,
          data: { id: 1, username: 'test', role: 0 }
        }
        
        userApi.login.mockResolvedValue(mockLoginResponse)
        userApi.getProfile.mockResolvedValue(mockProfileResponse)
        
        const store = useUserStore()
        
        const result = await store.loginAction(mockCredentials)
        
        expect(userApi.login).toHaveBeenCalledWith(mockCredentials)
        expect(store.token).toBe('login-token-789')
        expect(localStorage.setItem).toHaveBeenCalledWith('token', 'login-token-789')
        expect(userApi.getProfile).toHaveBeenCalled()
        expect(store.userInfo).toEqual({ id: 1, username: 'test', role: 0 })
        expect(result).toEqual(mockLoginResponse)
      })

      it('should throw error when login fails', async () => {
        const mockCredentials = { username: 'test', password: 'wrong' }
        const mockError = new Error('用户名或密码错误')
        
        userApi.login.mockRejectedValue(mockError)
        
        const store = useUserStore()
        
        await expect(store.loginAction(mockCredentials)).rejects.toThrow('用户名或密码错误')
        expect(store.token).toBe('')
        expect(store.userInfo).toBeNull()
      })
    })

    describe('fetchUserInfo', () => {
      it('should fetch and update user info successfully', async () => {
        const mockProfileResponse = {
          code: 200,
          data: { id: 1, username: 'testuser', email: 'test@example.com' }
        }
        
        userApi.getProfile.mockResolvedValue(mockProfileResponse)
        
        const store = useUserStore()
        const result = await store.fetchUserInfo()
        
        expect(userApi.getProfile).toHaveBeenCalled()
        expect(store.userInfo).toEqual({ id: 1, username: 'testuser', email: 'test@example.com' })
        expect(result).toEqual(mockProfileResponse)
      })

      it('should throw error when fetching profile fails', async () => {
        const mockError = new Error('获取用户信息失败')
        userApi.getProfile.mockRejectedValue(mockError)
        
        const store = useUserStore()
        
        await expect(store.fetchUserInfo()).rejects.toThrow('获取用户信息失败')
        expect(store.userInfo).toBeNull()
      })
    })
  })
})
