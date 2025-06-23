// src/stores/auth.js
import { defineStore } from 'pinia'
import { authAPI, memberAPI } from '@/utils/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    isAuthenticated: !!localStorage.getItem('accessToken'),
    loading: false,
    error: null,
  }),

  getters: {
    getUser: (state) => state.user,
    isLoggedIn: (state) => state.isAuthenticated,
  },

  actions: {
    // 초기화 함수 추가
    async initialize() {
      const accessToken = localStorage.getItem('accessToken')
      if (accessToken) {
        try {
          await this.fetchUserProfile()
        } catch (error) {
          // 토큰이 만료되었거나 유효하지 않은 경우
          this.clearTokens()
        }
      }
    },

    setTokens(accessToken, refreshToken) {
      this.accessToken = accessToken
      this.refreshToken = refreshToken
      this.isAuthenticated = true
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
    },

    clearTokens() {
      this.accessToken = null
      this.refreshToken = null
      this.isAuthenticated = false
      this.user = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    },

    async fetchUserProfile() {
      const response = await memberAPI.getMyProfile()
      this.user = response.data
      return response.data
    },

    async checkNickname(solvedAcId) {
      const response = await authAPI.checkNickname(solvedAcId)
      return response
    },

    async getVerificationCode(solvedAcId) {
      const response = await authAPI.getVerificationCode(solvedAcId)
      return response
    },

    async verifyCode(solvedAcId) {
      const response = await authAPI.verifyCode(solvedAcId)
      return response
    },

    async signUp(userData) {
      try {
        const response = await authAPI.signUp(userData)
        return response
      } catch (error) {
        console.error('회원가입 실패:', error)
        throw error
      }
    },

    async signIn(credentials) {
      try {
        this.loading = true
        this.error = null
        const response = await authAPI.signIn(credentials)
        const { accessToken, refreshToken } = response.data
        this.setTokens(accessToken, refreshToken)

        // 프로필 정보 조회
        await this.fetchUserProfile()

        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || '로그인에 실패했습니다.'
        throw error
      } finally {
        this.loading = false
      }
    },

    async signOut() {
      try {
        await authAPI.signOut()
      } finally {
        this.clearTokens()
      }
    },
  },
})
