import request from './request'

// Get system statistics
export function getStatistics() {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}

// User Management
export function getUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

// Resource Management
export function getResources(params) {
  return request({
    url: '/admin/resources',
    method: 'get',
    params
  })
}

export function createResource(data) {
  return request({
    url: '/resource',
    method: 'post',
    data
  })
}

export function updateResource(id, data) {
  return request({
    url: `/resource/${id}`,
    method: 'put',
    data
  })
}

export function deleteResource(id) {
  return request({
    url: `/admin/resource/${id}`,
    method: 'delete'
  })
}

// Question Management
export function getQuestions(params) {
  return request({
    url: '/admin/questions',
    method: 'get',
    params
  })
}

export function createQuestion(data) {
  return request({
    url: '/question',
    method: 'post',
    data
  })
}

// Note: updateQuestion endpoint does not exist in backend
// Removed to align with backend API
// export function updateQuestion(id, data) {
//   return request({
//     url: `/question/${id}`,
//     method: 'put',
//     data
//   })
// }

export function deleteQuestion(id) {
  return request({
    url: `/admin/question/${id}`,
    method: 'delete'
  })
}

// Community Post Management
export function getPosts(params) {
  return request({
    url: '/admin/posts',
    method: 'get',
    params
  })
}

// Note: updatePost endpoint does not exist in backend
// Removed to align with backend API
// export function updatePost(id, data) {
//   return request({
//     url: `/community/post/${id}`,
//     method: 'put',
//     data
//   })
// }

export function deletePost(id) {
  return request({
    url: `/admin/post/${id}`,
    method: 'delete'
  })
}

// Note: auditPost endpoint does not exist in backend
// Removed to align with backend API
// export function auditPost(id, status) {
//   return request({
//     url: `/admin/posts/${id}/audit`,
//     method: 'post',
//     data: { status }
//   })
// }
