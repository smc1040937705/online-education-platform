import request from '@/utils/request'

export const getCarts = () => {
  return request({
    url: '/carts',
    method: 'get'
  })
}

export const addToCart = (data) => {
  return request({
    url: '/carts',
    method: 'post',
    data
  })
}

export const updateCartQuantity = (id, data) => {
  return request({
    url: `/carts/${id}`,
    method: 'put',
    data
  })
}

export const deleteCart = (id) => {
  return request({
    url: `/carts/${id}`,
    method: 'delete'
  })
}

export const selectCart = (id, data) => {
  return request({
    url: `/carts/${id}/select`,
    method: 'post',
    data
  })
}

export const selectAllCart = (data) => {
  return request({
    url: '/carts/select-all',
    method: 'post',
    data
  })
}
