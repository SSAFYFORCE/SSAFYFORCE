import api from './index'

export const authApi = {
  // solved.ac 인증 관련
  getVerificationCode: (solvedAcId) => api.post('/solved-ac/verification-code', { solvedAcId }),
  verifyCode: (solvedAcId) => api.get(`/solved-ac/verify/${solvedAcId}`),

  // 회원가입/로그인/로그아웃
  signUp: (userData) => api.post('/auth/sign-up', userData),

  signIn: async (credentials) => {
    try {
      console.log('API 로그인 요청 데이터:', {
        solvedAcId: credentials.solvedAcId,
        password: credentials.password,
      })

      const response = await api.post('/auth/sign-in', {
        solvedAcId: credentials.solvedAcId,
        password: credentials.password,
      })

      console.log('API 로그인 응답:', response)
      return response
    } catch (error) {
      console.error('API 로그인 에러:', error.response || error)
      throw error
    }
  },

  signOut: () => api.post('/auth/sign-out'),
}
