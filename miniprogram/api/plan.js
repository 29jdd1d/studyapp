// api/plan.js - 学习计划相关API
const { get, post } = require('../utils/request.js');

/**
 * 创建学习计划
 * @param {Object} data 计划数据
 */
function createPlan(data) {
  return post('/plan', data);
}

/**
 * 生成推荐学习计划
 * @param {Object} params 参数
 * @param {String} params.targetUniversity 目标院校
 * @param {String} params.targetMajor 目标专业
 * @param {String} params.examDate 考试日期
 */
function generateRecommendPlan(params) {
  return post('/plan/recommend', {}, { data: params });
}

/**
 * 获取我的学习计划
 */
function getMyPlans() {
  return get('/plan/my');
}

/**
 * 获取活跃的学习计划
 */
function getActivePlans() {
  return get('/plan/active');
}

/**
 * 获取今日计划
 * @param {Number} planId 计划ID
 */
function getTodayPlan(planId) {
  return get(`/plan/${planId}/today`);
}

/**
 * 完成计划项
 * @param {Number} itemId 计划项ID
 * @param {Number} actualHours 实际学习时长
 */
function completePlanItem(itemId, actualHours) {
  return post(`/plan/item/${itemId}/complete`, {}, { 
    data: { actualHours } 
  });
}

module.exports = {
  createPlan,
  generateRecommendPlan,
  getMyPlans,
  getActivePlans,
  getTodayPlan,
  completePlanItem
};
