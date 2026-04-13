import request from './request'

export function getCourseList(params) {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

export function getCourseById(id) {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  })
}

export function getHotCourses(limit = 10) {
  return request({
    url: '/courses/hot',
    method: 'get',
    params: { limit }
  })
}

export function getNewCourses(limit = 10) {
  return request({
    url: '/courses/new',
    method: 'get',
    params: { limit }
  })
}

export function createCourse(data) {
  return request({
    url: '/courses',
    method: 'post',
    data
  })
}

export function updateCourse(id, data) {
  return request({
    url: `/courses/${id}`,
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/courses/${id}`,
    method: 'delete'
  })
}
