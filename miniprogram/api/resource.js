// api/resource.js - 学习资源相关API
const { get, post, put, del } = require('../utils/request.js');

/**
 * 创建学习资源
 * @param {Object} data 资源数据
 */
function createResource(data) {
  return post('/resource', data);
}

/**
 * 分页查询学习资源
 * @param {Object} params 查询参数
 * @param {String} params.subject 科目（可选）
 * @param {String} params.type 类型（可选）
 * @param {Number} params.pageNum 页码
 * @param {Number} params.pageSize 每页数量
 */
function getResourceList(params) {
  return get('/resource/list', params);
}

/**
 * 获取资源详情
 * @param {Number} id 资源ID
 */
function getResourceDetail(id) {
  return get(`/resource/${id}`);
}

/**
 * 按章节获取资源
 * @param {Object} params 查询参数
 * @param {String} params.subject 科目
 * @param {String} params.chapter 章节
 */
function getResourceByChapter(params) {
  return get('/resource/chapter', params);
}

/**
 * 更新学习资源
 * @param {Number} id 资源ID
 * @param {Object} data 资源数据
 */
function updateResource(id, data) {
  return put(`/resource/${id}`, data);
}

/**
 * 删除学习资源
 * @param {Number} id 资源ID
 */
function deleteResource(id) {
  return del(`/resource/${id}`);
}

module.exports = {
  createResource,
  getResourceList,
  getResourceDetail,
  getResourceByChapter,
  updateResource,
  deleteResource
};
