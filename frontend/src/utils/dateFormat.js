/**
 * 日期格式化工具函数
 * 将日期格式化为指定格式，例如：2025年10月20日 17:53
 */

/**
 * 格式化日期时间为指定格式
 * @param {Date|string|number} date 日期对象、日期字符串或时间戳
 * @param {boolean} showTime 是否显示时间，默认为true
 * @returns {string} 格式化后的日期字符串，格式为：YYYY年MM月DD日 HH:MM
 */
export function formatDate(date, showTime = true) {
  if (!date) return '';
  
  // 将输入转换为Date对象
  const dateObj = typeof date === 'string' || typeof date === 'number' 
    ? new Date(date) 
    : date;
  
  // 检查日期是否有效
  if (isNaN(dateObj.getTime())) {
    console.warn('Invalid date:', date);
    return '';
  }
  
  // 获取年、月、日、时、分
  const year = dateObj.getFullYear();
  const month = dateObj.getMonth() + 1;
  const day = dateObj.getDate();
  const hours = dateObj.getHours();
  const minutes = dateObj.getMinutes();
  
  // 格式化为：YYYY年MM月DD日
  let formattedDate = `${year}年${padZero(month)}月${padZero(day)}日`;
  
  // 如果需要显示时间，则添加时间部分
  if (showTime) {
    formattedDate += ` ${padZero(hours)}:${padZero(minutes)}`;
  }
  
  return formattedDate;
}

/**
 * 在个位数前补零
 * @param {number} num 数字
 * @returns {string} 补零后的字符串
 */
function padZero(num) {
  return num < 10 ? `0${num}` : `${num}`;
}