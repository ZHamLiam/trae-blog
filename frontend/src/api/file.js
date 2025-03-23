import request from './request';

/**
 * 上传文件到本地服务器
 * @param {File} file 文件对象
 * @returns {Promise} 返回Promise对象
 */
export function uploadFile(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 上传文件到阿里云OSS
 * @param {File} file 文件对象
 * @returns {Promise} 返回Promise对象
 */
export function uploadFileToOSS(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/files/upload/oss',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

export default {
  uploadFile,
  uploadFileToOSS
};