// utils/request.js - 网络请求封装
const app = getApp();

// API基础URL配置
const BASE_URL = 'http://localhost:8080/api';

/**
 * 发起网络请求
 * @param {Object} options 请求配置
 * @param {String} options.url 请求路径
 * @param {String} options.method 请求方法 GET/POST/PUT/DELETE
 * @param {Object} options.data 请求数据
 * @param {Boolean} options.needAuth 是否需要认证，默认true
 * @param {Boolean} options.showLoading 是否显示加载提示，默认true
 */
function request(options) {
  const {
    url,
    method = 'GET',
    data = {},
    needAuth = true,
    showLoading = true
  } = options;

  // 显示加载提示
  if (showLoading) {
    wx.showLoading({
      title: '加载中...',
      mask: true
    });
  }

  return new Promise((resolve, reject) => {
    // 构建请求头
    const header = {
      'Content-Type': 'application/json'
    };

    // 添加认证token
    if (needAuth) {
      const token = app.getToken();
      if (!token) {
        wx.hideLoading();
        wx.showModal({
          title: '提示',
          content: '请先登录',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({
                url: '/pages/login/login'
              });
            }
          }
        });
        reject(new Error('未登录'));
        return;
      }
      header['Authorization'] = 'Bearer ' + token;
    }

    // 发起请求
    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: header,
      success: (res) => {
        if (showLoading) {
          wx.hideLoading();
        }

        // 处理响应
        if (res.statusCode === 200) {
          const result = res.data;
          if (result.code === 200) {
            resolve(result.data);
          } else {
            // 业务错误
            wx.showToast({
              title: result.message || '请求失败',
              icon: 'none',
              duration: 2000
            });
            reject(new Error(result.message));
          }
        } else if (res.statusCode === 401) {
          // 未认证，跳转登录
          app.clearLoginInfo();
          wx.showModal({
            title: '提示',
            content: '登录已过期，请重新登录',
            success: (res) => {
              if (res.confirm) {
                wx.navigateTo({
                  url: '/pages/login/login'
                });
              }
            }
          });
          reject(new Error('未认证'));
        } else {
          // 其他错误
          wx.showToast({
            title: '请求失败',
            icon: 'none',
            duration: 2000
          });
          reject(new Error('请求失败'));
        }
      },
      fail: (err) => {
        if (showLoading) {
          wx.hideLoading();
        }
        wx.showToast({
          title: '网络错误',
          icon: 'none',
          duration: 2000
        });
        reject(err);
      }
    });
  });
}

/**
 * GET请求
 */
function get(url, data = {}, options = {}) {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  });
}

/**
 * POST请求
 */
function post(url, data = {}, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  });
}

/**
 * PUT请求
 */
function put(url, data = {}, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  });
}

/**
 * DELETE请求
 */
function del(url, data = {}, options = {}) {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  });
}

/**
 * 上传文件
 */
function uploadFile(filePath, type = 'image') {
  const token = app.getToken();
  if (!token) {
    return Promise.reject(new Error('未登录'));
  }

  let uploadUrl = '';
  switch(type) {
    case 'video':
      uploadUrl = '/file/upload/video';
      break;
    case 'document':
      uploadUrl = '/file/upload/document';
      break;
    case 'cover':
      uploadUrl = '/file/upload/cover';
      break;
    default:
      uploadUrl = '/file/upload/image';
  }

  return new Promise((resolve, reject) => {
    wx.showLoading({
      title: '上传中...',
      mask: true
    });

    wx.uploadFile({
      url: BASE_URL + uploadUrl,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': 'Bearer ' + token
      },
      success: (res) => {
        wx.hideLoading();
        const data = JSON.parse(res.data);
        if (data.code === 200) {
          resolve(data.data);
        } else {
          wx.showToast({
            title: data.message || '上传失败',
            icon: 'none'
          });
          reject(new Error(data.message));
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  uploadFile
};
