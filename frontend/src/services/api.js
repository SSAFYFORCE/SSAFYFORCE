import axios from 'axios';

// API 기본 설정 (Vite 환경 변수 방식으로 수정)
const API_BASE_URL = import.meta.env.PROD ? import.meta.env.VITE_API_URL_PROD : import.meta.env.VITE_API_URL_DEV;

// axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10초
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// 요청 인터셉터 - 모든 요청에 공통으로 적용할 로직
apiClient.interceptors.request.use(
  (config) => {
    // 로컬 스토리지에서 토큰 가져오기
    const token = localStorage.getItem('auth_token');

    // 토큰이 있으면 헤더에 추가
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 - 모든 응답에 공통으로 적용할 로직
apiClient.interceptors.response.use(
  (response) => {
    // 응답 데이터 가공 필요시 여기서 처리
    return response;
  },
  (error) => {
    // 오류 응답 처리
    const {response} = error;

    // 인증 오류 (401)
    if (response && response.status === 401) {
      localStorage.removeItem('auth_token');
      // 로그인 페이지로 리다이렉트 또는 다른 처리
      window.location.href = '/login';
    }

    // 서버 오류 (500)
    if (response && response.status >= 500) {
      console.error('서버 오류가 발생했습니다:', response.data);
      // 오류 알림 표시 등의 처리
    }

    return Promise.reject(error);
  }
);

// API 함수들
export const authAPI = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  register: (userData) => apiClient.post('/auth/register', userData),
  logout: () => apiClient.post('/auth/logout'),
  getProfile: () => apiClient.get('/auth/profile'),
};

export const userAPI = {
  getUsers: (params) => apiClient.get('/users', {params}),
  getUser: (id) => apiClient.get(`/users/${id}`),
  updateUser: (id, data) => apiClient.put(`/users/${id}`, data),
  deleteUser: (id) => apiClient.delete(`/users/${id}`),
};

// 기본 API 클라이언트 내보내기
export default apiClient;
