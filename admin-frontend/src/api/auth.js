import request from './request'

// Admin login (using username/password instead of WeChat)
export function login(username, password) {
  return request({
    url: '/admin/login',
    method: 'post',
    data: { username, password }
  })
}

// Get current user info
export function getCurrentUser() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// Logout
export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}
