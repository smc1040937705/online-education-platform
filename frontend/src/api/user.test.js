import { describe, it, expect, vi, beforeEach } from 'vitest'
import { login, register, getProfile, updateProfile, updatePassword, recharge } from './user'
import request from '@/utils/request'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('User API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('应该发送登录请求', async () => {
      const data = { username: 'test', password: '123456' }
      const response = { data: { token: 'jwt-token' } }
      request.mockResolvedValue(response)

      const result = await login(data)

      expect(request).toHaveBeenCalledWith({
        url: '/auth/login',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })

    it('登录失败时应该抛出错误', async () => {
      const data = { username: 'test', password: 'wrong' }
      const error = new Error('用户名或密码错误')
      request.mockRejectedValue(error)

      await expect(login(data)).rejects.toThrow('用户名或密码错误')
    })
  })

  describe('register', () => {
    it('应该发送注册请求', async () => {
      const data = { username: 'newuser', password: '123456', email: 'test@example.com' }
      const response = { data: { id: 1, username: 'newuser' } }
      request.mockResolvedValue(response)

      const result = await register(data)

      expect(request).toHaveBeenCalledWith({
        url: '/auth/register',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })
  })

  describe('getProfile', () => {
    it('应该获取用户资料', async () => {
      const response = { data: { id: 1, username: 'test', email: 'test@example.com' } }
      request.mockResolvedValue(response)

      const result = await getProfile()

      expect(request).toHaveBeenCalledWith({
        url: '/user/profile',
        method: 'get'
      })
      expect(result).toEqual(response)
    })
  })

  describe('updateProfile', () => {
    it('应该更新用户资料', async () => {
      const data = { email: 'new@example.com', phone: '13800138000' }
      const response = { data: { id: 1, email: 'new@example.com', phone: '13800138000' } }
      request.mockResolvedValue(response)

      const result = await updateProfile(data)

      expect(request).toHaveBeenCalledWith({
        url: '/user/profile',
        method: 'put',
        data
      })
      expect(result).toEqual(response)
    })
  })

  describe('updatePassword', () => {
    it('应该更新密码', async () => {
      const data = { oldPassword: 'old123', newPassword: 'new123' }
      const response = { data: { success: true } }
      request.mockResolvedValue(response)

      const result = await updatePassword(data)

      expect(request).toHaveBeenCalledWith({
        url: '/user/password',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })
  })

  describe('recharge', () => {
    it('应该发送充值请求', async () => {
      const data = { amount: 100 }
      const response = { data: { balance: 200 } }
      request.mockResolvedValue(response)

      const result = await recharge(data)

      expect(request).toHaveBeenCalledWith({
        url: '/user/recharge',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })
  })
})
