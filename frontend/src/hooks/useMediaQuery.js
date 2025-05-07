import {useState, useEffect} from 'react';

/**
 * 미디어 쿼리 상태를 추적하는 커스텀 훅
 * @param {string} query - CSS 미디어 쿼리 문자열
 * @returns {boolean} - 미디어 쿼리 일치 여부
 */
function useMediaQuery(query) {
  // 초기 상태 설정
  const getMatches = (queryString) => {
    // SSR 대응: window 객체가 없는 경우
    if (typeof window !== 'undefined') {
      return window.matchMedia(queryString).matches;
    }
    return false;
  };

  const [matches, setMatches] = useState(getMatches(query));

  // 이벤트 리스너를 통한 상태 업데이트
  useEffect(() => {
    const mediaQuery = window.matchMedia(query);

    // 상태 업데이트 함수
    const updateMatches = () => {
      setMatches(mediaQuery.matches);
    };

    // 초기 상태 설정
    updateMatches();

    // 이벤트 리스너 등록
    // 최신 브라우저용 API
    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', updateMatches);
      return () => {
        mediaQuery.removeEventListener('change', updateMatches);
      };
    }
    // 레거시 브라우저 지원
    else if (mediaQuery.addListener) {
      mediaQuery.addListener(updateMatches);
      return () => {
        mediaQuery.removeListener(updateMatches);
      };
    }
  }, [query]);

  return matches;
}

/**
 * 반응형 디자인을 위한 미디어 쿼리 훅
 * @returns {object} - 다양한 화면 크기에 대한 일치 여부
 */
export function useResponsive() {
  const isMobile = useMediaQuery('(max-width: 639px)');
  const isTablet = useMediaQuery('(min-width: 640px) and (max-width: 1023px)');
  const isDesktop = useMediaQuery('(min-width: 1024px)');
  const isLargeDesktop = useMediaQuery('(min-width: 1280px)');

  return {
    isMobile,
    isTablet,
    isDesktop,
    isLargeDesktop,
    // 유틸리티 속성
    isSmallScreen: isMobile || isTablet,
    isLargeScreen: isDesktop || isLargeDesktop,
  };
}

export default useMediaQuery;
