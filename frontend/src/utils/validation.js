/**
 * 이메일 형식 검증
 * @param {string} email - 검증할 이메일
 * @returns {boolean} - 유효성 여부
 */
export function isValidEmail(email) {
  const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  return regex.test(email);
}

/**
 * 패스워드 강도 검증
 * @param {string} password - 검증할 패스워드
 * @returns {object} - 강도 정보
 */
export function validatePassword(password) {
  if (!password) {
    return {isValid: false, strength: 0, message: '패스워드를 입력해주세요.'};
  }

  let strength = 0;
  const messages = [];

  // 길이 검사
  if (password.length < 8) {
    messages.push('패스워드는 8자 이상이어야 합니다.');
  } else {
    strength += 1;
  }

  // 대문자 포함 검사
  if (!/[A-Z]/.test(password)) {
    messages.push('대문자를 포함해야 합니다.');
  } else {
    strength += 1;
  }

  // 소문자 포함 검사
  if (!/[a-z]/.test(password)) {
    messages.push('소문자를 포함해야 합니다.');
  } else {
    strength += 1;
  }

  // 숫자 포함 검사
  if (!/[0-9]/.test(password)) {
    messages.push('숫자를 포함해야 합니다.');
  } else {
    strength += 1;
  }

  // 특수 문자 포함 검사
  if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    messages.push('특수 문자를 포함해야 합니다.');
  } else {
    strength += 1;
  }

  // 강도 메시지
  let strengthMessage = '';
  if (strength < 2) {
    strengthMessage = '매우 약함';
  } else if (strength < 3) {
    strengthMessage = '약함';
  } else if (strength < 4) {
    strengthMessage = '보통';
  } else if (strength < 5) {
    strengthMessage = '강함';
  } else {
    strengthMessage = '매우 강함';
  }

  return {
    isValid: strength >= 3,
    strength,
    strengthMessage,
    message: messages.join(' '),
  };
}

/**
 * 한국 휴대폰 번호 유효성 검사
 * @param {string} phoneNumber - 검증할 전화번호
 * @returns {boolean} - 유효성 여부
 */
export function isValidKoreanPhoneNumber(phoneNumber) {
  const regex = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
  return regex.test(phoneNumber);
}

/**
 * 필수 입력값 검증
 * @param {string} value - 검증할 값
 * @param {string} fieldName - 필드 이름
 * @returns {string|null} - 오류 메시지 또는 null
 */
export function validateRequired(value, fieldName) {
  if (!value || value.trim() === '') {
    return `${fieldName}은(는) 필수 입력 항목입니다.`;
  }
  return null;
}

/**
 * 최소/최대 길이 검증
 * @param {string} value - 검증할 값
 * @param {number} minLength - 최소 길이
 * @param {number} maxLength - 최대 길이
 * @param {string} fieldName - 필드 이름
 * @returns {string|null} - 오류 메시지 또는 null
 */
export function validateLength(value, minLength, maxLength, fieldName) {
  if (!value) return null;

  if (minLength && value.length < minLength) {
    return `${fieldName}은(는) 최소 ${minLength}자 이상이어야 합니다.`;
  }

  if (maxLength && value.length > maxLength) {
    return `${fieldName}은(는) 최대 ${maxLength}자 이하여야 합니다.`;
  }

  return null;
}

/**
 * 두 값이 일치하는지 검증 (비밀번호 확인 등)
 * @param {string} value1 - 첫 번째 값
 * @param {string} value2 - 두 번째 값
 * @param {string} message - 오류 메시지
 * @returns {string|null} - 오류 메시지 또는 null
 */
export function validateMatch(value1, value2, message = '두 값이 일치하지 않습니다.') {
  if (value1 !== value2) {
    return message;
  }
  return null;
}

/**
 * 숫자 범위 검증
 * @param {number} value - 검증할 값
 * @param {number} min - 최소값
 * @param {number} max - 최대값
 * @param {string} fieldName - 필드 이름
 * @returns {string|null} - 오류 메시지 또는 null
 */
export function validateNumberRange(value, min, max, fieldName) {
  if (value === null || value === undefined) return null;

  const num = Number(value);
  if (isNaN(num)) {
    return `${fieldName}은(는) 숫자여야 합니다.`;
  }

  if (min !== undefined && num < min) {
    return `${fieldName}은(는) ${min} 이상이어야 합니다.`;
  }

  if (max !== undefined && num > max) {
    return `${fieldName}은(는) ${max} 이하여야 합니다.`;
  }

  return null;
}
