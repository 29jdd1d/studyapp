// api/upload.js - 文件上传相关API
const { uploadFile } = require('../utils/request.js');

/**
 * 上传视频
 * @param {String} filePath 文件路径
 */
function uploadVideo(filePath) {
  return uploadFile(filePath, 'video');
}

/**
 * 上传文档
 * @param {String} filePath 文件路径
 */
function uploadDocument(filePath) {
  return uploadFile(filePath, 'document');
}

/**
 * 上传图片
 * @param {String} filePath 文件路径
 */
function uploadImage(filePath) {
  return uploadFile(filePath, 'image');
}

/**
 * 上传封面
 * @param {String} filePath 文件路径
 */
function uploadCover(filePath) {
  return uploadFile(filePath, 'cover');
}

module.exports = {
  uploadVideo,
  uploadDocument,
  uploadImage,
  uploadCover
};
