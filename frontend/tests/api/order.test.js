import { describe, it, expect, beforeEach, vi } from 'vitest'
import MockAdapter from 'axios-mock-adapter'
import request from '@/utils/request'
import {
  getOrders,
  getOrder,
  createOrder,
  cancelOrder,
  payOrder,
  receiveOrder
} from '@/api/order'

const mock = new MockAdapter(request)

describe('Order API', () => {
  beforeEach(() => {
    mock.reset()
  })

  describe('getOrders', () => {
    it('should send GET request to /orders with pagination params', async () => {
      const params = { page: 1, size: 10, status: 1 }
      const mockResponse = {
        code: 200,
        data: {
          total: 5,
          page: 1,
          size: 10,
          list: [
            {
              id: 1,
              orderNo: '20240101000001',
              status: 1,
              payAmount: 99.99,
              createTime: '2024-01-01 10:00:00'
            }
          ]
        },
        message: 'success'
      }

      mock.onGet('/api/orders', { params }).reply(200, mockResponse)

      const result = await getOrders(params)

      expect(result).toEqual(mockResponse)
      expect(result.data.list).toHaveLength(1)
    })

    it('should send GET request to /orders without params', async () => {
      const mockResponse = {
        code: 200,
        data: {
          total: 0,
          page: 1,
          size: 10,
          list: []
        },
        message: 'success'
      }

      mock.onGet('/api/orders').reply(200, mockResponse)

      const result = await getOrders()

      expect(result).toEqual(mockResponse)
    })
  })

  describe('getOrder', () => {
    it('should send GET request to /orders/:id', async () => {
      const orderId = 1
      const mockResponse = {
        code: 200,
        data: {
          id: 1,
          orderNo: '20240101000001',
          status: 1,
          totalAmount: 99.99,
          payAmount: 99.99,
          items: [
            {
              id: 1,
              productId: 101,
              productName: '测试商品',
              quantity: 1,
              productPrice: 99.99
            }
          ]
        },
        message: 'success'
      }

      mock.onGet(`/api/orders/${orderId}`).reply(200, mockResponse)

      const result = await getOrder(orderId)

      expect(result).toEqual(mockResponse)
      expect(result.data.id).toBe(1)
      expect(result.data.items).toHaveLength(1)
    })

    it('should handle order not found error', async () => {
      const orderId = 999
      const mockError = {
        code: 400,
        message: '订单不存在'
      }

      mock.onGet(`/api/orders/${orderId}`).reply(200, mockError)

      try {
        await getOrder(orderId)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('订单不存在')
      }
    })
  })

  describe('createOrder', () => {
    it('should send POST request to /orders with order data', async () => {
      const orderData = {
        addressId: 1,
        cartIds: [1, 2, 3],
        couponId: null,
        remark: '测试订单'
      }
      const mockResponse = {
        code: 200,
        data: {
          id: 1,
          orderNo: '20240101000001',
          status: 0,
          payAmount: 199.98
        },
        message: '创建成功'
      }

      mock.onPost('/api/orders', orderData).reply(200, mockResponse)

      const result = await createOrder(orderData)

      expect(result).toEqual(mockResponse)
      expect(result.data.status).toBe(0)
    })

    it('should handle empty cart error when creating order', async () => {
      const orderData = {
        addressId: 1,
        cartIds: [],
        couponId: null
      }
      const mockError = {
        code: 400,
        message: '购物车为空'
      }

      mock.onPost('/api/orders', orderData).reply(200, mockError)

      try {
        await createOrder(orderData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('购物车为空')
      }
    })

    it('should handle invalid address error', async () => {
      const orderData = {
        addressId: 999,
        cartIds: [1, 2]
      }
      const mockError = {
        code: 400,
        message: '地址不存在'
      }

      mock.onPost('/api/orders', orderData).reply(200, mockError)

      try {
        await createOrder(orderData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('地址不存在')
      }
    })

    it('should handle out of stock error', async () => {
      const orderData = {
        addressId: 1,
        cartIds: [1]
      }
      const mockError = {
        code: 400,
        message: '商品库存不足'
      }

      mock.onPost('/api/orders', orderData).reply(200, mockError)

      try {
        await createOrder(orderData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('商品库存不足')
      }
    })
  })

  describe('cancelOrder', () => {
    it('should send POST request to /orders/:id/cancel', async () => {
      const orderId = 1
      const mockResponse = {
        code: 200,
        message: '取消成功'
      }

      mock.onPost(`/api/orders/${orderId}/cancel`).reply(200, mockResponse)

      const result = await cancelOrder(orderId)

      expect(result).toEqual(mockResponse)
    })

    it('should handle cancel order with wrong status error', async () => {
      const orderId = 2
      const mockError = {
        code: 400,
        message: '订单状态不允许取消'
      }

      mock.onPost(`/api/orders/${orderId}/cancel`).reply(200, mockError)

      try {
        await cancelOrder(orderId)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('订单状态不允许取消')
      }
    })
  })

  describe('payOrder', () => {
    it('should send POST request to /orders/:id/pay', async () => {
      const orderId = 1
      const mockResponse = {
        code: 200,
        message: '支付成功'
      }

      mock.onPost(`/api/orders/${orderId}/pay`).reply(200, mockResponse)

      const result = await payOrder(orderId)

      expect(result).toEqual(mockResponse)
    })

    it('should handle insufficient balance error', async () => {
      const orderId = 1
      const mockError = {
        code: 400,
        message: '余额不足'
      }

      mock.onPost(`/api/orders/${orderId}/pay`).reply(200, mockError)

      try {
        await payOrder(orderId)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('余额不足')
      }
    })

    it('should handle pay order with wrong status error', async () => {
      const orderId = 3
      const mockError = {
        code: 400,
        message: '订单状态不允许支付'
      }

      mock.onPost(`/api/orders/${orderId}/pay`).reply(200, mockError)

      try {
        await payOrder(orderId)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('订单状态不允许支付')
      }
    })
  })

  describe('receiveOrder', () => {
    it('should send POST request to /orders/:id/receive', async () => {
      const orderId = 1
      const mockResponse = {
        code: 200,
        message: '确认收货成功'
      }

      mock.onPost(`/api/orders/${orderId}/receive`).reply(200, mockResponse)

      const result = await receiveOrder(orderId)

      expect(result).toEqual(mockResponse)
    })

    it('should handle receive order with wrong status error', async () => {
      const orderId = 1
      const mockError = {
        code: 400,
        message: '订单状态不允许收货'
      }

      mock.onPost(`/api/orders/${orderId}/receive`).reply(200, mockError)

      try {
        await receiveOrder(orderId)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('订单状态不允许收货')
      }
    })
  })
})
