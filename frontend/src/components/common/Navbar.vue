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
      <template v-if="isLoggedIn">
        <div class="profile-container" @click="toggleDropdown">
          <img
            :src="user?.profileImage || '/profiles/default.png'"
            alt="프로필"
            class="profile-image"
          />
          <div class="dropdown" v-show="showDropdown">
            <router-link to="/profile">프로필</router-link>
            <router-link to="/settings">설정</router-link>
            <a href="#" @click.prevent="handleLogout">로그아웃</a>
          </div>
        </div>
      </template>
      <template v-else>
        <router-link to="/login" class="login-button">로그인</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { checkAuthStatus, logout } from '@/utils/datatransferutil'

const router = useRouter()
const route = useRoute()
const isLoggedIn = ref(false)
const user = ref(null)
const showDropdown = ref(false)

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const handleLogout = async () => {
  try {
    await logout()
    isLoggedIn.value = false
    user.value = null
    showDropdown.value = false
    router.push('/')
  } catch (error) {
    console.error('로그아웃 중 오류 발생:', error)
  }
}

// 인증 상태 확인 함수
const checkLogin = async () => {
  try {
    const userData = await checkAuthStatus()
    if (userData) {
      isLoggedIn.value = true
      user.value = userData
    } else {
      isLoggedIn.value = false
      user.value = null
    }
  } catch (error) {
    console.error('인증 상태 확인 중 오류 발생:', error)
    isLoggedIn.value = false
    user.value = null
  }
}

// 클릭 이벤트를 감지하여 드롭다운 외부 클릭 시 드롭다운 닫기
const handleClickOutside = (event) => {
  const profileContainer = document.querySelector('.profile-container')
  if (profileContainer && !profileContainer.contains(event.target)) {
    showDropdown.value = false
  }
}

// 라우트 변경 시마다 인증 상태 확인
watch(route, () => {
  checkLogin()
})

// localStorage 변화 감지 (다른 탭에서 로그인/로그아웃 시)
const handleStorageChange = (event) => {
  if (event.key === 'user') {
    checkLogin()
  }
}

// 컴포넌트 마운트 시 실행
onMounted(() => {
  checkLogin()
  document.addEventListener('click', handleClickOutside)
  window.addEventListener('storage', handleStorageChange)
})

// 컴포넌트 언마운트 시 이벤트 리스너 정리
import { onUnmounted } from 'vue'
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('storage', handleStorageChange)
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
</style>
