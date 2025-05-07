import { useState, useEffect } from 'react';

/**
 * localStorage를 사용하여 상태를 관리하는 커스텀 훅
 * @param {string} key - 저장소에 사용될 키
 * @param {any} initialValue - 초기값
 * @returns {[any, function]} - 상태값과 업데이트 함수
 */
function useLocalStorage(key, initialValue) {
  // 초기 상태를 가져오는 함수 정의
  const initialize = () => {
    try {
      // localStorage에서 값 가져오기
      const item = localStorage.getItem(key);
      // 값이 있으면 파싱해서 반환, 없으면 초기값 반환
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  };

  // 상태 초기화
  const [storedValue, setStoredValue] = useState(initialize);

  // 상태 업데이트 함수
  const setValue = (value) => {
    try {
      // 함수로 전달되는 경우 처리
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      // 상태 업데이트
      setStoredValue(valueToStore);
      // localStorage에 저장
      localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(`Error setting localStorage key "${key}":`, error);
    }
  };

  // 다른 탭에서 localStorage가 변경되었을 때 동기화
  useEffect(() => {
    const handleStorageChange = (event) => {
      if (event.key === key && event.newValue) {
        setStoredValue(JSON.parse(event.newValue));
      }
    };

    // 이벤트 리스너 등록
    window.addEventListener('storage', handleStorageChange);
    
    // 클린업 함수
    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, [key]);

  return [storedValue, setValue];
}

export default useLocalStorage;