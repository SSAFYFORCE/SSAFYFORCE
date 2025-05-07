// 사용자 역할 정의
export const USER_ROLES = {
  ADMIN: 'admin',
  EDITOR: 'editor',
  USER: 'user',
  GUEST: 'guest',
};

// 알림 유형
export const NOTIFICATION_TYPES = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning',
  INFO: 'info',
};

// 애플리케이션 경로
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  PROFILE: '/profile',
  SETTINGS: '/settings',
  NOT_FOUND: '*',
};

// API 응답 상태 코드
export const STATUS_CODES = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
};

// 로컬 스토리지 키
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'auth_token',
  USER_PREFERENCES: 'user_preferences',
  THEME: 'app_theme',
};

// 테마 모드
export const THEME_MODES = {
  LIGHT: 'light',
  DARK: 'dark',
  SYSTEM: 'system',
};

// 애플리케이션 설정
export const APP_CONFIG = {
  NAME: '리액트 앱',
  VERSION: '1.0.0',
  COPYRIGHT_YEAR: new Date().getFullYear(),
};

// 페이지네이션 설정
export const PAGINATION = {
  DEFAULT_PAGE: 1,
  DEFAULT_PER_PAGE: 10,
  PER_PAGE_OPTIONS: [5, 10, 20, 50, 100],
};
