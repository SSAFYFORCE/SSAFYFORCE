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
      <div class="search-container" :class="{ 'search-focused': isSearchFocused }">
        <font-awesome-icon :icon="['fas', 'search']" />
        <input
          type="text"
          placeholder="유저나 팀 검색..."
          class="search-input"
          v-model="searchQuery"
          @focus="handleSearchFocus"
          @blur="handleSearchBlur"
          @input="handleSearchInput"
          @keydown.enter="handleSearchEnter"
          ref="searchInput"
        />

        <!-- 검색 결과 드롭다운 -->
        <div
          v-if="
            showSearchResults && (searchResults.users.length > 0 || searchResults.teams.length > 0)
          "
          class="search-dropdown"
        >
          <!-- 유저 검색 결과 -->
          <div v-if="searchResults.users.length > 0" class="search-section">
            <div class="search-section-title">유저</div>
            <div
              v-for="user in searchResults.users.slice(0, 3)"
              :key="user.solvedAcId"
              class="search-item user-item"
              @click="goToProfile(user.solvedAcId)"
            >
              <img
                :src="user.profileImage || defaultProfileImage"
                :alt="user.name"
                class="search-item-image"
              />
              <div class="search-item-info">
                <div class="search-item-name">{{ user.name }}</div>
                <div class="search-item-meta">@{{ user.solvedAcId }}</div>
              </div>
            </div>
          </div>

          <!-- 팀 검색 결과 -->
          <div v-if="searchResults.teams.length > 0" class="search-section">
            <div class="search-section-title">팀</div>
            <div
              v-for="team in searchResults.teams.slice(0, 3)"
              :key="team.id"
              class="search-item team-item"
              @click="goToTeam(team.id)"
            >
              <img
                :src="team.profileImage || defaultProfileImage"
                :alt="team.name"
                class="search-item-image"
              />
              <div class="search-item-info">
                <div class="search-item-name">{{ team.name }}</div>
                <div class="search-item-meta">멤버 {{ team.memberCount }}명</div>
              </div>
            </div>
          </div>

          <!-- 더 보기 안내 -->
          <div
            v-if="
              searchQuery.length > 0 &&
              (searchResults.users.length > 3 || searchResults.teams.length > 3)
            "
            class="search-more"
          >
            <div class="search-more-info">
              더 많은 결과가 있습니다. 구체적인 검색어를 입력해보세요.
            </div>
          </div>
        </div>

        <!-- 검색 결과가 없을 때 -->
        <div
          v-if="
            showSearchResults &&
            searchQuery.length > 0 &&
            searchResults.users.length === 0 &&
            searchResults.teams.length === 0
          "
          class="search-dropdown no-results"
        >
          <div class="no-results-message">
            <font-awesome-icon :icon="['fas', 'search']" />
            "{{ searchQuery }}"에 대한 검색 결과가 없습니다.
          </div>
        </div>
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
            <router-link :to="`/profile/${authStore.user?.solvedAcId}`" @click="closeDropdown">
              프로필
            </router-link>
            <router-link to="/settings" @click="closeDropdown">설정</router-link>
            <a href="#" @click.prevent="handlePasswordReset">비밀번호 재설정</a>
            <a href="#" @click.prevent="handleDeleteAccount" class="danger">회원 탈퇴</a>
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
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { memberApi } from '@/api/memberApi'
import defaultProfileImage from '@/mockdata/default_profile.png'
import PasswordResetModal from './PasswordResetModal.vue'

const router = useRouter()
const authStore = useAuthStore()
const showPasswordResetModal = ref(false)
const showDropdown = ref(false)
// 검색 관련 상태
const searchQuery = ref('')
const isSearchFocused = ref(false)
const showSearchResults = ref(false)
const searchInput = ref(null)
const searchResults = reactive({
  users: [],
  teams: [],
})

// 임시 검색 데이터 (실제로는 API에서 가져올 데이터)
const mockUsers = [
  {
    solvedAcId: 'shiftpsh',
    name: '박성현',
    profileImage: '/src/mockdata/shiftpsh_profile.png',
    tier: 'Ruby II',
  },
  {
    solvedAcId: 'jeongyeon',
    name: '김정연',
    profileImage: '/src/mockdata/default_profile.png',
    tier: 'Platinum I',
  },
  {
    solvedAcId: 'seungho',
    name: '김승호',
    profileImage: '/src/mockdata/default_profile.png',
    tier: 'Gold III',
  },
  {
    solvedAcId: 'kwonmin',
    name: '이권민',
    profileImage: '/src/mockdata/default_profile.png',
    tier: 'Silver I',
  },
  {
    solvedAcId: 'eunsung',
    name: '이은성',
    profileImage: '/src/mockdata/default_profile.png',
    tier: 'Gold V',
  },
]

const mockTeams = [
  {
    id: 1,
    name: 'SSAFY Force',
    description: '삼성 청년 SW 아카데미 알고리즘 스터디',
    memberCount: 5,
    profileImage: null,
  },
  {
    id: 2,
    name: 'Algorithm Masters',
    description: '알고리즘 마스터들의 모임',
    memberCount: 8,
    profileImage: null,
  },
  {
    id: 3,
    name: 'Coding Warriors',
    description: '코딩 전사들',
    memberCount: 12,
    profileImage: null,
  },
  {
    id: 4,
    name: 'Problem Solvers',
    description: '문제 해결사들',
    memberCount: 6,
    profileImage: null,
  },
]

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

// 검색 관련 함수들
const handleSearchFocus = () => {
  isSearchFocused.value = true
  if (searchQuery.value.length > 0) {
    showSearchResults.value = true
  }
}

const handleSearchBlur = () => {
  // 검색 결과 클릭을 위해 약간의 지연시간 추가
  setTimeout(() => {
    isSearchFocused.value = false
    showSearchResults.value = false
  }, 200)
}

const handleSearchInput = () => {
  if (searchQuery.value.length === 0) {
    showSearchResults.value = false
    searchResults.users = []
    searchResults.teams = []
    return
  }

  // 임시 검색 로직 (실제로는 API 호출)
  performSearch(searchQuery.value)
  showSearchResults.value = true
}

const handleSearchEnter = () => {
  // Enter 키 입력 시 첫 번째 검색 결과로 이동
  if (searchResults.users.length > 0) {
    goToProfile(searchResults.users[0].solvedAcId)
  } else if (searchResults.teams.length > 0) {
    goToTeam(searchResults.teams[0].id)
  }
}

const performSearch = (query) => {
  const lowerQuery = query.toLowerCase()

  // 유저 검색
  searchResults.users = mockUsers.filter(
    (user) =>
      user.name.toLowerCase().includes(lowerQuery) ||
      user.solvedAcId.toLowerCase().includes(lowerQuery),
  )

  // 팀 검색
  searchResults.teams = mockTeams.filter(
    (team) =>
      team.name.toLowerCase().includes(lowerQuery) ||
      team.description.toLowerCase().includes(lowerQuery),
  )
}

const goToProfile = (solvedAcId) => {
  router.push(`/profile/${solvedAcId}`)
  clearSearch()
}

const goToTeam = (teamId) => {
  router.push(`/teams/${teamId}`)
  clearSearch()
}

// 검색 결과 페이지는 제거 - 미리보기만 제공

const clearSearch = () => {
  searchQuery.value = ''
  showSearchResults.value = false
  searchResults.users = []
  searchResults.teams = []
  searchInput.value?.blur()
}

// 클릭 이벤트를 감지하여 드롭다운 외부 클릭 시 드롭다운 닫기
const handleClickOutside = (event) => {
  const profileContainer = document.querySelector('.profile-container')
  if (profileContainer && !profileContainer.contains(event.target)) {
    showDropdown.value = false
  }

  // 검색 드롭다운 외부 클릭 시 닫기
  const searchContainer = document.querySelector('.search-container')
  if (searchContainer && !searchContainer.contains(event.target)) {
    showSearchResults.value = false
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
  position: relative;
  z-index: 1000;
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
  position: relative;
  transition: all 0.2s ease;
  min-width: 200px;
}

.search-container.search-focused {
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--samsung-blue);
}

.search-input {
  border: none;
  background: transparent;
  margin-left: 0.5rem;
  outline: none;
  width: 150px;
  font-size: 0.9rem;
}

.search-input::placeholder {
  color: #999;
}

/* 검색 드롭다운 */
.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  margin-top: 0.5rem;
  max-height: 400px;
  overflow-y: auto;
  z-index: 1001;
}

.search-section {
  border-bottom: 1px solid #f0f0f0;
}

.search-section:last-child {
  border-bottom: none;
}

.search-section-title {
  padding: 0.75rem 1rem 0.5rem;
  font-size: 0.8rem;
  font-weight: 600;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-item:hover {
  background-color: #f8f9fa;
}

.search-item-image {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.search-item-info {
  flex: 1;
  min-width: 0;
}

.search-item-name {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.search-item-meta {
  font-size: 0.8rem;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.search-more {
  padding: 0.75rem 1rem;
  border-top: 1px solid #f0f0f0;
}

.search-more-info {
  text-align: center;
  padding: 0.5rem;
  color: #666;
  font-size: 0.85rem;
  font-style: italic;
}

.no-results {
  padding: 2rem 1rem;
}

.no-results-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.9rem;
  text-align: center;
}

.no-results-message svg {
  font-size: 1.5rem;
  opacity: 0.5;
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

/* 반응형 디자인 */
@media (max-width: 768px) {
  .search-container {
    margin-left: 1rem;
    min-width: 120px;
  }

  .search-input {
    width: 100px;
  }

  .nav-links {
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .search-container {
    display: none; /* 모바일에서는 검색창 숨김 */
  }
}
</style>
