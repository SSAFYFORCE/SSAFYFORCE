import axios from 'axios'

// API 기본 설정
const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // 백엔드 서버 주소
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})
export default api
