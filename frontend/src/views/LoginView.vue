<!-- src/views/LoginView.vue -->
<template>
  <div class="login-view">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1>로그인</h1>
          <p>알고리즘 플랫폼에 오신 것을 환영합니다</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="username">사용자명</label>
            <input
              id="username"
              v-model="credentials.username"
              type="text"
              placeholder="사용자명을 입력하세요"
              class="form-input"
              :class="{ error: errors.username }"
              required
            />
            <span v-if="errors.username" class="error-message">
              {{ errors.username }}
            </span>
          </div>

          <div class="form-group">
            <label for="password">비밀번호</label>
            <div class="password-input-container">
              <input
                id="password"
                v-model="credentials.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="비밀번호를 입력하세요"
                class="form-input"
                :class="{ error: errors.password }"
                required
              />
              <button type="button" @click="togglePasswordVisibility" class="password-toggle">
                <font-awesome-icon :icon="showPassword ? ['fas', 'eye-slash'] : ['fas', 'eye']" />
              </button>
            </div>
            <span v-if="errors.password" class="error-message">
              {{ errors.password }}
            </span>
          </div>

          <div class="form-options">
            <label class="checkbox-container">
              <input v-model="rememberMe" type="checkbox" />
              <span class="checkmark"></span>
              로그인 상태 유지
            </label>
            <a href="#" class="forgot-password">비밀번호를 잊으셨나요?</a>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="login-button"
            :class="{ loading: loading }"
          >
            <template v-if="loading">
              <font-awesome-icon :icon="['fas', 'spinner']" spin />
              로그인 중...
            </template>
            <template v-else> 로그인 </template>
          </button>

          <div v-if="errorMessage" class="general-error">
            {{ errorMessage }}
          </div>
        </form>

        <div class="login-footer">
          <div class="divider">
            <span>또는</span>
          </div>

          <div class="social-login">
            <button class="social-btn github-btn">
              <font-awesome-icon :icon="['fab', 'github']" />
              GitHub으로 로그인
            </button>
            <button class="social-btn google-btn">
              <font-awesome-icon :icon="['fab', 'google']" />
              Google로 로그인
            </button>
          </div>

          <div class="signup-link">
            아직 계정이 없으신가요?
            <router-link to="/signup" class="signup-text">회원가입</router-link>
          </div>
        </div>
      </div>

      <div class="demo-credentials">
        <h3>데모 계정</h3>
        <p>사용자명: <strong>test</strong></p>
        <p>비밀번호: <strong>password</strong></p>
      </div>
    </div>

    <!-- 배경 애니메이션 -->
    <div class="background-animation">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/utils/datatransferutil'

const router = useRouter()

// 반응성 데이터
const credentials = reactive({
  username: '',
  password: '',
})

const errors = reactive({
  username: '',
  password: '',
})

const loading = ref(false)
const errorMessage = ref('')
const showPassword = ref(false)
const rememberMe = ref(false)

// 메서드
const validateForm = () => {
  errors.username = ''
  errors.password = ''

  if (!credentials.username) {
    errors.username = '사용자명을 입력해주세요.'
  }

  if (!credentials.password) {
    errors.password = '비밀번호를 입력해주세요.'
  }

  return !errors.username && !errors.password
}

const handleLogin = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const user = await login(credentials)

    // 로그인 성공 시 storage 이벤트를 수동으로 발생시켜 다른 컴포넌트에서 감지할 수 있도록 함
    window.dispatchEvent(
      new StorageEvent('storage', {
        key: 'user',
        newValue: JSON.stringify(user),
        url: window.location.href,
      }),
    )

    // 로그인 성공 시 홈으로 리다이렉트
    const redirectPath = router.currentRoute.value.query.redirect || '/'
    router.push(redirectPath)
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}
</script>

<style scoped>
.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.login-container {
  position: relative;
  z-index: 2;
  max-width: 400px;
  width: 100%;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 1rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.login-header p {
  color: #666;
  font-size: 1rem;
}

.login-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
  background: white;
}

.form-input:focus {
  outline: none;
  border-color: var(--samsung-blue);
  box-shadow: 0 0 0 3px var(--samsung-blue-alpha);
}

.form-input.error {
  border-color: #dc3545;
}

.password-input-container {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: color 0.2s;
}

.password-toggle:hover {
  color: var(--samsung-blue);
}

.error-message {
  display: block;
  color: #dc3545;
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 0.9rem;
  color: #666;
}

.checkbox-container input {
  margin-right: 0.5rem;
}

.forgot-password {
  color: var(--samsung-blue);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s;
}

.forgot-password:hover {
  color: var(--samsung-blue-dark);
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 0.875rem;
  background: var(--samsung-blue);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.login-button:hover:not(:disabled) {
  background: var(--samsung-blue-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(20, 40, 160, 0.3);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.general-error {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #f8d7da;
  color: #721c24;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
}

.login-footer {
  text-align: center;
}

.divider {
  position: relative;
  margin: 1.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e1e5e9;
  z-index: 1;
}

.divider span {
  background: white;
  padding: 0 1rem;
  position: relative;
  z-index: 2;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  background: white;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
}

.social-btn:hover {
  border-color: #333;
  background: #f8f9fa;
}

.github-btn:hover {
  border-color: #333;
  background: #333;
  color: white;
}

.google-btn:hover {
  border-color: #db4437;
  background: #db4437;
  color: white;
}

.signup-link {
  color: #666;
  font-size: 0.9rem;
}

.signup-text {
  color: var(--samsung-blue);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
}

.signup-text:hover {
  color: var(--samsung-blue-dark);
  text-decoration: underline;
}

.demo-credentials {
  background: rgba(255, 255, 255, 0.9);
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  border: 1px solid rgba(20, 40, 160, 0.2);
}

.demo-credentials h3 {
  color: var(--samsung-blue);
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.demo-credentials p {
  color: #666;
  margin: 0.25rem 0;
  font-size: 0.9rem;
}

.demo-credentials strong {
  color: var(--samsung-blue);
}

/* 배경 애니메이션 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.floating-shapes {
  position: relative;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(45deg, var(--samsung-blue-alpha), var(--samsung-blue-light));
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 60px;
  height: 60px;
  top: 20%;
  right: 15%;
  animation-delay: 1s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 2s;
}

.shape-4 {
  width: 70px;
  height: 70px;
  bottom: 30%;
  right: 10%;
  animation-delay: 3s;
}

.shape-5 {
  width: 50px;
  height: 50px;
  top: 60%;
  left: 60%;
  animation-delay: 4s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 1;
  }
}

/* 반응형 디자인 */
@media (max-width: 480px) {
  .login-view {
    padding: 1rem;
  }

  .login-card {
    padding: 1.5rem;
  }

  .login-header h1 {
    font-size: 1.75rem;
  }
}
</style>
