import request from './request'

export function getEnrollmentList(params) {
  return request({
    url: '/enrollments',
    method: 'get',
    params
  })
}

export function getEnrollmentById(id) {
  return request({
    url: `/enrollments/${id}`,
    method: 'get'
  })
}

export function createEnrollment(data) {
  return request({
    url: '/enrollments',
    method: 'post',
    data
  })
}

export function cancelEnrollment(id) {
  return request({
    url: `/enrollments/${id}/cancel`,
    method: 'post'
  })
}

export function payEnrollment(id) {
  return request({
    url: `/enrollments/${id}/pay`,
    method: 'post'
  })
}

export function completeEnrollment(id) {
  return request({
    url: `/enrollments/${id}/complete`,
    method: 'post'
  })
}
