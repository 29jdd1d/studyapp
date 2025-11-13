import axios from 'axios'
import { ElMessage } from 'element-plus'

// Create axios instance
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
request.interceptors.request.use(
  config => {
    // Add token to request headers
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // If the response is successful
    if (res.code === 200 || res.success === true) {
      return res
    }
    
    // Handle error responses
    ElMessage.error(res.message || 'Request failed')
    return Promise.reject(new Error(res.message || 'Request failed'))
  },
  error => {
    console.error('Response error:', error)
    
    // Handle 401 Unauthorized
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      window.location.href = '/login'
      ElMessage.error('Session expired, please login again')
    } else {
      ElMessage.error(error.message || 'Network error')
    }
    
    return Promise.reject(error)
  }
)

export default request
