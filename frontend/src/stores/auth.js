// src/stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/AuthApi'
import { memberApi } from '@/api/memberApi'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  // 계산된 속성
  const isLoggedIn = computed(() => !!user.value)

  // 회원가입 관련 함수들
  const checkNickname = async (solvedAcId) => {
    try {
      const response = await memberApi.checkNickname(solvedAcId)
      return response.data
    } catch (error) {
      console.error('닉네임 확인 실패:', error)
      throw error
    }
  }

  const getVerificationCode = async (solvedAcId) => {
    try {
      const response = await authApi.getVerificationCode(solvedAcId)
      return response
    } catch (error) {
      console.error('인증 코드 발급 실패:', error)
      throw error
    }
  }

  const verifyCode = async (solvedAcId) => {
    try {
      const response = await authApi.verifyCode(solvedAcId)
      return response
    } catch (error) {
      console.error('인증 코드 확인 실패:', error)
      throw error
    }
  }

  const signUp = async (userData) => {
    try {
      const response = await authApi.signUp(userData)
      return response
    } catch (error) {
      console.error('회원가입 실패:', error)
      throw error
    }
  }

  // 로그인
  const login = async (credentials) => {
    isLoading.value = true
    error.value = null

    try {
      console.log('로그인 요청:', credentials)
      const response = await authApi.signIn(credentials)
      console.log('로그인 응답:', response)

      if (response?.data?.accessToken) {
        // 토큰 저장
        localStorage.setItem('accessToken', response.data.accessToken)
        if (response.data.refreshToken) {
          localStorage.setItem('refreshToken', response.data.refreshToken)
        }

        // 사용자 정보 가져오기
        try {
          // 토큰 설정 후 잠시 대기
          await new Promise((resolve) => setTimeout(resolve, 100))

          const userResponse = await memberApi.getMyProfile()
          console.log('사용자 정보 조회 응답:', userResponse)

          if (userResponse?.data) {
            user.value = {
              ...userResponse.data,
              solvedAcId: credentials.solvedAcId,
              isAuthenticated: true,
            }
            return user.value
          } else {
            throw new Error('사용자 정보가 없습니다.')
          }
        } catch (profileError) {
          console.error('사용자 정보 조회 실패:', profileError)
          // 기본 사용자 정보로 설정
          user.value = {
            solvedAcId: credentials.solvedAcId,
            isAuthenticated: true,
          }
          return user.value
        }
      }

      throw new Error('로그인 응답에 토큰이 없습니다.')
    } catch (err) {
      console.error('로그인 실패:', err)
      error.value = err.message || '로그인에 실패했습니다.'
      user.value = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // 로그아웃
  const logout = async () => {
    isLoading.value = true
    error.value = null

    try {
      await authApi.signOut()
      user.value = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      return true
    } catch (err) {
      error.value = err.message
      return false
    } finally {
      isLoading.value = false
    }
  }

  // 초기화 (앱 시작 시 호출)
  const initialize = async () => {
    if (localStorage.getItem('accessToken')) {
      try {
        const response = await memberApi.getMyProfile()
        console.log('사용자 정보 초기화 응답:', response)
        if (response?.data) {
          user.value = {
            ...response.data,
            isAuthenticated: true,
          }
        } else {
          throw new Error('사용자 정보가 없습니다.')
        }
      } catch (error) {
        console.error('사용자 정보 초기화 실패:', error)
        user.value = null
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
      }
    }
  }

  return {
    // State
    user,
    isLoading,
    error,

    // Getters
    isLoggedIn,

    // Actions
    initialize,
    checkNickname,
    getVerificationCode,
    verifyCode,
    signUp,
    login,
    logout,
  }
})
