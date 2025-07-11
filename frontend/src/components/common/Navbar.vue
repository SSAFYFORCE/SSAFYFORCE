<!-- src/components/common/Navbar.vue -->
<template>
  <nav class="navbar">
    <div class="navbar-left">
      <router-link to="/" class="logo">
        <img src="@/assets/logo.svg" alt="로고" />
      </router-link>
      <div class="nav-links">
        <router-link to="/problems">문제</router-link>
        <router-link to="/teams">팀</router-link>
        <router-link to="/ranking">랭킹</router-link>
      </div>
      <div class="search-container">
        <font-awesome-icon :icon="['fas', 'search']" />
        <input type="text" placeholder="검색" class="search-input" />
      </div>
    </div>
    <div class="navbar-right">
      <template v-if="authStore.isLoggedIn">
        <div class="profile-container" @click="toggleDropdown">
          <img
            :src="authStore.user?.profileImage || defaultProfileImage"
            alt="프로필"
            class="profile-image"
          />
          <div class="dropdown" v-show="showDropdown">
            <router-link to="/profile">프로필</router-link>
            <router-link to="/settings">설정</router-link>
            <a href="#" @click.prevent="handlePasswordReset">비밀번호 재설정</a>
            <a href="#" @click.prevent="handleDeleteAccount" class="danger">회원 탈퇴</a>
            <!-- 프로필 링크를 사용자의 solvedAcId로 변경 -->
            <router-link :to="`/profile/${authStore.user?.solvedAcId}`" @click="closeDropdown">
              프로필
            </router-link>
            <router-link to="/settings" @click="closeDropdown">설정</router-link>
            <a href="#" @click.prevent="handleLogout">로그아웃</a>
          </div>
        </div>
        <a href="#" @click.prevent="handleLogout" class="login-button">로그아웃</a>
      </template>
      <template v-else>
        <router-link to="/login" class="login-button">로그인</router-link>
      </template>
    </div>
  </nav>
  <PasswordResetModal :show="showPasswordResetModal" @close="showPasswordResetModal = false" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { memberApi } from '@/api/memberApi'
import defaultProfileImage from '@/mockdata/default_profile.png'
import PasswordResetModal from './PasswordResetModal.vue'

const router = useRouter()
const authStore = useAuthStore()
const showPasswordResetModal = ref(false)

const showDropdown = ref(false)

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const closeDropdown = () => {
  showDropdown.value = false
}

const handleLogout = async () => {
  try {
    await authStore.logout()
    showDropdown.value = false
    router.push('/')
  } catch (error) {
    console.error('로그아웃 중 오류 발생:', error)
  }
}

const handlePasswordReset = () => {
  showDropdown.value = false
  showPasswordResetModal.value = true
}

const handleDeleteAccount = async () => {
  if (confirm('정말로 회원 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
    try {
      await memberApi.deleteMember()
      await authStore.logout()
      router.push('/')
    } catch (error) {
      console.error('회원 탈퇴 중 오류 발생:', error)
    }
  }
}

// 클릭 이벤트를 감지하여 드롭다운 외부 클릭 시 드롭다운 닫기
const handleClickOutside = (event) => {
  const profileContainer = document.querySelector('.profile-container')
  if (profileContainer && !profileContainer.contains(event.target)) {
    showDropdown.value = false
  }
}

// 컴포넌트 마운트 시 실행
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 컴포넌트 언마운트 시 이벤트 리스너 정리
import { onUnmounted } from 'vue'
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 64px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo img {
  height: 32px;
  margin-right: 2rem;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
}

.nav-links a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
}

.nav-links a:hover {
  color: var(--samsung-blue);
}

.nav-links a.router-link-active {
  color: var(--samsung-blue);
}

.nav-links a.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--samsung-blue);
}

.search-container {
  display: flex;
  align-items: center;
  margin-left: 2rem;
  padding: 0.5rem 1rem;
  background-color: #f5f5f5;
  border-radius: 20px;
}

.search-input {
  border: none;
  background: transparent;
  margin-left: 0.5rem;
  outline: none;
  width: 150px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.login-button {
  padding: 0.5rem 1.5rem;
  background-color: var(--samsung-blue);
  color: white;
  border-radius: 20px;
  text-decoration: none;
  font-weight: 500;
}

.login-button:hover {
  background-color: var(--samsung-blue-dark);
}

.profile-container {
  position: relative;
  cursor: pointer;
}

.profile-image {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.dropdown {
  position: absolute;
  top: 45px;
  right: 0;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  width: 150px;
  z-index: 10;
}

.dropdown a {
  display: block;
  padding: 0.75rem 1rem;
  text-decoration: none;
  color: #333;
}

.dropdown a:hover {
  background-color: #f5f5f5;
}

.dropdown a.danger {
  color: #dc3545;
}

.dropdown a.danger:hover {
  background-color: #ffebee;
}
</style>
