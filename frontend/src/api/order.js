import request from '@/utils/request'

export const getOrders = (params) => {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

export const getOrder = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

export const createOrder = (data) => {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

export const cancelOrder = (id) => {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'post'
  })
}

export const payOrder = (id) => {
  return request({
    url: `/orders/${id}/pay`,
    method: 'post'
  })
}

export const receiveOrder = (id) => {
  return request({
    url: `/orders/${id}/receive`,
    method: 'post'
  })
}
