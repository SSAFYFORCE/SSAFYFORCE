<!-- src/views/LoginView.vue -->
<template>
  <div class="login-container">
    <div class="login-box">
      <h1>로그인</h1>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="solvedAcId">Solved.ac ID</label>
          <input
            type="text"
            id="solvedAcId"
            v-model="solvedAcId"
            required
            placeholder="Solved.ac ID를 입력하세요"
            :class="{ 'error-input': error }"
          />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <div class="password-input">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="password"
              required
              placeholder="비밀번호를 입력하세요"
              :class="{ 'error-input': error }"
            />
            <button type="button" @click="showPassword = !showPassword">
              <font-awesome-icon :icon="['fas', showPassword ? 'eye-slash' : 'eye']" />
            </button>
          </div>
        </div>
        <div class="error-message" v-if="error">{{ error }}</div>
        <div class="form-options">
          <div class="remember-me">
            <input type="checkbox" id="remember" v-model="rememberMe" />
            <label for="remember">로그인 상태 유지</label>
          </div>
          <a href="#" @click.prevent="showForgotPasswordModal = true">비밀번호를 잊으셨나요?</a>
        </div>
        <button type="submit" :disabled="isLoading" class="login-button">
          <span v-if="isLoading">
            <font-awesome-icon :icon="['fas', 'spinner']" spin />
          </span>
          <span v-else>로그인</span>
        </button>
        <div class="signup-link">
          계정이 없으신가요? <router-link to="/signup">회원가입</router-link>
        </div>
      </form>
    </div>
    <ForgotPasswordModal
      :show="showForgotPasswordModal"
      @close="showForgotPasswordModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ForgotPasswordModal from '@/components/auth/ForgotPasswordModal.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const solvedAcId = ref('')
const password = ref('')
const rememberMe = ref(false)
const showPassword = ref(false)
const error = ref('')
const isLoading = ref(false)
const showForgotPasswordModal = ref(false)

const handleLogin = async () => {
  error.value = ''
  isLoading.value = true

  try {
    const loginSuccess = await authStore.login({
      solvedAcId: solvedAcId.value,
      password: password.value,
    })

    if (loginSuccess) {
      // 로그인 성공 시에만 메인 페이지로 이동
      router.push('/')
    } else {
      // 로그인 실패
      error.value = 'Solved.ac ID 또는 비밀번호가 일치하지 않습니다.'
      password.value = ''
    }
  } catch (err) {
    error.value = 'Solved.ac ID 또는 비밀번호가 일치하지 않습니다.'
    password.value = ''
  } finally {
    isLoading.value = false
  }
}

// 컴포넌트 마운트 시 URL 확인 및 수정
onMounted(() => {
  if (route.fullPath !== '/login#/login') {
    router.push('/login#/login')
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px);
  padding: 2rem;
  background-color: #f8f9fa;
}

.login-box {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.password-input {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input button {
  position: absolute;
  right: 0.75rem;
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
}

.error-message {
  color: #dc3545;
  margin: 0.5rem 0;
  font-size: 0.875rem;
  font-weight: 500;
  text-align: center;
  padding: 0.5rem;
  background-color: #fff8f8;
  border-radius: 4px;
  border: 1px solid #ffebee;
}

.error-input {
  border-color: #dc3545 !important;
}

.error-input:focus {
  box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-options a {
  color: var(--samsung-blue);
  text-decoration: none;
  font-size: 0.875rem;
}

.form-options a:hover {
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 0.75rem;
  background-color: var(--samsung-blue);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: var(--samsung-blue-dark);
}

.login-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.signup-link {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.875rem;
}

.signup-link a {
  color: var(--samsung-blue);
  text-decoration: none;
}

.signup-link a:hover {
  text-decoration: underline;
}

.form-links {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  font-size: 0.875rem;
}

.form-links a {
  color: var(--samsung-blue);
  text-decoration: none;
}

.form-links a:hover {
  text-decoration: underline;
}
</style>
