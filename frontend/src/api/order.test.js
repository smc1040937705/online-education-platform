import { describe, it, expect, vi, beforeEach } from 'vitest'
import { getOrders, getOrder, createOrder, cancelOrder, payOrder, receiveOrder } from './order'
import request from '@/utils/request'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('Order API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getOrders', () => {
    it('应该获取订单列表', async () => {
      const params = { page: 1, size: 10 }
      const response = {
        data: {
          total: 2,
          list: [
            { id: 1, orderNo: '202401010001', totalAmount: 299.99, status: 0 },
            { id: 2, orderNo: '202401010002', totalAmount: 199.99, status: 1 }
          ]
        }
      }
      request.mockResolvedValue(response)

      const result = await getOrders(params)

      expect(request).toHaveBeenCalledWith({
        url: '/orders',
        method: 'get',
        params
      })
      expect(result).toEqual(response)
    })

    it('应该支持状态筛选', async () => {
      const params = { page: 1, size: 10, status: 0 }
      const response = {
        data: {
          total: 1,
          list: [{ id: 1, orderNo: '202401010001', status: 0 }]
        }
      }
      request.mockResolvedValue(response)

      const result = await getOrders(params)

      expect(request).toHaveBeenCalledWith({
        url: '/orders',
        method: 'get',
        params
      })
      expect(result.data.list[0].status).toBe(0)
    })
  })

  describe('getOrder', () => {
    it('应该获取单个订单详情', async () => {
      const id = 1
      const response = {
        data: {
          id: 1,
          orderNo: '202401010001',
          totalAmount: 299.99,
          status: 0,
          items: [
            { id: 1, productName: '商品1', quantity: 2, price: 99.99 }
          ]
        }
      }
      request.mockResolvedValue(response)

      const result = await getOrder(id)

      expect(request).toHaveBeenCalledWith({
        url: '/orders/1',
        method: 'get'
      })
      expect(result).toEqual(response)
    })

    it('获取不存在的订单应该抛出错误', async () => {
      const id = 999
      const error = new Error('订单不存在')
      request.mockRejectedValue(error)

      await expect(getOrder(id)).rejects.toThrow('订单不存在')
    })
  })

  describe('createOrder', () => {
    it('应该创建订单', async () => {
      const data = {
        addressId: 1,
        cartIds: [1, 2],
        remark: '请尽快发货'
      }
      const response = {
        data: {
          id: 3,
          orderNo: '202401010003',
          totalAmount: 399.99,
          status: 0
        }
      }
      request.mockResolvedValue(response)

      const result = await createOrder(data)

      expect(request).toHaveBeenCalledWith({
        url: '/orders',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })

    it('使用优惠券创建订单', async () => {
      const data = {
        addressId: 1,
        cartIds: [1],
        couponId: 1,
        remark: ''
      }
      const response = {
        data: {
          id: 4,
          orderNo: '202401010004',
          totalAmount: 199.99,
          discountAmount: 20,
          payAmount: 179.99,
          status: 0
        }
      }
      request.mockResolvedValue(response)

      const result = await createOrder(data)

      expect(result.data.discountAmount).toBe(20)
      expect(result.data.payAmount).toBe(179.99)
    })

    it('创建订单时库存不足应该抛出错误', async () => {
      const data = { addressId: 1, cartIds: [1] }
      const error = new Error('商品库存不足')
      request.mockRejectedValue(error)

      await expect(createOrder(data)).rejects.toThrow('商品库存不足')
    })
  })

  describe('cancelOrder', () => {
    it('应该取消待支付订单', async () => {
      const id = 1
      const response = { data: { success: true } }
      request.mockResolvedValue(response)

      const result = await cancelOrder(id)

      expect(request).toHaveBeenCalledWith({
        url: '/orders/1/cancel',
        method: 'post'
      })
      expect(result).toEqual(response)
    })

    it('取消已支付订单应该抛出错误', async () => {
      const id = 2
      const error = new Error('订单状态不允许取消')
      request.mockRejectedValue(error)

      await expect(cancelOrder(id)).rejects.toThrow('订单状态不允许取消')
    })
  })

  describe('payOrder', () => {
    it('应该支付待支付订单', async () => {
      const id = 1
      const response = { data: { success: true, payTime: '2024-01-01T10:00:00' } }
      request.mockResolvedValue(response)

      const result = await payOrder(id)

      expect(request).toHaveBeenCalledWith({
        url: '/orders/1/pay',
        method: 'post'
      })
      expect(result).toEqual(response)
    })

    it('余额不足时应该抛出错误', async () => {
      const id = 1
      const error = new Error('余额不足')
      request.mockRejectedValue(error)

      await expect(payOrder(id)).rejects.toThrow('余额不足')
    })

    it('支付已取消订单应该抛出错误', async () => {
      const id = 3
      const error = new Error('订单状态不允许支付')
      request.mockRejectedValue(error)

      await expect(payOrder(id)).rejects.toThrow('订单状态不允许支付')
    })
  })

  describe('receiveOrder', () => {
    it('应该确认收货', async () => {
      const id = 2
      const response = { data: { success: true, receiveTime: '2024-01-03T14:00:00' } }
      request.mockResolvedValue(response)

      const result = await receiveOrder(id)

      expect(request).toHaveBeenCalledWith({
        url: '/orders/2/receive',
        method: 'post'
      })
      expect(result).toEqual(response)
    })

    it('未发货订单不能确认收货', async () => {
      const id = 1
      const error = new Error('订单状态不允许收货')
      request.mockRejectedValue(error)

      await expect(receiveOrder(id)).rejects.toThrow('订单状态不允许收货')
    })
  })
})
