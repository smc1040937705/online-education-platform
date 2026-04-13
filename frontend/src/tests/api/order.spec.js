import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/utils/request'
import { getOrders, getOrder, createOrder, cancelOrder, payOrder, receiveOrder } from '@/api/order'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('Order API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getOrders', () => {
    it('应该获取订单列表', async () => {
      const mockResponse = { data: { records: [], total: 0 } }
      request.mockResolvedValue(mockResponse)
      
      const params = { page: 1, size: 10 }
      const result = await getOrders(params)
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders',
        method: 'get',
        params
      })
      expect(result).toEqual(mockResponse)
    })

    it('应该支持不带参数获取订单列表', async () => {
      const mockResponse = { data: { records: [], total: 0 } }
      request.mockResolvedValue(mockResponse)
      
      const result = await getOrders()
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders',
        method: 'get',
        params: undefined
      })
      expect(result).toEqual(mockResponse)
    })

    it('获取订单列表失败-未授权', async () => {
      const error = new Error('未授权')
      error.response = { status: 401 }
      request.mockRejectedValue(error)
      
      await expect(getOrders()).rejects.toThrow('未授权')
    })

    it('获取订单列表失败-网络错误', async () => {
      const error = new Error('Network Error')
      request.mockRejectedValue(error)
      
      await expect(getOrders()).rejects.toThrow('Network Error')
    })
  })

  describe('getOrder', () => {
    it('应该获取单个订单详情', async () => {
      const mockResponse = { data: { id: 1, orderNo: '202401010001' } }
      request.mockResolvedValue(mockResponse)
      
      const result = await getOrder(1)
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders/1',
        method: 'get'
      })
      expect(result).toEqual(mockResponse)
    })

    it('获取订单详情失败-订单不存在', async () => {
      const error = new Error('订单不存在')
      error.response = { status: 404, data: { message: '订单不存在' } }
      request.mockRejectedValue(error)
      
      await expect(getOrder(999)).rejects.toThrow('订单不存在')
    })

    it('获取订单详情失败-无权访问', async () => {
      const error = new Error('无权访问该订单')
      error.response = { status: 403 }
      request.mockRejectedValue(error)
      
      await expect(getOrder(1)).rejects.toThrow('无权访问该订单')
    })
  })

  describe('createOrder', () => {
    it('应该创建订单', async () => {
      const mockResponse = { data: { id: 1, orderNo: '202401010001' } }
      request.mockResolvedValue(mockResponse)
      
      const data = { addressId: 1, cartIds: [1, 2] }
      const result = await createOrder(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('创建订单失败-地址不存在', async () => {
      const error = new Error('地址不存在')
      error.response = { status: 400, data: { message: '地址不存在' } }
      request.mockRejectedValue(error)
      
      const data = { addressId: 999, cartIds: [1, 2] }
      
      await expect(createOrder(data)).rejects.toThrow('地址不存在')
    })

    it('创建订单失败-购物车为空', async () => {
      const error = new Error('购物车为空')
      error.response = { status: 400, data: { message: '购物车为空' } }
      request.mockRejectedValue(error)
      
      const data = { addressId: 1, cartIds: [] }
      
      await expect(createOrder(data)).rejects.toThrow('购物车为空')
    })

    it('创建订单失败-商品库存不足', async () => {
      const error = new Error('商品库存不足')
      error.response = { status: 400, data: { message: '商品库存不足' } }
      request.mockRejectedValue(error)
      
      const data = { addressId: 1, cartIds: [1, 2] }
      
      await expect(createOrder(data)).rejects.toThrow('商品库存不足')
    })
  })

  describe('cancelOrder', () => {
    it('应该取消订单', async () => {
      const mockResponse = { data: { success: true } }
      request.mockResolvedValue(mockResponse)
      
      const result = await cancelOrder(1)
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders/1/cancel',
        method: 'post'
      })
      expect(result).toEqual(mockResponse)
    })

    it('取消订单失败-订单不存在', async () => {
      const error = new Error('订单不存在')
      error.response = { status: 404 }
      request.mockRejectedValue(error)
      
      await expect(cancelOrder(999)).rejects.toThrow('订单不存在')
    })

    it('取消订单失败-订单状态不允许取消', async () => {
      const error = new Error('订单状态不允许取消')
      error.response = { status: 400, data: { message: '订单状态不允许取消' } }
      request.mockRejectedValue(error)
      
      await expect(cancelOrder(1)).rejects.toThrow('订单状态不允许取消')
    })
  })

  describe('payOrder', () => {
    it('应该支付订单', async () => {
      const mockResponse = { data: { success: true } }
      request.mockResolvedValue(mockResponse)
      
      const result = await payOrder(1)
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders/1/pay',
        method: 'post'
      })
      expect(result).toEqual(mockResponse)
    })

    it('支付订单失败-订单不存在', async () => {
      const error = new Error('订单不存在')
      error.response = { status: 404 }
      request.mockRejectedValue(error)
      
      await expect(payOrder(999)).rejects.toThrow('订单不存在')
    })

    it('支付订单失败-余额不足', async () => {
      const error = new Error('余额不足')
      error.response = { status: 400, data: { message: '余额不足' } }
      request.mockRejectedValue(error)
      
      await expect(payOrder(1)).rejects.toThrow('余额不足')
    })

    it('支付订单失败-订单状态不允许支付', async () => {
      const error = new Error('订单状态不允许支付')
      error.response = { status: 400 }
      request.mockRejectedValue(error)
      
      await expect(payOrder(1)).rejects.toThrow('订单状态不允许支付')
    })
  })

  describe('receiveOrder', () => {
    it('应该确认收货', async () => {
      const mockResponse = { data: { success: true } }
      request.mockResolvedValue(mockResponse)
      
      const result = await receiveOrder(1)
      
      expect(request).toHaveBeenCalledWith({
        url: '/orders/1/receive',
        method: 'post'
      })
      expect(result).toEqual(mockResponse)
    })

    it('确认收货失败-订单不存在', async () => {
      const error = new Error('订单不存在')
      error.response = { status: 404 }
      request.mockRejectedValue(error)
      
      await expect(receiveOrder(999)).rejects.toThrow('订单不存在')
    })

    it('确认收货失败-订单状态不允许收货', async () => {
      const error = new Error('订单状态不允许收货')
      error.response = { status: 400, data: { message: '订单状态不允许收货' } }
      request.mockRejectedValue(error)
      
      await expect(receiveOrder(1)).rejects.toThrow('订单状态不允许收货')
    })
  })
})
