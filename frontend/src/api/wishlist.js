import request from './request'

export function getWishlist() {
  return request({
    url: '/wishlist',
    method: 'get'
  })
}

export function addToWishlist(courseId) {
  return request({
    url: '/wishlist',
    method: 'post',
    params: { courseId }
  })
}

export function removeFromWishlist(id) {
  return request({
    url: `/wishlist/${id}`,
    method: 'delete'
  })
}
