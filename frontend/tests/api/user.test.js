import { describe, it, expect, beforeEach, vi } from 'vitest'
import MockAdapter from 'axios-mock-adapter'
import request from '@/utils/request'
import { login, register, getProfile, updateProfile, updatePassword, recharge } from '@/api/user'

const mock = new MockAdapter(request)

describe('User API', () => {
  beforeEach(() => {
    mock.reset()
  })

  describe('login', () => {
    it('should send POST request to /auth/login', async () => {
      const loginData = { username: 'testuser', password: '123456' }
      const mockResponse = {
        code: 200,
        data: { token: 'mock-token-123' },
        message: '登录成功'
      }

      mock.onPost('/api/auth/login', loginData).reply(200, mockResponse)

      const result = await login(loginData)

      expect(result).toEqual(mockResponse)
    })

    it('should handle login error', async () => {
      const loginData = { username: 'testuser', password: 'wrong' }
      const mockError = {
        code: 400,
        message: '用户名或密码错误'
      }

      mock.onPost('/api/auth/login', loginData).reply(200, mockError)

      try {
        await login(loginData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('用户名或密码错误')
      }
    })
  })

  describe('register', () => {
    it('should send POST request to /auth/register', async () => {
      const registerData = {
        username: 'newuser',
        password: '123456',
        email: 'test@example.com',
        phone: '13800138000'
      }
      const mockResponse = {
        code: 200,
        data: { id: 1, username: 'newuser' },
        message: '注册成功'
      }

      mock.onPost('/api/auth/register', registerData).reply(200, mockResponse)

      const result = await register(registerData)

      expect(result).toEqual(mockResponse)
    })

    it('should handle register error when username exists', async () => {
      const registerData = { username: 'existing', password: '123456' }
      const mockError = {
        code: 400,
        message: '用户名已存在'
      }

      mock.onPost('/api/auth/register', registerData).reply(200, mockError)

      try {
        await register(registerData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('用户名已存在')
      }
    })
  })

  describe('getProfile', () => {
    it('should send GET request to /user/profile', async () => {
      const mockResponse = {
        code: 200,
        data: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          role: 0
        },
        message: 'success'
      }

      mock.onGet('/api/user/profile').reply(200, mockResponse)

      const result = await getProfile()

      expect(result).toEqual(mockResponse)
    })

    it('should handle 401 unauthorized', async () => {
      mock.onGet('/api/user/profile').reply(401)

      try {
        await getProfile()
        expect.fail('Should throw error')
      } catch (error) {
        expect(error).toBeDefined()
      }
    })
  })

  describe('updateProfile', () => {
    it('should send PUT request to /user/profile', async () => {
      const updateData = {
        email: 'new@example.com',
        phone: '13900139000'
      }
      const mockResponse = {
        code: 200,
        data: { id: 1, username: 'testuser', email: 'new@example.com' },
        message: '更新成功'
      }

      mock.onPut('/api/user/profile', updateData).reply(200, mockResponse)

      const result = await updateProfile(updateData)

      expect(result).toEqual(mockResponse)
    })
  })

  describe('updatePassword', () => {
    it('should send POST request to /user/password', async () => {
      const passwordData = {
        oldPassword: '123456',
        newPassword: '654321'
      }
      const mockResponse = {
        code: 200,
        message: '密码修改成功'
      }

      mock.onPost('/api/user/password', passwordData).reply(200, mockResponse)

      const result = await updatePassword(passwordData)

      expect(result).toEqual(mockResponse)
    })

    it('should handle wrong old password error', async () => {
      const passwordData = {
        oldPassword: 'wrong',
        newPassword: '654321'
      }
      const mockError = {
        code: 400,
        message: '原密码错误'
      }

      mock.onPost('/api/user/password', passwordData).reply(200, mockError)

      try {
        await updatePassword(passwordData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('原密码错误')
      }
    })
  })

  describe('recharge', () => {
    it('should send POST request to /user/recharge', async () => {
      const rechargeData = { amount: 100 }
      const mockResponse = {
        code: 200,
        message: '充值成功'
      }

      mock.onPost('/api/user/recharge', rechargeData).reply(200, mockResponse)

      const result = await recharge(rechargeData)

      expect(result).toEqual(mockResponse)
    })

    it('should handle invalid amount error', async () => {
      const rechargeData = { amount: -50 }
      const mockError = {
        code: 400,
        message: '充值金额必须大于0'
      }

      mock.onPost('/api/user/recharge', rechargeData).reply(200, mockError)

      try {
        await recharge(rechargeData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('充值金额必须大于0')
      }
    })
  })
})
