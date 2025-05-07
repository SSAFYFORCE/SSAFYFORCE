/**
 * 날짜 포맷팅 함수
 * @param {Date|string} date - 포맷팅할 날짜
 * @param {string} format - 포맷 형식 (기본값: 'YYYY-MM-DD')
 * @returns {string} - 포맷팅된 날짜 문자열
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  const d = date instanceof Date ? date : new Date(date);

  if (isNaN(d.getTime())) {
    return 'Invalid Date';
  }

  const year = d.getFullYear();
  const month = (d.getMonth() + 1).toString().padStart(2, '0');
  const day = d.getDate().toString().padStart(2, '0');
  const hours = d.getHours().toString().padStart(2, '0');
  const minutes = d.getMinutes().toString().padStart(2, '0');
  const seconds = d.getSeconds().toString().padStart(2, '0');

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 숫자에 천 단위 구분자 추가
 * @param {number} number - 포맷팅할 숫자
 * @param {number} decimals - 소수점 자릿수 (기본값: 0)
 * @param {string} decimalSeparator - 소수점 구분자 (기본값: '.')
 * @param {string} thousandsSeparator - 천 단위 구분자 (기본값: ',')
 * @returns {string} - 포맷팅된 숫자 문자열
 */
export function formatNumber(number, decimals = 0, decimalSeparator = '.', thousandsSeparator = ',') {
  if (number === null || number === undefined) {
    return '';
  }

  const num = Number(number);

  if (isNaN(num)) {
    return '';
  }

  const fixedNum = num.toFixed(decimals);
  const [integerPart, decimalPart] = fixedNum.split('.');

  // 천 단위 구분자 추가
  const formattedInteger = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator);

  // 소수점 부분이 있는 경우에만 소수점 구분자 추가
  return decimalPart ? `${formattedInteger}${decimalSeparator}${decimalPart}` : formattedInteger;
}

/**
 * 금액 포맷팅 함수
 * @param {number} amount - 포맷팅할 금액
 * @param {string} currencySymbol - 통화 기호 (기본값: '₩')
 * @param {boolean} symbolFirst - 통화 기호를 앞에 표시할지 여부 (기본값: true)
 * @returns {string} - 포맷팅된 금액 문자열
 */
export function formatCurrency(amount, currencySymbol = '₩', symbolFirst = true) {
  const formattedAmount = formatNumber(amount);

  return symbolFirst ? `${currencySymbol}${formattedAmount}` : `${formattedAmount}${currencySymbol}`;
}

/**
 * 문자열 길이 제한 및 말줄임표 추가
 * @param {string} text - 원본 문자열
 * @param {number} maxLength - 최대 길이
 * @returns {string} - 길이가 제한된 문자열
 */
export function truncateText(text, maxLength) {
  if (!text || text.length <= maxLength) {
    return text;
  }

  return `${text.substring(0, maxLength)}...`;
}

/**
 * 파일 크기 포맷팅
 * @param {number} bytes - 바이트 단위 파일 크기
 * @param {number} decimals - 소수점 자릿수 (기본값: 2)
 * @returns {string} - 포맷팅된 파일 크기
 */
export function formatFileSize(bytes, decimals = 2) {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(decimals))} ${sizes[i]}`;
}

/**
 * 전화번호 포맷팅
 * @param {string} phoneNumber - 포맷팅할 전화번호
 * @returns {string} - 포맷팅된 전화번호
 */
export function formatPhoneNumber(phoneNumber) {
  if (!phoneNumber) return '';

  // 숫자만 추출
  const cleaned = phoneNumber.replace(/\D/g, '');

  // 한국 전화번호 포맷 (010-1234-5678)
  if (cleaned.length === 11 && cleaned.startsWith('010')) {
    return `${cleaned.substring(0, 3)}-${cleaned.substring(3, 7)}-${cleaned.substring(7, 11)}`;
  }

  // 그 외 전화번호는 그대로 반환
  return phoneNumber;
}
