// src/stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, checkAuthStatus } from '@/utils/datatransferutil'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  // 계산된 속성
  const isLoggedIn = computed(() => !!user.value)

  // 인증 상태 확인
  const checkAuth = async () => {
    isLoading.value = true
    error.value = null

    try {
      const userData = await checkAuthStatus()
      user.value = userData
      return userData
    } catch (err) {
      error.value = err.message
      user.value = null
      return null
    } finally {
      isLoading.value = false
    }
  }

  // 로그인
  const loginUser = async (credentials) => {
    isLoading.value = true
    error.value = null

    try {
      const userData = await login(credentials)
      user.value = userData

      // Storage 이벤트 발생
      window.dispatchEvent(
        new StorageEvent('storage', {
          key: 'user',
          newValue: JSON.stringify(userData),
          url: window.location.href,
        }),
      )

      return userData
    } catch (err) {
      error.value = err.message
      return null
    } finally {
      isLoading.value = false
    }
  }

  // 로그아웃
  const logoutUser = async () => {
    isLoading.value = true
    error.value = null

    try {
      await logout()
      user.value = null

      // Storage 이벤트 발생
      window.dispatchEvent(
        new StorageEvent('storage', {
          key: 'user',
          newValue: null,
          url: window.location.href,
        }),
      )

      return true
    } catch (err) {
      error.value = err.message
      return false
    } finally {
      isLoading.value = false
    }
  }

  // 초기화 (앱 시작 시 호출)
  const initialize = () => {
    checkAuth()
  }

  return {
    user,
    isLoading,
    error,
    isLoggedIn,
    checkAuth,
    loginUser,
    logoutUser,
    initialize,
  }
})
