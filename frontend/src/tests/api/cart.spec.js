import { describe, it, expect, vi, beforeEach } from 'vitest'
import request from '@/utils/request'
import { getCarts, addToCart, updateCartQuantity, deleteCart, selectCart, selectAllCart } from '@/api/cart'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('Cart API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getCarts', () => {
    it('应该获取购物车列表', async () => {
      const mockResponse = { data: [{ id: 1, productId: 1, quantity: 2 }] }
      request.mockResolvedValue(mockResponse)
      
      const result = await getCarts()
      
      expect(request).toHaveBeenCalledWith({
        url: '/carts',
        method: 'get'
      })
      expect(result).toEqual(mockResponse)
    })

    it('获取购物车失败-未授权', async () => {
      const error = new Error('未授权')
      error.response = { status: 401 }
      request.mockRejectedValue(error)
      
      await expect(getCarts()).rejects.toThrow('未授权')
    })

    it('获取购物车失败-网络错误', async () => {
      const error = new Error('Network Error')
      request.mockRejectedValue(error)
      
      await expect(getCarts()).rejects.toThrow('Network Error')
    })
  })

  describe('addToCart', () => {
    it('应该添加商品到购物车', async () => {
      const mockResponse = { data: { id: 1, productId: 1, quantity: 1 } }
      request.mockResolvedValue(mockResponse)
      
      const data = { productId: 1, quantity: 1 }
      const result = await addToCart(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/carts',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('添加商品失败-商品不存在', async () => {
      const error = new Error('商品不存在')
      error.response = { status: 404, data: { message: '商品不存在' } }
      request.mockRejectedValue(error)
      
      const data = { productId: 999, quantity: 1 }
      
      await expect(addToCart(data)).rejects.toThrow('商品不存在')
    })

    it('添加商品失败-库存不足', async () => {
      const error = new Error('库存不足')
      error.response = { status: 400, data: { message: '库存不足' } }
      request.mockRejectedValue(error)
      
      const data = { productId: 1, quantity: 999 }
      
      await expect(addToCart(data)).rejects.toThrow('库存不足')
    })
  })

  describe('updateCartQuantity', () => {
    it('应该更新购物车商品数量', async () => {
      const mockResponse = { data: { id: 1, quantity: 3 } }
      request.mockResolvedValue(mockResponse)
      
      const data = { quantity: 3 }
      const result = await updateCartQuantity(1, data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/carts/1',
        method: 'put',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('更新数量失败-购物车项不存在', async () => {
      const error = new Error('购物车项不存在')
      error.response = { status: 404 }
      request.mockRejectedValue(error)
      
      const data = { quantity: 3 }
      
      await expect(updateCartQuantity(999, data)).rejects.toThrow('购物车项不存在')
    })
  })

  describe('deleteCart', () => {
    it('应该删除购物车商品', async () => {
      const mockResponse = { data: { success: true } }
      request.mockResolvedValue(mockResponse)
      
      const result = await deleteCart(1)
      
      expect(request).toHaveBeenCalledWith({
        url: '/carts/1',
        method: 'delete'
      })
      expect(result).toEqual(mockResponse)
    })

    it('删除购物车项失败-不存在', async () => {
      const error = new Error('购物车项不存在')
      error.response = { status: 404 }
      request.mockRejectedValue(error)
      
      await expect(deleteCart(999)).rejects.toThrow('购物车项不存在')
    })
  })

  describe('selectCart', () => {
    it('应该选择/取消选择购物车商品', async () => {
      const mockResponse = { data: { id: 1, selected: true } }
      request.mockResolvedValue(mockResponse)
      
      const data = { selected: true }
      const result = await selectCart(1, data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/carts/1/select',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('选择购物车项失败-未授权', async () => {
      const error = new Error('未授权')
      error.response = { status: 401 }
      request.mockRejectedValue(error)
      
      const data = { selected: true }
      
      await expect(selectCart(1, data)).rejects.toThrow('未授权')
    })
  })

  describe('selectAllCart', () => {
    it('应该全选/取消全选购物车', async () => {
      const mockResponse = { data: { success: true } }
      request.mockResolvedValue(mockResponse)
      
      const data = { selected: true }
      const result = await selectAllCart(data)
      
      expect(request).toHaveBeenCalledWith({
        url: '/carts/select-all',
        method: 'post',
        data
      })
      expect(result).toEqual(mockResponse)
    })

    it('全选失败-未授权', async () => {
      const error = new Error('未授权')
      error.response = { status: 401 }
      request.mockRejectedValue(error)
      
      const data = { selected: true }
      
      await expect(selectAllCart(data)).rejects.toThrow('未授权')
    })
  })
})
