import { describe, it, expect, beforeEach, vi } from 'vitest'
import MockAdapter from 'axios-mock-adapter'
import request from '@/utils/request'
import {
  getCarts,
  addToCart,
  updateCartQuantity,
  deleteCart,
  selectCart,
  selectAllCart
} from '@/api/cart'

const mock = new MockAdapter(request)

describe('Cart API', () => {
  beforeEach(() => {
    mock.reset()
  })

  describe('getCarts', () => {
    it('should send GET request to /carts', async () => {
      const mockResponse = {
        code: 200,
        data: [
          {
            id: 1,
            productId: 101,
            productName: '测试商品',
            quantity: 2,
            price: 99.99,
            selected: true
          }
        ],
        message: 'success'
      }

      mock.onGet('/api/carts').reply(200, mockResponse)

      const result = await getCarts()

      expect(result).toEqual(mockResponse)
      expect(result.data).toHaveLength(1)
      expect(result.data[0].productName).toBe('测试商品')
    })

    it('should handle empty cart list', async () => {
      const mockResponse = {
        code: 200,
        data: [],
        message: 'success'
      }

      mock.onGet('/api/carts').reply(200, mockResponse)

      const result = await getCarts()

      expect(result).toEqual(mockResponse)
      expect(result.data).toHaveLength(0)
    })
  })

  describe('addToCart', () => {
    it('should send POST request to /carts with product data', async () => {
      const cartData = { productId: 101, quantity: 1 }
      const mockResponse = {
        code: 200,
        data: { id: 1, productId: 101, quantity: 1 },
        message: '添加成功'
      }

      mock.onPost('/api/carts', cartData).reply(200, mockResponse)

      const result = await addToCart(cartData)

      expect(result).toEqual(mockResponse)
    })

    it('should handle add to cart with quantity > 1', async () => {
      const cartData = { productId: 102, quantity: 5 }
      const mockResponse = {
        code: 200,
        data: { id: 2, productId: 102, quantity: 5 },
        message: '添加成功'
      }

      mock.onPost('/api/carts', cartData).reply(200, mockResponse)

      const result = await addToCart(cartData)

      expect(result).toEqual(mockResponse)
      expect(result.data.quantity).toBe(5)
    })

    it('should handle product not found error', async () => {
      const cartData = { productId: 999, quantity: 1 }
      const mockError = {
        code: 400,
        message: '商品不存在'
      }

      mock.onPost('/api/carts', cartData).reply(200, mockError)

      try {
        await addToCart(cartData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('商品不存在')
      }
    })

    it('should handle out of stock error', async () => {
      const cartData = { productId: 101, quantity: 100 }
      const mockError = {
        code: 400,
        message: '库存不足'
      }

      mock.onPost('/api/carts', cartData).reply(200, mockError)

      try {
        await addToCart(cartData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('库存不足')
      }
    })
  })

  describe('updateCartQuantity', () => {
    it('should send PUT request to /carts/:id with new quantity', async () => {
      const cartId = 1
      const updateData = { quantity: 3 }
      const mockResponse = {
        code: 200,
        message: '更新成功'
      }

      mock.onPut(`/api/carts/${cartId}`, updateData).reply(200, mockResponse)

      const result = await updateCartQuantity(cartId, updateData)

      expect(result).toEqual(mockResponse)
    })

    it('should handle cart item not found error', async () => {
      const cartId = 999
      const updateData = { quantity: 3 }
      const mockError = {
        code: 400,
        message: '购物车商品不存在'
      }

      mock.onPut(`/api/carts/${cartId}`, updateData).reply(200, mockError)

      try {
        await updateCartQuantity(cartId, updateData)
        expect.fail('Should throw error')
      } catch (error) {
        expect(error.message).toBe('购物车商品不存在')
      }
    })
  })

  describe('deleteCart', () => {
    it('should send DELETE request to /carts/:id', async () => {
      const cartId = 1
      const mockResponse = {
        code: 200,
        message: '删除成功'
      }

      mock.onDelete(`/api/carts/${cartId}`).reply(200, mockResponse)

      const result = await deleteCart(cartId)

      expect(result).toEqual(mockResponse)
    })
  })

  describe('selectCart', () => {
    it('should send POST request to /carts/:id/select with selected status', async () => {
      const cartId = 1
      const selectData = { selected: true }
      const mockResponse = {
        code: 200,
        message: '选择成功'
      }

      mock.onPost(`/api/carts/${cartId}/select`, selectData).reply(200, mockResponse)

      const result = await selectCart(cartId, selectData)

      expect(result).toEqual(mockResponse)
    })

    it('should send POST request to /carts/:id/select with unselected status', async () => {
      const cartId = 1
      const selectData = { selected: false }
      const mockResponse = {
        code: 200,
        message: '取消选择成功'
      }

      mock.onPost(`/api/carts/${cartId}/select`, selectData).reply(200, mockResponse)

      const result = await selectCart(cartId, selectData)

      expect(result).toEqual(mockResponse)
    })
  })

  describe('selectAllCart', () => {
    it('should send POST request to /carts/select-all', async () => {
      const selectAllData = { selected: true }
      const mockResponse = {
        code: 200,
        message: '全选成功'
      }

      mock.onPost('/api/carts/select-all', selectAllData).reply(200, mockResponse)

      const result = await selectAllCart(selectAllData)

      expect(result).toEqual(mockResponse)
    })

    it('should send POST request to /carts/select-all to unselect all', async () => {
      const selectAllData = { selected: false }
      const mockResponse = {
        code: 200,
        message: '取消全选成功'
      }

      mock.onPost('/api/carts/select-all', selectAllData).reply(200, mockResponse)

      const result = await selectAllCart(selectAllData)

      expect(result).toEqual(mockResponse)
    })
  })
})
