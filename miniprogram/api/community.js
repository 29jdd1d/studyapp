// api/community.js - 社区相关API
const { get, post } = require('../utils/request.js');

/**
 * 创建帖子
 * @param {Object} data 帖子数据
 */
function createPost(data) {
  return post('/community/post', data);
}

/**
 * 获取帖子列表
 * @param {Object} params 查询参数
 */
function getPostList(params) {
  return get('/community/post/list', params);
}

/**
 * 获取我的帖子
 * @param {Object} params 查询参数
 * @param {Number} params.pageNum 页码
 * @param {Number} params.pageSize 每页数量
 */
function getMyPosts(params = {}) {
  return get('/community/post/my', params);
}

/**
 * 获取置顶帖子
 */
function getPinnedPosts() {
  return get('/community/post/pinned');
}

/**
 * 获取帖子详情
 * @param {Number} id 帖子ID
 */
function getPostDetail(id) {
  return get(`/community/post/${id}`);
}

/**
 * 点赞帖子
 * @param {Number} id 帖子ID
 */
function likePost(id) {
  return post(`/community/post/${id}/like`);
}

/**
 * 添加评论
 * @param {Object} data 评论数据
 */
function addComment(data) {
  return post('/community/comment', data);
}

/**
 * 获取评论列表
 * @param {Number} postId 帖子ID
 */
function getComments(postId) {
  return get(`/community/comment/${postId}`);
}

/**
 * 学习打卡
 * @param {Object} data 打卡数据
 */
function checkIn(data) {
  return post('/community/checkin', data);
}

/**
 * 获取打卡记录
 */
function getCheckInRecords() {
  return get('/community/checkin/records');
}

/**
 * 获取连续打卡天数
 */
function getContinuousDays() {
  return get('/community/checkin/continuous');
}

module.exports = {
  createPost,
  getPostList,
  getMyPosts,
  getPinnedPosts,
  getPostDetail,
  likePost,
  addComment,
  getComments,
  checkIn,
  getCheckInRecords,
  getContinuousDays
};
