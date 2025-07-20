<template>
  <div class="signup-view">
    <div class="signup-container">
      <div class="signup-card">
        <div class="signup-header">
          <h1>회원가입</h1>
          <p>SSAFY FORCE에 오신 것을 환영합니다</p>
        </div>

        <!-- 단계 표시 -->
        <div class="signup-steps">
          <div
            v-for="(step, index) in steps"
            :key="step.title"
            class="step"
            :class="{
              active: currentStep === index,
              completed: currentStep > index,
            }"
          >
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-title">{{ step.title }}</div>
          </div>
        </div>

        <!-- 단계별 폼 -->
        <div class="signup-form">
          <!-- 1단계: solved.ac ID 확인 -->
          <div v-if="currentStep === 0" class="step-content">
            <div class="form-group">
              <label for="solvedAcId">solved.ac ID</label>
              <div class="input-with-button">
                <input
                  id="solvedAcId"
                  v-model="formData.solvedAcId"
                  type="text"
                  placeholder="solved.ac ID를 입력하세요"
                  class="form-input"
                  :class="{ error: errors.solvedAcId }"
                  :disabled="isIdVerified"
                />
                <button
                  @click="checkSolvedAcId"
                  class="verify-button"
                  :disabled="!formData.solvedAcId || isIdVerified || isChecking"
                >
                  {{ isChecking ? '확인 중...' : '중복 확인' }}
                </button>
              </div>
              <span v-if="errors.solvedAcId" class="error-message">
                {{ errors.solvedAcId }}
              </span>
              <span v-if="isIdVerified" class="success-message">
                {{ '사용 가능한 ID입니다.' }}
              </span>
            </div>
          </div>

          <!-- 2단계: 인증 코드 발급 및 확인 -->
          <div v-if="currentStep === 1" class="step-content">
            <div class="verification-info">
              <h3>인증 코드 확인</h3>
              <p>1. 아래 버튼을 클릭하여 인증 코드를 발급받으세요.</p>
              <p>
                2. solved.ac 사이트에서 프로필의 name 또는 nameNative 필드에 발급받은 인증 코드를
                입력하세요.
              </p>
              <p>3. 저장 후 아래 확인 버튼을 클릭하세요.</p>
            </div>

            <div class="verification-actions">
              <button @click="getVerificationCode" class="code-button" :disabled="isGettingCode">
                {{ isGettingCode ? '발급 중...' : '인증 코드 발급' }}
              </button>

              <transition name="fade">
                <div v-if="verificationCode" class="verification-code">
                  <div class="image-container">
                    <span class="image-caption">Solved.ac 프로필 이름 설정 예시</span>
                  </div>
                  <div class="code-display">
                    <span>인증 코드:</span>
                    <code>{{ verificationCode }}</code>
                    <button
                      @click="copyVerificationCode"
                      class="copy-button"
                      :class="{ success: copySuccess }"
                      title="클립보드에 복사"
                    >
                      <font-awesome-icon :icon="['fas', 'copy']" />
                      {{ copySuccess ? '복사됨' : '복사' }}
                    </button>
                  </div>
                  <div class="code-instructions">
                    <p>1. 위 인증 코드를 복사하세요.</p>
                    <p>
                      2.
                      <a
                        href="https://solved.ac/settings/profile"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="link"
                      >
                        solved.ac 프로필 설정
                      </a>
                      페이지로 이동하세요.
                    </p>

                    <img
                      src="@/assets/images/솔브닥인증안내1.png"
                      alt="인증 예시"
                      class="guide-image"
                    />
                    <br />
                    <p>3. "모국어로 작성" 또는 "영어로 작성" 필드에 인증코드를 붙여넣기 하세요.</p>

                    <img
                      src="@/assets/images/솔브닥인증안내2.png"
                      alt="인증 예시"
                      class="guide-image"
                    />
                    <br />
                    <p>4. "프로필에 이름 표시"를 켜주세요.</p>

                    <img
                      src="@/assets/images/솔브닥인증안내3.png"
                      alt="인증 예시"
                      class="guide-image"
                    />
                    <br />
                    <p>5. 저장 후, 인증 버튼을 클릭하세요.</p>
                  </div>
                </div>
              </transition>

              <button
                @click="verifyCode"
                class="verify-button"
                :disabled="!verificationCode || isVerifying"
              >
                {{ isVerifying ? '확인 중...' : '인증 확인' }}
              </button>
            </div>

            <!-- 인증 확인 버튼을 눌렀을 때만 표시되는 메시지 -->
            <div
              v-if="verificationMessage"
              :class="['message-box', isCodeVerified ? 'success-message-box' : 'error-message-box']"
            >
              {{ verificationMessage }}
            </div>
          </div>

          <!-- 3단계: 회원 정보 입력 -->
          <div v-if="currentStep === 2" class="step-content">
            <div class="form-group">
              <label for="name">이름 (본명)</label>
              <input
                id="name"
                v-model="formData.name"
                type="text"
                placeholder="이름을 입력하세요"
                class="form-input"
                :class="{ error: errors.name }"
              />
              <span v-if="errors.name" class="error-message">
                {{ errors.name }}
              </span>
              <span class="input-help">팀장이 팀 가입 요청을 확인할 때 사용됩니다</span>
            </div>

            <div class="form-group">
              <label for="password">비밀번호</label>
              <input
                id="password"
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="8자 이상의 비밀번호를 입력하세요"
                class="form-input"
                :class="{ error: errors.password }"
              />
              <span v-if="errors.password" class="error-message">
                {{ errors.password }}
              </span>
              <span class="input-help">8자 이상 입력해주세요</span>
            </div>

            <div class="form-group">
              <label for="passwordConfirm">비밀번호 확인</label>
              <input
                id="passwordConfirm"
                v-model="formData.passwordConfirm"
                :type="showPassword ? 'text' : 'password'"
                placeholder="비밀번호를 다시 입력하세요"
                class="form-input"
                :class="{ error: errors.passwordConfirm }"
              />
              <span v-if="errors.passwordConfirm" class="error-message">
                {{ errors.passwordConfirm }}
              </span>
            </div>

            <div class="form-group">
              <label class="checkbox-container">
                <input v-model="showPassword" type="checkbox" />
                비밀번호 표시
              </label>
            </div>
          </div>

          <!-- 전역 에러 메시지 (3단계에서만 표시) -->
          <div
            v-if="error && currentStep === 2"
            :class="[
              'message-box',
              error.includes('성공') ? 'success-message-box' : 'error-message-box',
            ]"
          >
            {{ error }}
          </div>

          <!-- 단계 이동 버튼 -->
          <div class="step-buttons">
            <button v-if="currentStep > 0" @click="prevStep" class="prev-button">이전</button>
            <button
              v-if="currentStep < steps.length - 1"
              @click="nextStep"
              class="next-button"
              :disabled="!canProceed"
            >
              다음
            </button>
            <button
              v-else
              @click="submitSignUp"
              class="submit-button"
              :disabled="!canSubmit || isSubmitting"
            >
              {{ isSubmitting ? '가입 중...' : '가입하기' }}
            </button>
          </div>
        </div>

        <div class="signup-footer">
          이미 계정이 있으신가요?
          <router-link to="/login" class="login-link">로그인</router-link>
        </div>
      </div>
    </div>

    <!-- Toast 알림 -->
    <transition name="toast">
      <div v-if="showToast" :class="['toast', toastType]">
        <div class="toast-content">
          <font-awesome-icon
            :icon="['fas', toastType === 'success' ? 'check-circle' : 'times-circle']"
            class="toast-icon"
          />
          <span class="toast-message">{{ toastMessage }}</span>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 회원가입 단계
const steps = [{ title: 'solved.ac ID 확인' }, { title: '인증' }, { title: '정보 입력' }]

const currentStep = ref(0)
const formData = reactive({
  solvedAcId: '',
  name: '',
  password: '',
  passwordConfirm: '',
  classLevel: '',
})

const errors = reactive({
  solvedAcId: '',
  name: '',
  password: '',
  passwordConfirm: '',
  classLevel: '',
})

const error = ref('')
const isChecking = ref(false)
const isIdVerified = ref(false)
const isGettingCode = ref(false)
const verificationCode = ref('')
const verificationMessage = ref('') // 인증 확인 전용 메시지
const isVerifying = ref(false)
const isCodeVerified = ref(false)
const showPassword = ref(false)
const isSubmitting = ref(false)
const copySuccess = ref(false)

// Toast 관련 상태
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success') // 'success' 또는 'error'

// 단계별 진행 가능 여부
const canProceed = computed(() => {
  switch (currentStep.value) {
    case 0:
      return isIdVerified.value
    case 1:
      return isCodeVerified.value
    default:
      return false
  }
})

// 최종 제출 가능 여부
const canSubmit = computed(() => {
  return (
    formData.name &&
    formData.password &&
    formData.password === formData.passwordConfirm &&
    formData.password.length >= 8
  )
})

// ID 중복 확인
const checkSolvedAcId = async () => {
  if (!formData.solvedAcId) {
    errors.solvedAcId = 'solved.ac ID를 입력해주세요.'
    return
  }

  isChecking.value = true
  errors.solvedAcId = ''
  try {
    const result = await authStore.checkNickname(formData.solvedAcId)
    if (result.available) {
      isIdVerified.value = true
    } else {
      isIdVerified.value = false
      errors.solvedAcId = result.message || '이미 사용 중인 ID입니다.'
    }
  } catch (error) {
    console.error('ID 확인 중 에러:', error)
    isIdVerified.value = false
    errors.solvedAcId = '서버와의 통신 중 오류가 발생했습니다.'
  } finally {
    isChecking.value = false
  }
}

// 인증 코드 발급
const getVerificationCode = async () => {
  try {
    isGettingCode.value = true
    verificationMessage.value = '' // 기존 메시지 초기화
    const response = await authStore.getVerificationCode(formData.solvedAcId)
    verificationCode.value = response.data.verificationCode

    // 인증코드 발급 시간 저장
    localStorage.setItem('verificationCodeTime', new Date().toISOString())

    // 성공적으로 발급된 경우에는 메시지를 표시하지 않음
  } catch (err) {
    verificationMessage.value =
      err.response?.data?.message || '인증 코드 발급 중 오류가 발생했습니다.'
    verificationCode.value = ''
  } finally {
    isGettingCode.value = false
  }
}

// 인증 코드 확인
const verifyCode = async () => {
  if (!verificationCode.value) {
    verificationMessage.value = '먼저 인증코드를 발급받아주세요.'
    return
  }

  try {
    isVerifying.value = true
    verificationMessage.value = ''

    // solved.ac 프로필에 변경사항이 반영될 시간을 주기 위해 잠시 대기
    await new Promise((resolve) => setTimeout(resolve, 2000))

    const response = await authStore.verifyCode(formData.solvedAcId)

    if (response.data.verified) {
      isCodeVerified.value = true
      localStorage.setItem('verificationCode', verificationCode.value)
      localStorage.setItem('verifiedSolvedAcId', formData.solvedAcId)
      verificationMessage.value =
        '인증이 완료되었습니다. 다음 버튼을 눌러 회원 정보를 입력해주세요.'

      // 자동으로 다음 단계로 이동하는 코드 제거
    } else {
      isCodeVerified.value = false
      verificationMessage.value =
        '인증에 실패했습니다. solved.ac 프로필에 인증코드가 정확히 입력되었는지 확인하고, 저장 후 1분 정도 기다렸다가 다시 시도해주세요.'
    }
  } catch (err) {
    console.error('인증 확인 중 에러:', err)
    isCodeVerified.value = false
    verificationMessage.value = err.response?.data?.message || '인증 확인 중 오류가 발생했습니다.'
  } finally {
    isVerifying.value = false
  }
}

// 인증 코드 복사
const copyVerificationCode = async () => {
  try {
    await navigator.clipboard.writeText(verificationCode.value)
    copySuccess.value = true

    // 2초 후에 복사 성공 상태 초기화
    setTimeout(() => {
      copySuccess.value = false
    }, 2000)
  } catch (error) {
    console.error('인증 코드 복사 실패:', error)
  }
}

// Toast 표시 함수
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true

  // 3초 후 Toast 숨기기
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

// 회원가입 제출
const submitSignUp = async () => {
  if (!validateForm()) return

  try {
    isSubmitting.value = true
    error.value = ''

    // 회원가입 전 마지막으로 인증 상태 확인
    const verifyResponse = await authStore.verifyCode(formData.solvedAcId)
    if (!verifyResponse.data.verified) {
      error.value = '인증이 만료되었습니다. 처음부터 다시 진행해주세요.'
      currentStep.value = 1 // 인증 단계로 돌아가기
      return
    }

    // 회원가입 요청 데이터
    const signUpData = {
      solvedAcId: formData.solvedAcId,
      password: formData.password,
      name: formData.name,
    }

    const response = await authStore.signUp(signUpData)
    console.log('회원가입 응답:', response)

    // 회원가입 성공 Toast 표시
    showToastMessage('🎉 회원가입이 완료되었습니다!', 'success')

    // 1.5초 후 로그인 페이지로 이동
    setTimeout(() => {
      router.push({
        path: '/login',
        query: {
          message: '회원가입이 완료되었습니다. 로그인해주세요.',
          solvedAcId: formData.solvedAcId,
        },
      })
    }, 1500)
  } catch (err) {
    console.error('회원가입 에러:', err)
    if (err.response?.status === 400) {
      // 유효성 검사 실패
      const validationErrors = err.response.data
      if (validationErrors.solvedAcId) {
        errors.solvedAcId = validationErrors.solvedAcId
      }
      if (validationErrors.password) {
        errors.password = validationErrors.password
      }
      if (validationErrors.name) {
        errors.name = validationErrors.name
      }
      error.value = '입력값을 확인해주세요.'
    } else {
      error.value = err.response?.data?.message || '회원가입 중 오류가 발생했습니다.'
    }
  } finally {
    isSubmitting.value = false
  }
}

// 입력값 유효성 검사
const validateForm = () => {
  let isValid = true
  errors.name = ''
  errors.password = ''
  errors.passwordConfirm = ''

  if (!formData.name) {
    errors.name = '이름을 입력해주세요.'
    isValid = false
  } else if (formData.name.length > 30) {
    errors.name = '이름은 30자 이하로 입력해주세요.'
    isValid = false
  }

  if (!formData.password) {
    errors.password = '비밀번호를 입력해주세요.'
    isValid = false
  } else if (formData.password.length < 8) {
    errors.password = '비밀번호는 8자 이상이어야 합니다.'
    isValid = false
  }

  if (formData.password !== formData.passwordConfirm) {
    errors.passwordConfirm = '비밀번호가 일치하지 않습니다.'
    isValid = false
  }

  if (!isIdVerified.value) {
    errors.solvedAcId = 'solved.ac ID 인증이 필요합니다.'
    isValid = false
  }

  if (!isCodeVerified.value) {
    error.value = 'solved.ac 인증이 필요합니다.'
    isValid = false
  }

  return isValid
}

// 이전 단계로
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
    // 단계가 변경될 때 메시지 초기화
    verificationMessage.value = ''
    error.value = ''
  }
}

// 다음 단계로
const nextStep = () => {
  if (currentStep.value < steps.length - 1 && canProceed.value) {
    currentStep.value++
    // 단계가 변경될 때 메시지 초기화
    verificationMessage.value = ''
    error.value = ''
  }
}

onMounted(async () => {
  await authStore.initialize()
})
</script>

<style scoped>
.signup-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
}

.signup-container {
  max-width: 500px;
  width: 100%;
}

.signup-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.signup-header {
  text-align: center;
  margin-bottom: 2rem;
}

.signup-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.signup-header p {
  color: #666;
}

.signup-steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2rem;
  position: relative;
}

.signup-steps::before {
  content: '';
  position: absolute;
  top: 20px;
  left: 0;
  right: 0;
  height: 2px;
  background: #e1e5e9;
  z-index: 1;
}

.step {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  border: 2px solid #e1e5e9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #666;
}

.step.active .step-number {
  background: var(--samsung-blue);
  border-color: var(--samsung-blue);
  color: white;
}

.step.completed .step-number {
  background: var(--samsung-blue);
  border-color: var(--samsung-blue);
  color: white;
}

.step-title {
  font-size: 0.875rem;
  color: #666;
  text-align: center;
}

.step.active .step-title {
  color: var(--samsung-blue);
  font-weight: 600;
}

.signup-form {
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
}

.input-with-button {
  display: flex;
  gap: 0.5rem;
}

.form-input {
  flex: 1;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: var(--samsung-blue);
  box-shadow: 0 0 0 3px var(--samsung-blue-alpha);
}

.form-input.error {
  border-color: #dc3545;
}

.error-message {
  display: block;
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.success-message {
  display: block;
  color: #28a745;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.verify-button,
.code-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  background: var(--samsung-blue);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.verify-button:hover:not(:disabled),
.code-button:hover:not(:disabled) {
  background: var(--samsung-blue-dark);
}

.verify-button:disabled,
.code-button:disabled {
  background: #e1e5e9;
  cursor: not-allowed;
}

.verification-info {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.verification-info h3 {
  margin-bottom: 1rem;
  color: #333;
}

.verification-info p {
  margin-bottom: 0.5rem;
  color: #666;
}

.verification-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
}

.verification-code {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;
  width: 100%;
  margin: 1rem 0;
  border: 2px solid var(--samsung-blue);
}

.code-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.code-display code {
  font-family: 'Courier New', monospace;
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--samsung-blue);
  background: white;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  border: 2px solid var(--samsung-blue);
  letter-spacing: 2px;
  min-width: 200px;
}

.copy-button {
  background-color: var(--samsung-blue);
  color: white;
  border: none;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s;
}

.copy-button:hover {
  background-color: var(--samsung-blue-dark);
  transform: translateY(-1px);
}

.copy-button:active {
  transform: translateY(0);
}

.copy-button.success {
  background-color: #28a745;
}

.copy-button svg {
  width: 1rem;
  height: 1rem;
}

.code-instructions {
  text-align: left;
  margin-top: 1rem;
  padding: 1rem;
  background: rgba(var(--samsung-blue-rgb), 0.05);
  border-radius: 8px;
}

.code-instructions p {
  margin: 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.guide-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 0.5rem 0;
  border: 1px solid #ddd;
}

.link {
  color: var(--samsung-blue);
  text-decoration: none;
  font-weight: 500;
}

.link:hover {
  text-decoration: underline;
}

.input-help {
  display: block;
  color: #666;
  font-size: 0.8rem;
  margin-top: 0.25rem;
  font-style: italic;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  cursor: pointer;
}

.message-box {
  margin: 1rem 0;
  padding: 0.75rem;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.error-message-box {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.success-message-box {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.step-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.prev-button,
.next-button,
.submit-button {
  flex: 1;
  padding: 0.875rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.prev-button {
  background: #e1e5e9;
  color: #666;
}

.prev-button:hover {
  background: #d1d5d9;
}

.next-button,
.submit-button {
  background: var(--samsung-blue);
  color: white;
}

.next-button:hover:not(:disabled),
.submit-button:hover:not(:disabled) {
  background: var(--samsung-blue-dark);
}

.next-button:disabled,
.submit-button:disabled {
  background: #e1e5e9;
  cursor: not-allowed;
}

.signup-footer {
  text-align: center;
  color: #666;
}

.login-link {
  color: var(--samsung-blue);
  text-decoration: none;
  font-weight: 600;
  margin-left: 0.25rem;
}

.login-link:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .signup-view {
    padding: 1rem;
  }

  .signup-card {
    padding: 1.5rem;
  }

  .step-title {
    font-size: 0.75rem;
  }
}

/* Toast 스타일 */
.toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  z-index: 9999;
  min-width: 300px;
  max-width: 500px;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast.success {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.toast.error {
  background: linear-gradient(135deg, #dc3545 0%, #e55d71 100%);
  color: white;
}

.toast-content {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  gap: 0.75rem;
}

.toast-icon {
  font-size: 1.25rem;
  flex-shrink: 0;
}

.toast-message {
  font-weight: 500;
  font-size: 1rem;
  line-height: 1.4;
}

/* Toast 애니메이션 */
.toast-enter-active {
  transition: all 1s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.toast-leave-active {
  transition: all 0.6s ease-in;
}

.toast-enter-from {
  transform: translateX(100%) scale(0.8);
  opacity: 0;
}

.toast-leave-to {
  transform: translateX(100%) scale(0.8);
  opacity: 0;
}

/* 반응형 Toast */
@media (max-width: 768px) {
  .toast {
    top: 1rem;
    left: 1rem;
    right: 1rem;
    min-width: auto;
  }

  .toast-enter-from,
  .toast-leave-to {
    transform: translateY(-100%) scale(0.8);
  }
}

/* 페이드 애니메이션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
