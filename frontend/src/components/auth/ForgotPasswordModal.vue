<!-- src/components/auth/ForgotPasswordModal.vue -->
<template>
  <div v-if="show" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <h2>비밀번호 찾기</h2>
      <!-- Step 1: Solved.ac ID 입력 및 인증코드 발급 -->
      <form v-if="currentStep === 1" @submit.prevent="handleGetVerificationCode">
        <div class="form-group">
          <label for="solvedAcId">Solved.ac ID</label>
          <input
            type="text"
            id="solvedAcId"
            v-model="solvedAcId"
            required
            placeholder="Solved.ac ID를 입력하세요"
            :disabled="isLoading"
          />
        </div>
        <div class="info-message" v-if="verificationMessage">
          {{ verificationMessage }}
        </div>
        <div class="error-message" v-if="error">{{ error }}</div>
        <div class="button-group">
          <button type="button" class="cancel-button" @click="closeModal">취소</button>
          <button type="submit" class="submit-button" :disabled="isLoading">
            <span v-if="isLoading">
              <font-awesome-icon :icon="['fas', 'spinner']" spin />
            </span>
            <span v-else>인증코드 발급</span>
          </button>
        </div>
      </form>

      <!-- Step 2: 인증코드 확인 -->
      <form v-if="currentStep === 2" @submit.prevent="handleVerifyCode">
        <div class="verification-info">
          <p>Solved.ac 프로필의 이름을 아래 인증코드로 변경해주세요:</p>
          <div class="verification-code-container">
            <div class="verification-code">{{ verificationCode }}</div>
            <button 
              type="button" 
              class="copy-button" 
              @click="copyVerificationCode"
              :title="copySuccess ? '복사됨!' : '복사하기'"
            >
              <font-awesome-icon 
                :icon="['fas', copySuccess ? 'check' : 'copy']" 
                :class="{ 'success': copySuccess }"
              />
            </button>
          </div>
        </div>
        <div class="info-message">
          프로필 이름을 변경한 후 아래 버튼을 클릭하세요.
        </div>
        <div class="error-message" v-if="error">{{ error }}</div>
        <div class="button-group">
          <button type="button" class="cancel-button" @click="closeModal">취소</button>
          <button type="submit" class="submit-button" :disabled="isLoading">
            <span v-if="isLoading">
              <font-awesome-icon :icon="['fas', 'spinner']" spin />
            </span>
            <span v-else>인증하기</span>
          </button>
        </div>
      </form>

      <!-- Step 3: 새 비밀번호 설정 -->
      <form v-if="currentStep === 3" @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="newPassword">새 비밀번호</label>
          <div class="password-input">
            <input
              :type="showNewPassword ? 'text' : 'password'"
              id="newPassword"
              v-model="newPassword"
              required
              placeholder="새 비밀번호를 입력하세요"
            />
            <button type="button" @click="showNewPassword = !showNewPassword">
              <font-awesome-icon :icon="['fas', showNewPassword ? 'eye-slash' : 'eye']" />
            </button>
          </div>
        </div>
        <div class="form-group">
          <label for="confirmPassword">새 비밀번호 확인</label>
          <div class="password-input">
            <input
              :type="showConfirmPassword ? 'text' : 'password'"
              id="confirmPassword"
              v-model="confirmPassword"
              required
              placeholder="새 비밀번호를 다시 입력하세요"
            />
            <button type="button" @click="showConfirmPassword = !showConfirmPassword">
              <font-awesome-icon :icon="['fas', showConfirmPassword ? 'eye-slash' : 'eye']" />
            </button>
          </div>
        </div>
        <div class="error-message" v-if="error">{{ error }}</div>
        <div class="button-group">
          <button type="button" class="cancel-button" @click="closeModal">취소</button>
          <button type="submit" class="submit-button" :disabled="isLoading">
            <span v-if="isLoading">
              <font-awesome-icon :icon="['fas', 'spinner']" spin />
            </span>
            <span v-else>비밀번호 변경</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { memberApi } from '@/api/memberApi'
import { authApi } from '@/api/AuthApi'

const props = defineProps({
  show: Boolean,
})

const emit = defineEmits(['close'])

const currentStep = ref(1)
const solvedAcId = ref('')
const verificationCode = ref('')
const verificationMessage = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const isLoading = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const copySuccess = ref(false)

const closeModal = () => {
  emit('close')
  resetForm()
}

const resetForm = () => {
  currentStep.value = 1
  solvedAcId.value = ''
  verificationCode.value = ''
  verificationMessage.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  error.value = ''
  showNewPassword.value = false
  showConfirmPassword.value = false
  copySuccess.value = false
}

// Step 1: 인증코드 발급
const handleGetVerificationCode = async () => {
  error.value = ''
  isLoading.value = true

  try {
    const response = await authApi.getVerificationCode(solvedAcId.value)
    verificationCode.value = response.data.verificationCode
    verificationMessage.value = '인증코드가 발급되었습니다.'
    currentStep.value = 2
  } catch (err) {
    error.value = err.response?.data?.message || '인증코드 발급에 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}

// Step 2: 인증코드 확인
const handleVerifyCode = async () => {
  error.value = ''
  isLoading.value = true

  try {
    await authApi.verifyCode(solvedAcId.value)
    currentStep.value = 3
  } catch (err) {
    error.value = err.response?.data?.message || '인증에 실패했습니다. Solved.ac 프로필 이름을 확인해주세요.'
  } finally {
    isLoading.value = false
  }
}

const copyVerificationCode = async () => {
  try {
    await navigator.clipboard.writeText(verificationCode.value)
    copySuccess.value = true
    setTimeout(() => {
      copySuccess.value = false
    }, 2000)
  } catch (err) {
    console.error('Failed to copy:', err)
  }
}

// Step 3: 비밀번호 변경
const handleSubmit = async () => {
  error.value = ''
  
  if (newPassword.value !== confirmPassword.value) {
    error.value = '새 비밀번호가 일치하지 않습니다.'
    return
  }

  if (newPassword.value.length < 8) {
    error.value = '비밀번호는 8자 이상이어야 합니다.'
    return
  }

  isLoading.value = true

  try {
    await memberApi.resetPassword({
      solvedAcId: solvedAcId.value,
      newPassword: newPassword.value
    })
    alert('비밀번호가 성공적으로 변경되었습니다. 새 비밀번호로 로그인해주세요.')
    closeModal()
  } catch (err) {
    error.value = err.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

input {
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
  margin-bottom: 1rem;
  font-size: 0.875rem;
  text-align: center;
  white-space: pre-line;
  background-color: #fff8f8;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ffebee;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

button {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-button {
  background-color: #f8f9fa;
  color: #333;
}

.cancel-button:hover {
  background-color: #e9ecef;
}

.submit-button {
  background-color: var(--samsung-blue);
  color: white;
}

.submit-button:hover {
  background-color: var(--samsung-blue-dark);
}

.submit-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.info-message {
  color: var(--samsung-blue);
  margin-bottom: 1rem;
  font-size: 0.875rem;
  text-align: center;
  background-color: #f8f9fa;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.verification-info {
  margin-bottom: 1rem;
  text-align: center;
}

.verification-code-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background-color: #f8f9fa;
  padding: 1rem;
  margin: 1rem 0;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.verification-code {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--samsung-blue);
  margin: 0;
  padding: 0;
  background: none;
  border: none;
}

.copy-button {
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.copy-button:hover {
  background-color: #e9ecef;
  color: var(--samsung-blue);
}

.copy-button .success {
  color: #28a745;
}
</style> 