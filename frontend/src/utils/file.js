/**
 * 将文件转换为base64
 * @param {File} file 文件对象
 * @param {Function} callback 回调函数，参数为base64字符串
 */
export function getBase64(file, callback) {
  const reader = new FileReader();
  reader.addEventListener('load', () => callback(reader.result));
  reader.readAsDataURL(file);
}

/**
 * 检查文件类型是否为图片
 * @param {File} file 文件对象
 * @returns {boolean} 是否为图片
 */
export function isImageFile(file) {
  return file.type.startsWith('image/');
}

/**
 * 检查文件大小是否超过限制
 * @param {File} file 文件对象
 * @param {number} maxSize 最大大小（MB）
 * @returns {boolean} 是否超过限制
 */
export function checkFileSize(file, maxSize) {
  return file.size / 1024 / 1024 < maxSize;
}