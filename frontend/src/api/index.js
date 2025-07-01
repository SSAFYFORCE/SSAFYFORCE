import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api/v1'

// axios 인스턴스 생성
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000, // 10초
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // CORS 인증 설정 추가
})

// API 상태 확인
export const checkApiStatus = async () => {
  try {
    const response = await axios.get(`${BASE_URL}/auth/health-check`, { timeout: 3000 })
    return response.data.status === 'UP'
  } catch (error) {
    console.error('API 서버 연결 실패:', error.message)
    return false
  }
}

// 에러 핸들러
const handleApiError = (error) => {
  // 서버 연결 실패
  if (!error.response) {
    return Promise.reject({
      response: {
        status: 503,
        data: {
          message: '서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.',
        },
      },
    })
  }

  // 시간 초과
  if (error.code === 'ECONNABORTED') {
    return Promise.reject({
      response: {
        status: 408,
        data: {
          message: '요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.',
        },
      },
    })
  }

  return Promise.reject(error)
}

// 요청 인터셉터
api.interceptors.request.use(
  async (config) => {
    // API 서버 상태 확인
    const isApiAvailable = await checkApiStatus()
    if (!isApiAvailable) {
      throw new Error('API 서버에 연결할 수 없습니다.')
    }

    // 토큰 처리
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    console.log('API 요청 설정:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data,
    })

    return config
  },
  (error) => handleApiError(error),
)

// 응답 인터셉터
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // 에러 응답 기본 처리
    const processedError = handleApiError(error)

    // 401 에러 처리 (토큰 만료)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) {
          throw new Error('리프레시 토큰이 없습니다.')
        }

        const response = await api.post('/auth/refresh', null, {
          headers: {
            Authorization: `Bearer ${refreshToken}`,
          },
        })

        const { accessToken } = response.data
        localStorage.setItem('accessToken', accessToken)
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return api(originalRequest)
      } catch (error) {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject({
          response: {
            status: 401,
            data: {
              message: '세션이 만료되었습니다. 다시 로그인해주세요.',
            },
          },
        })
      }
    }

    return processedError
  },
)

export default api
