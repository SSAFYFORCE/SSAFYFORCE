import api from './index'

export const memberApi = {
  // 닉네임 중복 확인
  checkNickname: (solvedAcId) =>
    api.get('/members/check-nickname', {
      params: { solvedAcId },
    }),

  // 내 정보 조회
  getMyProfile: async () => {
    try {
      const token = localStorage.getItem('accessToken')
      if (!token) {
        throw new Error('인증 토큰이 없습니다.')
      }

      const response = await api.get('/members/me', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      console.log('프로필 조회 응답:', response)
      return response
    } catch (error) {
      console.error('프로필 조회 에러:', error.response || error)
      throw error
    }
  },

  // 회원 정보 수정
  updateProfile: (updateData) => api.patch('/members/me', updateData),

  // 비밀번호 변경 (로그인 후)
  changePassword: (passwordData) => api.patch('/members/password', passwordData),

  // 비밀번호 재설정 (로그인 전)
  resetPassword: (passwordData) => api.post('/members/password/reset', passwordData),

  // 회원 탈퇴
  deleteMember: () => api.delete('/members/me'),

  // 특정 회원 정보 조회
  getMemberProfile: (solvedAcId) => api.get(`/members/${solvedAcId}`),

  // 회원이 소속된 팀 정보 조회
  getMemberTeams: (solvedAcId) => api.get(`/members/${solvedAcId}/teams`),

  // 프로필 동기화
  syncProfile: async (solvedAcId) => {
    try {
      const response = await api.post(`/members/${solvedAcId}/sync`)
      console.log('프로필 동기화 응답:', response)
      return response
    } catch (error) {
      console.error('프로필 동기화 에러:', error.response || error)
      throw error
    }
  },
}
