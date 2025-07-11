<!-- src/views/SettingsView.vue -->
<template>
  <div class="settings-view">
    <div class="container">
      <div class="page-header">
        <h1>설정</h1>
        <p>설정 서비스 준비중 입니다(기능이 적용되지 않습니다.)</p>
      </div>

      <div class="settings-layout">
        <!-- 왼쪽 사이드바 -->
        <div class="settings-sidebar">
          <nav class="settings-nav">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              :class="['nav-item', { active: activeTab === tab.id }]"
              @click="activeTab = tab.id"
            >
              <font-awesome-icon :icon="tab.icon" />
              {{ tab.name }}
            </button>
          </nav>
        </div>

        <!-- 오른쪽 콘텐츠 -->
        <div class="settings-content">
          <!-- 프로필 설정 -->
          <div v-if="activeTab === 'profile'" class="settings-section">
            <h2>프로필 설정</h2>
            <form @submit.prevent="updateProfile" class="settings-form">
              <div class="form-group">
                <label for="name">이름</label>
                <input
                  id="name"
                  v-model="profileData.name"
                  type="text"
                  class="form-input"
                  placeholder="이름을 입력하세요"
                />
              </div>

              <div class="form-group">
                <label for="solvedAcId">Solved.ac ID</label>
                <input
                  id="solvedAcId"
                  v-model="profileData.solvedAcId"
                  type="text"
                  class="form-input"
                  disabled
                  readonly
                />
                <small class="form-help">Solved.ac ID는 변경할 수 없습니다.</small>
              </div>

              <div class="form-group">
                <label for="bio">소개</label>
                <textarea
                  id="bio"
                  v-model="profileData.bio"
                  class="form-textarea"
                  rows="3"
                  placeholder="자신을 소개해주세요"
                ></textarea>
              </div>

              <div class="form-actions">
                <button type="submit" class="btn btn-primary" :disabled="isUpdating">
                  <font-awesome-icon v-if="isUpdating" :icon="['fas', 'spinner']" spin />
                  {{ isUpdating ? '저장 중...' : '저장하기' }}
                </button>
              </div>
            </form>
          </div>

          <!-- 비밀번호 변경 -->
          <div v-if="activeTab === 'password'" class="settings-section">
            <h2>비밀번호 변경</h2>
            <form @submit.prevent="changePassword" class="settings-form">
              <div class="form-group">
                <label for="currentPassword">현재 비밀번호</label>
                <div class="password-input">
                  <input
                    id="currentPassword"
                    v-model="passwordData.currentPassword"
                    :type="showCurrentPassword ? 'text' : 'password'"
                    class="form-input"
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
                    id="newPassword"
                    v-model="passwordData.newPassword"
                    :type="showNewPassword ? 'text' : 'password'"
                    class="form-input"
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
                    id="confirmPassword"
                    v-model="passwordData.confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    class="form-input"
                    required
                  />
                  <button type="button" @click="showConfirmPassword = !showConfirmPassword">
                    <font-awesome-icon :icon="['fas', showConfirmPassword ? 'eye-slash' : 'eye']" />
                  </button>
                </div>
              </div>

              <div v-if="passwordError" class="error-message">
                {{ passwordError }}
              </div>

              <div class="form-actions">
                <button type="submit" class="btn btn-primary" :disabled="isChangingPassword">
                  <font-awesome-icon v-if="isChangingPassword" :icon="['fas', 'spinner']" spin />
                  {{ isChangingPassword ? '변경 중...' : '비밀번호 변경' }}
                </button>
              </div>
            </form>
          </div>

          <!-- 알림 설정 -->
          <div v-if="activeTab === 'notifications'" class="settings-section">
            <h2>알림 설정</h2>
            <div class="settings-form">
              <div class="form-group">
                <div class="toggle-setting">
                  <div class="setting-info">
                    <label>이메일 알림</label>
                    <small>중요한 업데이트를 이메일로 받아보세요</small>
                  </div>
                  <div class="toggle-switch">
                    <input
                      id="emailNotifications"
                      v-model="notificationSettings.email"
                      type="checkbox"
                    />
                    <label for="emailNotifications" class="toggle-label"></label>
                  </div>
                </div>
              </div>

              <div class="form-group">
                <div class="toggle-setting">
                  <div class="setting-info">
                    <label>랭킹 업데이트 알림</label>
                    <small>랭킹이 변동될 때 알림을 받습니다</small>
                  </div>
                  <div class="toggle-switch">
                    <input
                      id="rankingNotifications"
                      v-model="notificationSettings.ranking"
                      type="checkbox"
                    />
                    <label for="rankingNotifications" class="toggle-label"></label>
                  </div>
                </div>
              </div>

              <div class="form-group">
                <div class="toggle-setting">
                  <div class="setting-info">
                    <label>팀 활동 알림</label>
                    <small>소속 팀의 새로운 활동 알림을 받습니다</small>
                  </div>
                  <div class="toggle-switch">
                    <input
                      id="teamNotifications"
                      v-model="notificationSettings.team"
                      type="checkbox"
                    />
                    <label for="teamNotifications" class="toggle-label"></label>
                  </div>
                </div>
              </div>

              <div class="form-actions">
                <button
                  @click="saveNotificationSettings"
                  class="btn btn-primary"
                  :disabled="isSavingNotifications"
                >
                  <font-awesome-icon v-if="isSavingNotifications" :icon="['fas', 'spinner']" spin />
                  {{ isSavingNotifications ? '저장 중...' : '설정 저장' }}
                </button>
              </div>
            </div>
          </div>

          <!-- 계정 관리 -->
          <div v-if="activeTab === 'account'" class="settings-section">
            <h2>계정 관리</h2>
            <div class="settings-form">
              <div class="form-group">
                <h3>데이터 동기화</h3>
                <p>Solved.ac에서 최신 데이터를 가져와 프로필을 업데이트합니다.</p>
                <button @click="syncProfile" class="btn btn-secondary" :disabled="isSyncing">
                  <font-awesome-icon v-if="isSyncing" :icon="['fas', 'spinner']" spin />
                  {{ isSyncing ? '동기화 중...' : '프로필 동기화' }}
                </button>
              </div>

              <div class="form-group danger-zone">
                <h3>위험 구역</h3>
                <p>아래 작업들은 되돌릴 수 없습니다. 신중하게 진행해주세요.</p>

                <div class="danger-actions">
                  <button @click="showLogoutConfirm = true" class="btn btn-outline-danger">
                    모든 기기에서 로그아웃
                  </button>

                  <button @click="showDeleteConfirm = true" class="btn btn-danger">
                    계정 삭제
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 성공 메시지 토스트 -->
    <div v-if="showSuccessToast" class="toast toast-success">
      <font-awesome-icon :icon="['fas', 'check-circle']" />
      {{ successMessage }}
    </div>

    <!-- 에러 메시지 토스트 -->
    <div v-if="showErrorToast" class="toast toast-error">
      <font-awesome-icon :icon="['fas', 'times-circle']" />
      {{ errorMessage }}
    </div>

    <!-- 로그아웃 확인 모달 -->
    <div v-if="showLogoutConfirm" class="modal-overlay" @click="showLogoutConfirm = false">
      <div class="modal-content" @click.stop>
        <h3>모든 기기에서 로그아웃</h3>
        <p>모든 기기에서 로그아웃하시겠습니까? 다시 로그인해야 합니다.</p>
        <div class="modal-actions">
          <button @click="showLogoutConfirm = false" class="btn btn-secondary">취소</button>
          <button @click="handleLogoutAll" class="btn btn-danger">로그아웃</button>
        </div>
      </div>
    </div>

    <!-- 계정 삭제 확인 모달 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="showDeleteConfirm = false">
      <div class="modal-content" @click.stop>
        <h3>계정 삭제</h3>
        <p>정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>
        <div class="modal-actions">
          <button @click="showDeleteConfirm = false" class="btn btn-secondary">취소</button>
          <button @click="handleDeleteAccount" class="btn btn-danger">계정 삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { memberApi } from '@/api/memberApi'
import { solvedProblemApi } from '@/api/solvedProblemApi'

const router = useRouter()
const authStore = useAuthStore()

// 탭 정의
const tabs = [
  { id: 'profile', name: '프로필', icon: ['fas', 'user'] },
  { id: 'password', name: '비밀번호', icon: ['fas', 'lock'] },
  { id: 'notifications', name: '알림', icon: ['fas', 'bell'] },
  { id: 'account', name: '계정', icon: ['fas', 'cog'] },
]

// 현재 활성 탭
const activeTab = ref('profile')

// 프로필 데이터
const profileData = reactive({
  name: '',
  solvedAcId: '',
  bio: '',
})

// 비밀번호 변경 데이터
const passwordData = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 알림 설정
const notificationSettings = reactive({
  email: true,
  ranking: true,
  team: false,
})

// 상태 관리
const isUpdating = ref(false)
const isChangingPassword = ref(false)
const isSavingNotifications = ref(false)
const isSyncing = ref(false)
const passwordError = ref('')

// 비밀번호 표시 토글
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

// 모달 상태
const showLogoutConfirm = ref(false)
const showDeleteConfirm = ref(false)

// 토스트 메시지
const showSuccessToast = ref(false)
const showErrorToast = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

// 토스트 표시 함수
const showToast = (message, isError = false) => {
  if (isError) {
    errorMessage.value = message
    showErrorToast.value = true
    setTimeout(() => {
      showErrorToast.value = false
    }, 3000)
  } else {
    successMessage.value = message
    showSuccessToast.value = true
    setTimeout(() => {
      showSuccessToast.value = false
    }, 3000)
  }
}

// 프로필 업데이트
const updateProfile = async () => {
  isUpdating.value = true
  try {
    // API 호출 비활성화 - 임시 처리
    // await memberApi.updateProfile({
    //   name: profileData.name,
    //   bio: profileData.bio
    // })
    await new Promise((resolve) => setTimeout(resolve, 1000)) // 로딩 시뮬레이션
    showToast('프로필이 성공적으로 업데이트되었습니다.')
  } catch (error) {
    showToast('프로필 업데이트에 실패했습니다.', true)
    console.log(error)
  } finally {
    isUpdating.value = false
  }
}

// 비밀번호 변경
const changePassword = async () => {
  passwordError.value = ''

  if (passwordData.newPassword !== passwordData.confirmPassword) {
    passwordError.value = '새 비밀번호가 일치하지 않습니다.'
    return
  }

  if (passwordData.newPassword.length < 8) {
    passwordError.value = '비밀번호는 8자 이상이어야 합니다.'
    return
  }

  isChangingPassword.value = true
  try {
    // API 호출 비활성화 - 임시 처리
    // await memberApi.changePassword({
    //   currentPassword: passwordData.currentPassword,
    //   newPassword: passwordData.newPassword
    // })
    await new Promise((resolve) => setTimeout(resolve, 1000)) // 로딩 시뮬레이션

    // 폼 초기화
    passwordData.currentPassword = ''
    passwordData.newPassword = ''
    passwordData.confirmPassword = ''

    showToast('비밀번호가 성공적으로 변경되었습니다.')
  } catch (error) {
    passwordError.value = error.response?.data?.message || '비밀번호 변경에 실패했습니다.'
  } finally {
    isChangingPassword.value = false
  }
}

// 알림 설정 저장
const saveNotificationSettings = async () => {
  isSavingNotifications.value = true
  try {
    // API 호출 (실제 구현 시)
    // await memberApi.updateNotificationSettings(notificationSettings)

    // 임시로 성공 처리
    await new Promise((resolve) => setTimeout(resolve, 1000))
    showToast('알림 설정이 저장되었습니다.')
  } catch (error) {
    console.log(error)
    showToast('알림 설정 저장에 실패했습니다.', true)
  } finally {
    isSavingNotifications.value = false
  }
}

// 프로필 동기화
const syncProfile = async () => {
  if (!authStore.user?.solvedAcId) {
    showToast('사용자 정보를 찾을 수 없습니다.', true)
    return
  }

  isSyncing.value = true
  try {
    // API 호출 비활성화 - 임시 처리
    // await solvedProblemApi.syncSolvedProblems(authStore.user.solvedAcId)
    await new Promise((resolve) => setTimeout(resolve, 2000)) // 로딩 시뮬레이션
    showToast('프로필이 성공적으로 동기화되었습니다.')
  } catch (error) {
    console.log(error)
    showToast('프로필 동기화에 실패했습니다.', true)
  } finally {
    isSyncing.value = false
  }
}

// 모든 기기에서 로그아웃
const handleLogoutAll = async () => {
  try {
    // API 호출 비활성화 - 임시 처리
    // await authStore.logout()
    await new Promise((resolve) => setTimeout(resolve, 1000)) // 로딩 시뮬레이션
    showLogoutConfirm.value = false
    showToast('모든 기기에서 로그아웃되었습니다.')
    // router.push('/')
  } catch (error) {
    console.log(error)
    showToast('로그아웃에 실패했습니다.', true)
  }
}

// 계정 삭제
const handleDeleteAccount = async () => {
  try {
    // API 호출 비활성화 - 임시 처리
    // await memberApi.deleteMember()
    // await authStore.logout()
    await new Promise((resolve) => setTimeout(resolve, 1000)) // 로딩 시뮬레이션
    showDeleteConfirm.value = false
    showToast('계정이 삭제되었습니다.')
    // router.push('/')
  } catch (error) {
    console.log(error)
    showToast('계정 삭제에 실패했습니다.', true)
  }
}

// 컴포넌트 마운트 시 사용자 정보 로드
onMounted(async () => {
  // API 호출 비활성화 - 임시 처리
  // 더미 데이터로 초기화
  if (authStore.user) {
    profileData.name = authStore.user.name || '김싸피'
    profileData.solvedAcId = authStore.user.solvedAcId || 'ssafy_user'
    profileData.bio = authStore.user.bio || '알고리즘을 좋아하는 개발자입니다.'
  } else {
    // 로그인하지 않은 경우 기본값 설정
    profileData.name = '김싸피'
    profileData.solvedAcId = 'ssafy_user'
    profileData.bio = '알고리즘을 좋아하는 개발자입니다.'
  }
})
</script>

<style scoped>
.settings-view {
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.page-header p {
  font-size: 1.1rem;
  color: #666;
}

.settings-layout {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
  align-items: start;
}

/* 사이드바 */
.settings-sidebar {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 2rem;
}

.settings-nav {
  padding: 1rem 0;
}

.nav-item {
  width: 100%;
  text-align: left;
  padding: 0.75rem 1.5rem;
  background: none;
  border: none;
  color: #666;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.nav-item:hover {
  background-color: #f8f9fa;
  color: var(--samsung-blue);
}

.nav-item.active {
  background-color: var(--samsung-blue-alpha);
  color: var(--samsung-blue);
  font-weight: 600;
  border-right: 3px solid var(--samsung-blue);
}

/* 콘텐츠 */
.settings-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.settings-section h2 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.5rem;
}

.settings-form {
  max-width: 500px;
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

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--samsung-blue);
  box-shadow: 0 0 0 3px var(--samsung-blue-alpha);
}

.form-input:disabled {
  background-color: #f8f9fa;
  color: #666;
}

.form-help {
  display: block;
  margin-top: 0.25rem;
  color: #666;
  font-size: 0.875rem;
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
  font-size: 0.875rem;
  margin-top: 0.5rem;
  padding: 0.5rem;
  background-color: #fff8f8;
  border-radius: 4px;
  border: 1px solid #ffebee;
}

/* 토글 스위치 */
.toggle-setting {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

.setting-info label {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.25rem;
}

.setting-info small {
  color: #666;
  font-size: 0.875rem;
}

.toggle-switch {
  position: relative;
}

.toggle-switch input[type='checkbox'] {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-label {
  display: block;
  width: 50px;
  height: 28px;
  background-color: #ccc;
  border-radius: 50px;
  position: relative;
  cursor: pointer;
  transition: background-color 0.2s;
}

.toggle-label::after {
  content: '';
  position: absolute;
  width: 24px;
  height: 24px;
  background-color: white;
  border-radius: 50%;
  top: 2px;
  left: 2px;
  transition: transform 0.2s;
}

input[type='checkbox']:checked + .toggle-label {
  background-color: var(--samsung-blue);
}

input[type='checkbox']:checked + .toggle-label::after {
  transform: translateX(22px);
}

/* 위험 구역 */
.danger-zone {
  border: 2px solid #dc3545;
  border-radius: 8px;
  padding: 1.5rem;
  background-color: #fff8f8;
}

.danger-zone h3 {
  color: #dc3545;
  margin-bottom: 0.5rem;
}

.danger-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

/* 버튼 스타일 */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  text-decoration: none;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: var(--samsung-blue);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--samsung-blue-dark);
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.btn-outline-danger {
  background-color: transparent;
  color: #dc3545;
  border: 1px solid #dc3545;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
}

.form-actions {
  margin-top: 2rem;
}

/* 토스트 메시지 */
.toast {
  position: fixed;
  top: 2rem;
  right: 2rem;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  z-index: 1000;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  animation: slideIn 0.3s ease;
}

.toast-success {
  background-color: #28a745;
}

.toast-error {
  background-color: #dc3545;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 모달 */
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
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
  margin-bottom: 1rem;
  color: #333;
}

.modal-content p {
  margin-bottom: 1.5rem;
  color: #666;
  line-height: 1.5;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .container {
    padding: 0 1rem;
  }

  .settings-layout {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .settings-sidebar {
    position: static;
  }

  .settings-nav {
    display: flex;
    overflow-x: auto;
    padding: 0.5rem;
  }

  .nav-item {
    white-space: nowrap;
    border-right: none;
    border-bottom: 3px solid transparent;
  }

  .nav-item.active {
    border-right: none;
    border-bottom: 3px solid var(--samsung-blue);
  }

  .danger-actions {
    flex-direction: column;
  }

  .toast {
    top: 1rem;
    right: 1rem;
    left: 1rem;
  }
}
</style>
