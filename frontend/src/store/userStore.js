import {atom} from 'jotai';
import {authAPI} from '../services/api';

// 사용자 상태를 위한 atom
export const userAtom = atom(null);

// 로딩 상태를 위한 atom
export const isLoadingAtom = atom(false);

// 오류 상태를 위한 atom
export const errorAtom = atom(null);

// 로그인 여부를 확인하는 파생 atom
export const isLoggedInAtom = atom((get) => get(userAtom) !== null);

// 사용자 역할에 따른 권한 확인 함수
export const hasPermissionAtom = atom((get) => (requiredRole) => {
  const user = get(userAtom);
  if (!user) return false;

  // 'admin' 역할은 모든 권한을 가짐
  if (user.role === 'admin') return true;

  // 특정 역할 확인
  return user.role === requiredRole;
});

// 로그인 액션
export const loginAction = atom(null, async (get, set, credentials) => {
  // 로딩 상태 시작
  set(isLoadingAtom, true);
  set(errorAtom, null);

  try {
    // API 호출
    const response = await authAPI.login(credentials);
    // 토큰 저장
    localStorage.setItem('auth_token', response.data.token);
    // 사용자 정보 저장
    set(userAtom, response.data.user);

    return response.data;
  } catch (error) {
    // 오류 처리
    set(errorAtom, error.response?.data?.message || '로그인에 실패했습니다.');
    throw error;
  } finally {
    // 로딩 상태 종료
    set(isLoadingAtom, false);
  }
});

// 로그아웃 액션
export const logoutAction = atom(null, async (get, set) => {
  // 로딩 상태 시작
  set(isLoadingAtom, true);

  try {
    // API 호출 (선택적)
    await authAPI.logout();
  } catch (error) {
    console.error('로그아웃 오류:', error);
  } finally {
    // 토큰 제거
    localStorage.removeItem('auth_token');
    // 사용자 정보 초기화
    set(userAtom, null);
    set(errorAtom, null);
    set(isLoadingAtom, false);
  }
});

// 사용자 프로필 가져오기 액션
export const fetchProfileAction = atom(null, async (get, set) => {
  // 이미 로그인 상태이면 스킵
  if (get(userAtom)) return;

  // 토큰이 없으면 스킵
  const token = localStorage.getItem('auth_token');
  if (!token) return;

  // 로딩 상태 시작
  set(isLoadingAtom, true);

  try {
    // API 호출
    const response = await authAPI.getProfile();
    // 사용자 정보 저장
    set(userAtom, response.data);
  } catch (error) {
    // 오류 시 토큰 제거
    console.log(error.message);
    localStorage.removeItem('auth_token');
    set(errorAtom, '세션이 만료되었습니다. 다시 로그인해주세요.');
  } finally {
    // 로딩 상태 종료
    set(isLoadingAtom, false);
  }
});
