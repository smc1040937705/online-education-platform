import request from '@/utils/request'

export const getCategories = () => {
  return request({
    url: '/categories',
    method: 'get'
  })
}

export const getCategoryTree = () => {
  return request({
    url: '/categories/tree',
    method: 'get'
  })
}
