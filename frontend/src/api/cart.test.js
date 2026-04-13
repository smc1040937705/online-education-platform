import { describe, it, expect, vi, beforeEach } from 'vitest'
import { getCarts, addToCart, updateCartQuantity, deleteCart, selectCart, selectAllCart } from './cart'
import request from '@/utils/request'

vi.mock('@/utils/request', () => ({
  default: vi.fn()
}))

describe('Cart API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getCarts', () => {
    it('应该获取购物车列表', async () => {
      const response = {
        data: [
          { id: 1, productId: 1, productName: '商品1', quantity: 2, price: 99.99 },
          { id: 2, productId: 2, productName: '商品2', quantity: 1, price: 199.99 }
        ]
      }
      request.mockResolvedValue(response)

      const result = await getCarts()

      expect(request).toHaveBeenCalledWith({
        url: '/carts',
        method: 'get'
      })
      expect(result).toEqual(response)
    })

    it('购物车为空时应该返回空数组', async () => {
      const response = { data: [] }
      request.mockResolvedValue(response)

      const result = await getCarts()

      expect(result.data).toEqual([])
    })
  })

  describe('addToCart', () => {
    it('应该添加商品到购物车', async () => {
      const data = { productId: 1, quantity: 2 }
      const response = { data: { id: 3, productId: 1, quantity: 2 } }
      request.mockResolvedValue(response)

      const result = await addToCart(data)

      expect(request).toHaveBeenCalledWith({
        url: '/carts',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })

    it('添加商品失败时应该抛出错误', async () => {
      const data = { productId: 999, quantity: 1 }
      const error = new Error('商品不存在')
      request.mockRejectedValue(error)

      await expect(addToCart(data)).rejects.toThrow('商品不存在')
    })
  })

  describe('updateCartQuantity', () => {
    it('应该更新购物车商品数量', async () => {
      const id = 1
      const data = { quantity: 5 }
      const response = { data: { id: 1, productId: 1, quantity: 5 } }
      request.mockResolvedValue(response)

      const result = await updateCartQuantity(id, data)

      expect(request).toHaveBeenCalledWith({
        url: '/carts/1',
        method: 'put',
        data
      })
      expect(result).toEqual(response)
    })

    it('更新不存在的购物车项应该抛出错误', async () => {
      const id = 999
      const data = { quantity: 5 }
      const error = new Error('购物车项不存在')
      request.mockRejectedValue(error)

      await expect(updateCartQuantity(id, data)).rejects.toThrow('购物车项不存在')
    })
  })

  describe('deleteCart', () => {
    it('应该删除购物车项', async () => {
      const id = 1
      const response = { data: { success: true } }
      request.mockResolvedValue(response)

      const result = await deleteCart(id)

      expect(request).toHaveBeenCalledWith({
        url: '/carts/1',
        method: 'delete'
      })
      expect(result).toEqual(response)
    })

    it('删除不存在的购物车项应该抛出错误', async () => {
      const id = 999
      const error = new Error('购物车项不存在')
      request.mockRejectedValue(error)

      await expect(deleteCart(id)).rejects.toThrow('购物车项不存在')
    })
  })

  describe('selectCart', () => {
    it('应该选中/取消选中购物车项', async () => {
      const id = 1
      const data = { selected: true }
      const response = { data: { id: 1, selected: true } }
      request.mockResolvedValue(response)

      const result = await selectCart(id, data)

      expect(request).toHaveBeenCalledWith({
        url: '/carts/1/select',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })

    it('取消选中购物车项', async () => {
      const id = 1
      const data = { selected: false }
      const response = { data: { id: 1, selected: false } }
      request.mockResolvedValue(response)

      const result = await selectCart(id, data)

      expect(request).toHaveBeenCalledWith({
        url: '/carts/1/select',
        method: 'post',
        data
      })
      expect(result.data.selected).toBe(false)
    })
  })

  describe('selectAllCart', () => {
    it('应该全选购物车', async () => {
      const data = { selected: true }
      const response = { data: { updated: 2 } }
      request.mockResolvedValue(response)

      const result = await selectAllCart(data)

      expect(request).toHaveBeenCalledWith({
        url: '/carts/select-all',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })

    it('应该取消全选', async () => {
      const data = { selected: false }
      const response = { data: { updated: 2 } }
      request.mockResolvedValue(response)

      const result = await selectAllCart(data)

      expect(request).toHaveBeenCalledWith({
        url: '/carts/select-all',
        method: 'post',
        data
      })
      expect(result).toEqual(response)
    })
  })
})
