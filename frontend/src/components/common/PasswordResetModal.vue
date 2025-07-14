<!-- src/components/common/PasswordResetModal.vue -->
<template>
  <div v-if="show" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <h2>비밀번호 재설정</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="currentPassword">현재 비밀번호</label>
          <div class="password-input">
            <input
              :type="showCurrentPassword ? 'text' : 'password'"
              id="currentPassword"
              v-model="currentPassword"
              required
            />
            <button type="button" @click="showCurrentPassword = !showCurrentPassword">
              <font-awesome-icon :icon="['fas', showCurrentPassword ? 'eye-slash' : 'eye']" />
            </button>
          </div>
        </div>
        <div class="form-group">
          <label for="newPassword">새 비밀번호</label>
          <div class="password-input">
            <input
              :type="showNewPassword ? 'text' : 'password'"
              id="newPassword"
              v-model="newPassword"
              required
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
            <span v-else>변경하기</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { memberApi } from '@/api/memberApi'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  show: Boolean,
})

const emit = defineEmits(['close'])
const authStore = useAuthStore()

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const isLoading = ref(false)
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const closeModal = () => {
  emit('close')
  resetForm()
}

const resetForm = () => {
  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  error.value = ''
  showCurrentPassword.value = false
  showNewPassword.value = false
  showConfirmPassword.value = false
}

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
      solvedAcId: authStore.user.solvedAcId,
      oldPassword: currentPassword.value,
      newPassword: newPassword.value
    })
    alert('비밀번호가 성공적으로 변경되었습니다.')
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

.password-input {
  position: relative;
  display: flex;
  align-items: center;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
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
</style> 