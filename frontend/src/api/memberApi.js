import api from './index'

export const memeberApi = {
  login: (credentials) => api.post('/auth/sign-in', credentials),
  // 개인 정보 조회
  getMemberProfile: (solvedAcId) => api.get(`/members/${solvedAcId}`),
  // 개인이 소속된 팀 정보 조회
  getMemberTeams: (solvedAcId) => api.get(`/members/${solvedAcId}/teams`),
}
