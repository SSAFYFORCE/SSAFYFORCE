// 유틸리티 함수 내보내기
export * from './formatters';
export * from './validation';

/**
 * 디바운스 함수
 * @param {Function} func - 실행할 함수
 * @param {number} wait - 대기 시간 (밀리초)
 * @returns {Function} - 디바운스된 함수
 */
export function debounce(func, wait = 300) {
  let timeout;

  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };

    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * 쓰로틀 함수
 * @param {Function} func - 실행할 함수
 * @param {number} limit - 제한 시간 (밀리초)
 * @returns {Function} - 쓰로틀된 함수
 */
export function throttle(func, limit = 300) {
  let inThrottle;

  return function executedFunction(...args) {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;

      setTimeout(() => {
        inThrottle = false;
      }, limit);
    }
  };
}

/**
 * 랜덤 ID 생성
 * @param {number} length - ID 길이
 * @returns {string} - 생성된 ID
 */
export function generateId(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';

  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }

  return result;
}

/**
 * 객체에서 빈 값 제거
 * @param {object} obj - 처리할 객체
 * @returns {object} - 빈 값이 제거된 객체
 */
export function removeEmptyValues(obj) {
  return Object.fromEntries(
    Object.entries(obj).filter(([, value]) => {
      if (value === null || value === undefined) return false;
      if (value === '') return false;
      if (Array.isArray(value) && value.length === 0) return false;
      if (typeof value === 'object' && Object.keys(value).length === 0) return false;
      return true;
    })
  );
}

/**
 * 쿼리 문자열을 객체로 파싱
 * @param {string} queryString - 파싱할 쿼리 문자열
 * @returns {object} - 파싱된 객체
 */
export function parseQueryString(queryString) {
  if (!queryString || queryString === '?') return {};

  const query = queryString.startsWith('?') ? queryString.substring(1) : queryString;

  return Object.fromEntries(
    query.split('&').map((pair) => {
      const [key, value] = pair.split('=');
      return [decodeURIComponent(key), decodeURIComponent(value || '')];
    })
  );
}

/**
 * 객체를 쿼리 문자열로 변환
 * @param {object} params - 변환할 객체
 * @returns {string} - 쿼리 문자열
 */
export function buildQueryString(params) {
  const cleanParams = removeEmptyValues(params);

  if (Object.keys(cleanParams).length === 0) {
    return '';
  }

  return (
    '?' +
    Object.entries(cleanParams)
      .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
      .join('&')
  );
}

/**
 * 브라우저의 로컬 스토리지에 데이터 저장
 * @param {string} key - 키
 * @param {any} value - 저장할 값
 */
export function setLocalStorage(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error('로컬 스토리지 저장 오류:', error);
  }
}

/**
 * 브라우저의 로컬 스토리지에서 데이터 가져오기
 * @param {string} key - 키
 * @param {any} defaultValue - 기본값
 * @returns {any} - 가져온 값
 */
export function getLocalStorage(key, defaultValue = null) {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch (error) {
    console.error('로컬 스토리지 읽기 오류:', error);
    return defaultValue;
  }
}

/**
 * 브라우저의 로컬 스토리지에서 데이터 삭제
 * @param {string} key - 키
 */
export function removeLocalStorage(key) {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error('로컬 스토리지 삭제 오류:', error);
  }
}
