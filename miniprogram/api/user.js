// api/user.js - 用户相关API
const { get, post, put } = require('../utils/request.js');

/**
 * 微信登录
 * @param {Object} data 登录数据
 * @param {String} data.code 微信登录凭证
 * @param {String} data.nickName 昵称
 * @param {String} data.avatarUrl 头像URL
 * @param {String} data.gender 性别
 */
function wechatLogin(data) {
  return post('/user/wechat-login', data, { needAuth: false });
}

/**
 * 获取用户信息
 */
function getUserInfo() {
  return get('/user/info');
}

/**
 * 更新用户信息
 * @param {Object} data 用户信息
 */
function updateUserInfo(data) {
  return put('/user/info', data);
}

/**
 * 获取学习数据看板
 */
function getDashboard() {
  return get('/user/dashboard');
}

module.exports = {
  wechatLogin,
  getUserInfo,
  updateUserInfo,
  getDashboard
};
