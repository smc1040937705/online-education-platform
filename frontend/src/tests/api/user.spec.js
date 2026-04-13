import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/utils/request'
import { login, register, getProfile, updateProfile, updatePassword, recharge } from '@/api/user'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('User API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('应该发送登录请求', async () => {
      const mockResponse = { data: { token: 'test-token' } }
      request.mockResolvedValue(mockResponse)
      
      const data = { username: 'testuser', password: 'password123' }
      const result = await login(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/auth/login',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('登录失败-用户名或密码错误', async () => {
      const error = new Error('用户名或密码错误')
      error.response = { status: 401, data: { message: '用户名或密码错误' } }
      request.mockRejectedValue(error)
      
      const data = { username: 'testuser', password: 'wrongpassword' }
      
      await expect(login(data)).rejects.toThrow('用户名或密码错误')
      expect(request).toHaveBeenCalledWith({
        url: '/auth/login',
        method: 'post',
        data
      })
    })

    it('登录失败-账户被禁用', async () => {
      const error = new Error('账户已被禁用')
      error.response = { status: 403, data: { message: '账户已被禁用' } }
      request.mockRejectedValue(error)
      
      const data = { username: 'disableduser', password: 'password123' }
      
      await expect(login(data)).rejects.toThrow('账户已被禁用')
    })

    it('登录失败-网络错误', async () => {
      const error = new Error('Network Error')
      request.mockRejectedValue(error)
      
      const data = { username: 'testuser', password: 'password123' }
      
      await expect(login(data)).rejects.toThrow('Network Error')
    })
  })

  describe('register', () => {
    it('应该发送注册请求', async () => {
      const mockResponse = { data: { id: 1, username: 'newuser' } }
      request.mockResolvedValue(mockResponse)
      
      const data = { username: 'newuser', password: 'password123', email: 'test@example.com' }
      const result = await register(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/auth/register',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('注册失败-用户名已存在', async () => {
      const error = new Error('用户名已存在')
      error.response = { status: 400, data: { message: '用户名已存在' } }
      request.mockRejectedValue(error)
      
      const data = { username: 'existinguser', password: 'password123', email: 'test@example.com' }
      
      await expect(register(data)).rejects.toThrow('用户名已存在')
    })

    it('注册失败-邮箱格式不正确', async () => {
      const error = new Error('邮箱格式不正确')
      error.response = { status: 400, data: { message: '邮箱格式不正确' } }
      request.mockRejectedValue(error)
      
      const data = { username: 'newuser', password: 'password123', email: 'invalid-email' }
      
      await expect(register(data)).rejects.toThrow('邮箱格式不正确')
    })
  })

  describe('getProfile', () => {
    it('应该获取用户信息', async () => {
      const mockResponse = { data: { id: 1, username: 'testuser' } }
      request.mockResolvedValue(mockResponse)
      
      const result = await getProfile()
      
      expect(request).toHaveBeenCalledWith({
        url: '/user/profile',
        method: 'get'
      })
      expect(result).toEqual(mockResponse)
    })

    it('获取用户信息失败-未授权', async () => {
      const error = new Error('未授权')
      error.response = { status: 401, data: { message: '未授权' } }
      request.mockRejectedValue(error)
      
      await expect(getProfile()).rejects.toThrow('未授权')
    })

    it('获取用户信息失败-token过期', async () => {
      const error = new Error('Token已过期')
      error.response = { status: 401, data: { message: 'Token已过期' } }
      request.mockRejectedValue(error)
      
      await expect(getProfile()).rejects.toThrow('Token已过期')
    })
  })

  describe('updateProfile', () => {
    it('应该更新用户信息', async () => {
      const mockResponse = { data: { id: 1, email: 'new@example.com' } }
      request.mockResolvedValue(mockResponse)
      
      const data = { email: 'new@example.com', phone: '13800138000' }
      const result = await updateProfile(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/user/profile',
        method: 'put',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('更新用户信息失败-未授权', async () => {
      const error = new Error('未授权')
      error.response = { status: 401 }
      request.mockRejectedValue(error)
      
      const data = { email: 'new@example.com' }
      
      await expect(updateProfile(data)).rejects.toThrow('未授权')
    })
  })

  describe('updatePassword', () => {
    it('应该更新密码', async () => {
      const mockResponse = { data: { success: true } }
      request.mockResolvedValue(mockResponse)
      
      const data = { oldPassword: 'old123', newPassword: 'new123' }
      const result = await updatePassword(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/user/password',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('更新密码失败-原密码错误', async () => {
      const error = new Error('原密码错误')
      error.response = { status: 400, data: { message: '原密码错误' } }
      request.mockRejectedValue(error)
      
      const data = { oldPassword: 'wrongold', newPassword: 'new123' }
      
      await expect(updatePassword(data)).rejects.toThrow('原密码错误')
    })
  })

  describe('recharge', () => {
    it('应该发送充值请求', async () => {
      const mockResponse = { data: { balance: 100 } }
      request.mockResolvedValue(mockResponse)
      
      const data = { amount: 100 }
      const result = await recharge(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/user/recharge',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('充值失败-金额无效', async () => {
      const error = new Error('充值金额必须大于0')
      error.response = { status: 400, data: { message: '充值金额必须大于0' } }
      request.mockRejectedValue(error)
      
      const data = { amount: -100 }
      
      await expect(recharge(data)).rejects.toThrow('充值金额必须大于0')
    })
  })
})
