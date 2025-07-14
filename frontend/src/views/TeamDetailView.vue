<!-- src/views/TeamDetailView.vue -->
<template>
  <div class="team-detail-view">
    <div class="container">
      <!-- 로딩 상태 -->
      <div v-if="loading" class="loading">
        <font-awesome-icon :icon="['fas', 'spinner']" spin />
        <span>팀 정보를 불러오는 중...</span>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="error" class="error-message">
        <p>{{ error }}</p>
        <button class="btn btn-primary" @click="loadTeamDetail">다시 시도</button>
      </div>

      <!-- 팀 상세 정보 -->
      <div v-else-if="team" class="team-detail-content">
        <!-- 상단 팀 정보 카드 -->
        <div class="team-header-card">
          <div class="team-avatar">
            <img
              :src="team.profileImage || defaultProfileImage"
              :alt="team.name"
              class="team-image"
            />
          </div>

          <div class="team-main-info">
            <h1 class="team-name">{{ team.name }}</h1>
            <p class="team-description">{{ team.description || '팀 설명이 없습니다.' }}</p>

            <div class="team-stats">
              <div class="stat-item">
                <span class="stat-number">{{ team.memberCount }}</span>
                <span class="stat-label">멤버</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ formatDate(team.createdAt) }}</span>
                <span class="stat-label">생성일</span>
              </div>
            </div>

            <!-- 팀 가입 버튼 -->
            <div class="team-actions">
              <button
                v-if="!isJoined && isLoggedIn"
                @click="handleJoinTeam"
                :disabled="joiningTeam"
                class="btn btn-primary"
              >
                <template v-if="joiningTeam">
                  <font-awesome-icon :icon="['fas', 'spinner']" spin />
                  가입 중...
                </template>
                <template v-else>
                  <font-awesome-icon :icon="['fas', 'plus']" />
                  팀 가입하기
                </template>
              </button>

              <span v-else-if="isJoined" class="joined-badge">
                <font-awesome-icon :icon="['fas', 'check-circle']" />
                가입된 팀
              </span>

              <router-link v-else-if="!isLoggedIn" to="/login" class="btn btn-primary">
                <font-awesome-icon :icon="['fas', 'user']" />
                로그인 후 가입
              </router-link>
            </div>
          </div>
        </div>

        <!-- 팀 멤버 목록 -->
        <div class="team-members-section">
          <h2 class="section-title">
            <font-awesome-icon :icon="['fas', 'users']" />
            팀 멤버 ({{ team.memberCount }}명)
          </h2>

          <div v-if="team.teamMembers.length === 0" class="no-members">
            아직 가입된 멤버가 없습니다.
          </div>

          <div v-else class="members-grid">
            <div
              v-for="member in team.teamMembers"
              :key="member.id"
              class="member-card"
              @click="goToProfile(member.nickname)"
            >
              <div class="member-avatar">
                <img
                  :src="member.profileImage || defaultProfileImage"
                  :alt="member.name"
                  class="member-image"
                />
              </div>

              <div class="member-info">
                <h3 class="member-name">{{ member.name }}</h3>
                <p class="member-nickname">@{{ member.nickname }}</p>
              </div>

              <div class="member-stats">
                <!-- 여기에 멤버별 통계 정보가 들어갈 수 있습니다 -->
                <span class="member-status">활성</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 팀 활동 통계 (선택사항) -->
        <div class="team-activity-section">
          <h2 class="section-title">
            <font-awesome-icon :icon="['fas', 'chart-bar']" />
            팀 활동
          </h2>

          <div class="activity-cards">
            <div class="activity-card">
              <div class="activity-icon">
                <font-awesome-icon :icon="['fas', 'code']" />
              </div>
              <div class="activity-info">
                <span class="activity-number">-</span>
                <span class="activity-label">해결한 문제</span>
              </div>
            </div>

            <div class="activity-card">
              <div class="activity-icon">
                <font-awesome-icon :icon="['fas', 'fire']" />
              </div>
              <div class="activity-info">
                <span class="activity-number">-</span>
                <span class="activity-label">연속 활동일</span>
              </div>
            </div>

            <div class="activity-card">
              <div class="activity-icon">
                <font-awesome-icon :icon="['fas', 'trophy']" />
              </div>
              <div class="activity-info">
                <span class="activity-number">-</span>
                <span class="activity-label">팀 랭킹</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { teamApi } from '@/api/teamApi'
import defaultProfileImage from '@/mockdata/default_profile.png'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 라우트에서 teamId 가져오기
const teamId = computed(() => route.params.teamId)

// 반응성 데이터
const team = ref(null)
const loading = ref(true)
const error = ref('')
const joiningTeam = ref(false)

// 계산된 속성
const isLoggedIn = computed(() => authStore.isLoggedIn)

// 현재 사용자가 이 팀에 가입되어 있는지 확인
const isJoined = computed(() => {
  if (!team.value || !authStore.user) return false

  // 백엔드 API 응답에서 nickname은 solvedAcId를 의미
  return team.value.teamMembers.some((member) => member.nickname === authStore.user.solvedAcId)
})

// 팀 상세 정보 로드
const loadTeamDetail = async () => {
  loading.value = true
  error.value = ''

  try {
    console.log('팀 상세 정보 요청 중...', teamId.value)
    const response = await teamApi.getTeamDetail(teamId.value)
    team.value = response.data
    console.log('팀 상세 정보 로드 성공:', team.value)
  } catch (err) {
    console.error('팀 상세 정보 로드 실패:', err)
    if (err.response?.status === 404) {
      error.value = '존재하지 않는 팀입니다.'
    } else if (err.response?.status === 403) {
      error.value = '팀 정보에 접근할 권한이 없습니다.'
    } else {
      error.value = err.response?.data?.message || '팀 정보를 불러오는데 실패했습니다.'
    }
  } finally {
    loading.value = false
  }
}

// 팀 가입 처리
const handleJoinTeam = async () => {
  if (!isLoggedIn.value) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }

  joiningTeam.value = true

  try {
    console.log('팀 가입 요청 중...', teamId.value)
    await teamApi.joinTeam(teamId.value)
    console.log('팀 가입 성공')
    alert('팀에 성공적으로 가입했습니다!')

    // 팀 정보 다시 로드하여 멤버 목록 업데이트
    await loadTeamDetail()
  } catch (err) {
    console.error('팀 가입 실패:', err)
    let errorMessage = '팀 가입에 실패했습니다.'

    if (err.response?.status === 400) {
      errorMessage = err.response.data?.message || '이미 가입된 팀이거나 잘못된 요청입니다.'
    } else if (err.response?.status === 401) {
      errorMessage = '로그인이 필요합니다.'
      router.push({ name: 'login', query: { redirect: route.fullPath } })
      return
    } else if (err.response?.status === 403) {
      errorMessage = '팀 가입 권한이 없습니다.'
    } else if (err.response?.status === 404) {
      errorMessage = '존재하지 않는 팀입니다.'
    } else if (err.response?.data?.message) {
      errorMessage = err.response.data.message
    }

    alert(errorMessage)
  } finally {
    joiningTeam.value = false
  }
}

// 멤버 프로필로 이동
const goToProfile = (solvedAcId) => {
  router.push(`/profile/${solvedAcId}`)
}

// 유틸리티 함수들
const formatDate = (dateString) => {
  if (!dateString) return '-'

  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

onMounted(async () => {
  await authStore.initialize()
  await loadTeamDetail()
})
</script>

<style scoped>
.team-detail-view {
  min-height: calc(100vh - 64px);
  background-color: #f8f9fa;
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4rem;
  color: #666;
  gap: 1rem;
  font-size: 1.1rem;
}

.loading svg {
  font-size: 2rem;
  color: var(--samsung-blue);
}

.error-message {
  text-align: center;
  padding: 4rem;
  color: #dc3545;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.error-message p {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

/* 팀 헤더 카드 */
.team-header-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 2rem;
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.team-avatar {
  flex-shrink: 0;
}

.team-image {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.team-main-info {
  flex: 1;
}

.team-name {
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.5rem;
}

.team-description {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.team-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--samsung-blue);
  margin-bottom: 0.25rem;
}

.stat-label {
  display: block;
  font-size: 0.9rem;
  color: #666;
}

.team-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background-color: var(--samsung-blue);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--samsung-blue-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(20, 40, 160, 0.3);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.joined-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  background: #d4edda;
  color: #155724;
  border-radius: 8px;
  font-weight: 500;
  font-size: 1rem;
}

/* 팀 멤버 섹션 */
.team-members-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #f1f3f5;
}

.no-members {
  text-align: center;
  padding: 3rem;
  color: #666;
  font-size: 1.1rem;
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.member-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1.5rem;
  transition: all 0.2s;
  cursor: pointer;
  border: 2px solid transparent;
}

.member-card:hover {
  background: #e9ecef;
  border-color: var(--samsung-blue);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.member-avatar {
  text-align: center;
  margin-bottom: 1rem;
}

.member-image {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.member-info {
  text-align: center;
  margin-bottom: 1rem;
}

.member-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.25rem;
}

.member-nickname {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
}

.member-stats {
  text-align: center;
}

.member-status {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background: #28a745;
  color: white;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

/* 팀 활동 섹션 */
.team-activity-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.activity-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.activity-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  transition: transform 0.2s;
}

.activity-card:hover {
  transform: translateY(-2px);
}

.activity-icon {
  font-size: 2rem;
  color: var(--samsung-blue);
  margin-bottom: 1rem;
}

.activity-number {
  display: block;
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--samsung-blue);
  margin-bottom: 0.25rem;
}

.activity-label {
  display: block;
  font-size: 0.9rem;
  color: #666;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .container {
    padding: 0 1rem;
  }

  .team-header-card {
    flex-direction: column;
    text-align: center;
    gap: 1.5rem;
  }

  .team-name {
    font-size: 2rem;
  }

  .team-stats {
    justify-content: center;
    gap: 1.5rem;
  }

  .members-grid {
    grid-template-columns: 1fr;
  }

  .activity-cards {
    grid-template-columns: 1fr;
  }
}
</style>
