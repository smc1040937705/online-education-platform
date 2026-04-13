import request from '@/utils/request'

export const getProducts = (params) => {
  return request({
    url: '/products',
    method: 'get',
    params
  })
}

export const getProduct = (id) => {
  return request({
    url: `/products/${id}`,
    method: 'get'
  })
}

export const getHotProducts = (limit = 10) => {
  return request({
    url: '/products/hot',
    method: 'get',
    params: { limit }
  })
}

export const getNewProducts = (limit = 10) => {
  return request({
    url: '/products/new',
    method: 'get',
    params: { limit }
  })
}
