import request from './request'

/**
 * Upload video file
 * @param {File} file - The video file to upload
 * @returns {Promise} Response with file URL
 */
export function uploadVideo(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/file/upload/video',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 300000 // 5 minutes for video upload
  })
}

/**
 * Upload document file
 * @param {File} file - The document file to upload
 * @returns {Promise} Response with file URL
 */
export function uploadDocument(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/file/upload/document',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000 // 1 minute for document upload
  })
}

/**
 * Upload image file
 * @param {File} file - The image file to upload
 * @returns {Promise} Response with file URL
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/file/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Upload cover image
 * @param {File} file - The cover image file to upload
 * @returns {Promise} Response with file URL
 */
export function uploadCover(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/file/upload/cover',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
