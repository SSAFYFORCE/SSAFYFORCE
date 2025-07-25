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
  const isLoggedIn = computed(() => {
    return !!user.value && !!localStorage.getItem('accessToken')
  })

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
      const response = await authApi.signIn(credentials)

      if (!response?.data?.accessToken) {
        throw new Error('로그인 응답에 토큰이 없습니다.')
      }

      // 토큰 저장
      localStorage.setItem('accessToken', response.data.accessToken)
      if (response.data.refreshToken) {
        localStorage.setItem('refreshToken', response.data.refreshToken)
      }

      // 사용자 정보 가져오기
      try {
        const userResponse = await memberApi.getMyProfile()
        
        if (!userResponse?.data) {
          throw new Error('사용자 정보가 없습니다.')
        }

        user.value = {
          ...userResponse.data,
          solvedAcId: credentials.solvedAcId,
          isAuthenticated: true,
        }
        return true // 로그인 성공
      } catch (profileError) {
        console.error('사용자 정보 조회 실패:', profileError)
        user.value = null
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        throw new Error('사용자 정보를 가져오는데 실패했습니다.')
      }
    } catch (err) {
      console.error('로그인 실패:', err)
      error.value = err.message || '로그인에 실패했습니다.'
      user.value = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      return false // 로그인 실패
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
    const token = localStorage.getItem('accessToken')
    if (!token) {
      user.value = null
      return
    }

    try {
      const response = await memberApi.getMyProfile()
      console.log('사용자 정보 초기화 응답:', response)
      
      if (response?.data) {
        user.value = {
          ...response.data,
          memberId: response.data.id,
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

  // 프로필 정보 업데이트
  const updateUserProfile = async () => {
    if (!user.value) return
    
    try {
      const response = await memberApi.getMyProfile()
      if (response?.data) {
        user.value = {
          ...response.data,
          solvedAcId: user.value.solvedAcId, // 기존 solvedAcId 유지
          isAuthenticated: true,
        }
      }
    } catch (error) {
      console.error('프로필 정보 업데이트 실패:', error)
    }
  }

  // 프로필 이미지만 업데이트
  const updateProfileImage = (imageUrl) => {
    if (user.value) {
      user.value.profileImage = imageUrl
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
    updateUserProfile,
    updateProfileImage,
  }
})
