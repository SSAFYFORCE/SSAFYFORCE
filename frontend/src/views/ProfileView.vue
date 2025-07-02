<!-- src/views/ProfileView.vue -->
<template>
  <div class="profile-view">
    <div class="container">
      <div v-if="loading" class="loading">
        <font-awesome-icon :icon="['fas', 'spinner']" spin />
        <span>프로필을 불러오는 중...</span>
      </div>

      <div v-else-if="profile && userTeams" class="profile-layout">
        <!-- 왼쪽: 프로필 카드 -->
        <div class="profile-card">
          <div class="profile-avatar">
            <img :src="profile.profileImage" :alt="profile.name" />
          </div>

          <div class="profile-main-info">
            <h2 class="name">{{ profile.name }}</h2>
            <p class="solved-ac-id">@{{ profile.solvedAcId }}</p>

            <!-- <div class="rank-info">
              <div class="rank-item">
                <span class="rank-label">Rank</span>
                <span class="rank-value">#{{ stats.rank }}</span>
              </div>
              <div class="rank-item">
                <span class="rank-label">상위</span>
                <span class="rank-value">{{ stats.topPercent }}%</span>
              </div>
            </div> -->

            <div class="sync-info">
              <div class="sync-header">
                <span class="sync-label">마지막 동기화</span>
                <button @click="syncProfile" class="sync-button" :disabled="isSyncing">
                  <font-awesome-icon :icon="['fas', 'sync']" :class="{ 'fa-spin': isSyncing }" />
                  {{ isSyncing ? '동기화 중...' : '동기화' }}
                </button>
              </div>
              <span class="sync-time">{{ getRelativeTime(profile.lastProblemSyncTime) }}</span>
            </div>
          </div>

          <!-- 소속 팀 정보 -->
          <div class="teams-section">
            <h3 class="teams-title">소속 팀</h3>
            <div class="teams-list">
              <div
                v-for="team in userTeams.teams"
                :key="team.id"
                class="team-item"
                @click="goToTeam(team.id)"
              >
                <div class="team-avatar">
                  <img :src="team.profileImage" :alt="team.name" />
                </div>
                <div class="team-info">
                  <span class="team-name">{{ team.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 중앙/오른쪽: 통계 -->
        <div class="profile-stats">
          <!-- 문제 해결 현황 -->
          <div class="stats-section">
            <h3>문제 해결 현황</h3>
            <div class="solve-stats">
              <div class="solve-count">
                <span class="count-number">{{ stats.solvedProblems }}</span>
                <span class="count-label">맞혔습니다!!</span>
              </div>
              <div class="solve-rate">
                <span class="rate-number">{{ stats.correctRate }}%</span>
                <span class="rate-label">정답률</span>
              </div>
            </div>
          </div>

          <!-- 현재 연속 해결 -->
          <div class="streak-section">
            <h3>현재 연속 해결</h3>
            <div class="streak-info">
              <div class="streak-count">
                <span class="streak-number">{{ stats.streak }}</span>
                <span class="streak-label">일 연속</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 하단: 최근 해결한 문제 -->
        <div class="recent-problems-section">
          <h3>최근 해결한 문제</h3>
          <div class="problems-list">
            <div
              v-for="(problem, index) in stats.recentSolved"
              :key="`${problem.problemNumber}-${index}`"
              class="problem-item"
            >
              <div class="problem-tier-badge">
                <span
                  class="tier-indicator"
                  :style="{ backgroundColor: getTierColor(problem.tier).backgroundColor }"
                >
                  {{ getTierShortName(problem.tier) }}
                </span>
              </div>

              <div class="problem-info">
                <div class="problem-header">
                  <span class="problem-number">{{ problem.problemNumber }}</span>
                  <span class="problem-title">{{ problem.title }}</span>
                </div>
                <div class="problem-meta">
                  <span class="solve-language">{{ problem.language }}</span>
                  <span class="solve-time">{{ getRelativeTime(problem.solvedAt) }}</span>
                </div>
              </div>

              <div class="problem-experience">
                <div class="exp-gained">
                  <span class="exp-label">경험치 획득</span>
                  <span class="exp-value">{{ problem.experience }}원</span>
                </div>
                <div class="exp-total">
                  <span class="exp-total-label">경험치 합계</span>
                  <span class="exp-total-value">{{ problem.experience }}원</span>
                </div>
              </div>
            </div>
          </div>

          <div class="more-problems">
            <button class="more-btn">더보기</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { memberApi } from '@/api/memberApi'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 반응성 데이터
const profile = ref(null)
const userTeams = ref(null)
const stats = ref(null)
const loading = ref(true)

// 동기화 상태
const isSyncing = ref(false)

// 메서드
const getTierColor = (tier) => {
  const tierName = tier.split(' ')[0].toLowerCase()
  const colors = {
    ruby: { backgroundColor: '#ff0062', color: '#ffffff' },
    diamond: { backgroundColor: '#00b4fc', color: '#ffffff' },
    platinum: { backgroundColor: '#27e2a4', color: '#ffffff' },
    gold: { backgroundColor: '#ec9a00', color: '#ffffff' },
    silver: { backgroundColor: '#435f7a', color: '#ffffff' },
    bronze: { backgroundColor: '#ad5600', color: '#ffffff' },
    unrated: { backgroundColor: '#2d2d2d', color: '#ffffff' },
  }
  return colors[tierName] || colors.unrated
}

const getTierShortName = (tier) => {
  const tierMap = {
    'Ruby V': 'R5',
    'Ruby IV': 'R4',
    'Ruby III': 'R3',
    'Ruby II': 'R2',
    'Ruby I': 'R1',
    'Diamond V': 'D5',
    'Diamond IV': 'D4',
    'Diamond III': 'D3',
    'Diamond II': 'D2',
    'Diamond I': 'D1',
    'Platinum V': 'P5',
    'Platinum IV': 'P4',
    'Platinum III': 'P3',
    'Platinum II': 'P2',
    'Platinum I': 'P1',
    'Gold V': 'G5',
    'Gold IV': 'G4',
    'Gold III': 'G3',
    'Gold II': 'G2',
    'Gold I': 'G1',
    'Silver V': 'S5',
    'Silver IV': 'S4',
    'Silver III': 'S3',
    'Silver II': 'S2',
    'Silver I': 'S1',
    'Bronze V': 'B5',
    'Bronze IV': 'B4',
    'Bronze III': 'B3',
    'Bronze II': 'B2',
    'Bronze I': 'B1',
  }
  return tierMap[tier] || tier
}

const getRelativeTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))
  const diffHours = Math.floor((now - date) / (1000 * 60 * 60))
  const diffMinutes = Math.floor((now - date) / (1000 * 60))

  if (diffMinutes < 60) return `${diffMinutes}분 전`
  if (diffHours < 24) return `${diffHours}시간 전`
  if (diffDays === 0) return '오늘'
  if (diffDays === 1) return '1일 전'
  if (diffDays < 7) return `${diffDays}일 전`
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}주 전`
  if (diffDays < 365) return `${Math.floor(diffDays / 30)}개월 전`
  return `${Math.floor(diffDays / 365)}년 전`
}

const goToTeam = (teamId) => {
  router.push(`/team/${teamId}`)
}

const loadProfile = async () => {
  loading.value = true
  try {
    // 현재 로그인한 사용자의 정보 사용
    const solvedAcId = authStore.user?.solvedAcId
    console.log('solvedAcId:', solvedAcId)

    if (!solvedAcId) {
      throw new Error('사용자 정보를 찾을 수 없습니다.')
    }

    // API 호출
    const [profileResponse, teamsResponse] = await Promise.all([
      memberApi.getMemberProfile(solvedAcId),
      memberApi.getMemberTeams(solvedAcId),
    ])

    console.log('프로필 응답:', profileResponse)
    console.log('팀 응답:', teamsResponse)

    // API 응답 데이터 파싱
    profile.value = parseProfileData(profileResponse.data)
    userTeams.value = parseTeamsData(teamsResponse.data)

    // 임시로 통계 데이터는 더미 데이터 사용
    stats.value = {
      solvedProblems: 0,
      correctRate: 0,
      streak: 0,
      recentSolved: [],
    }
  } catch (error) {
    console.error('프로필을 불러오는 중 오류 발생:', error)
    // 에러 처리
  } finally {
    loading.value = false
  }
}

// 데이터 파싱 함수들
const parseProfileData = (data) => {
  return {
    profileImage: data.profileImage || 'https://via.placeholder.com/80x80',
    name: data.name || '이름 없음',
    solvedAcId: data.solvedAcId || '',
    lastProblemSyncTime: data.lastProblemSyncTime || new Date().toISOString(),
  }
}

const parseTeamsData = (data) => {
  return {
    teams: (data.teams || []).map((team) => ({
      id: team.id,
      name: team.name || '팀 이름 없음',
      profileImage:
        team.profileImage ||
        `https://via.placeholder.com/40x40/1428A0/ffffff?text=${team.name?.charAt(0) || 'T'}`,
    })),
  }
}

// 프로필 동기화
const syncProfile = async () => {
  if (isSyncing.value) return

  isSyncing.value = true
  try {
    const solvedAcId = authStore.user?.solvedAcId
    if (!solvedAcId) {
      throw new Error('사용자 정보를 찾을 수 없습니다.')
    }

    // 동기화 API 호출
    await memberApi.syncProfile(solvedAcId)

    // 프로필 다시 로드
    await loadProfile()

    alert('프로필이 성공적으로 동기화되었습니다.')
  } catch (error) {
    console.error('프로필 동기화 실패:', error)
    alert('프로필 동기화에 실패했습니다. 잠시 후 다시 시도해주세요.')
  } finally {
    isSyncing.value = false
  }
}

// 컴포넌트 마운트 시 프로필 로드
onMounted(async () => {
  await authStore.initialize()
  loadProfile()
})
</script>

<style scoped>
:root {
  --samsung-blue: #1428a0;
  --samsung-blue-dark: #0f3a7c;
  --samsung-blue-light: #1e5bc6;
  --samsung-blue-alpha: rgba(20, 40, 160, 0.1);
}

.profile-view {
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
  padding: 3rem;
  color: #666;
  gap: 1rem;
}

/* 프로필 레이아웃 */
.profile-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  grid-template-rows: auto 1fr;
  gap: 2rem;
  grid-template-areas:
    'profile-card profile-stats'
    'recent-problems recent-problems';
}

/* 프로필 카드 */
.profile-card {
  grid-area: profile-card;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  text-align: center;
  height: fit-content;
}

.profile-avatar {
  margin-bottom: 1.5rem;
}

.profile-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-main-info {
  margin-bottom: 2rem;
}

.name {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.25rem;
}

.solved-ac-id {
  font-size: 1rem;
  color: #666;
  margin-bottom: 1rem;
}

.tier-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tier-badge {
  font-size: 1.1rem;
  font-weight: 600;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  color: white;
}

.rating {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--samsung-blue);
}

.rank-info {
  display: flex;
  justify-content: space-around;
  gap: 1rem;
  margin-bottom: 1rem;
}

.rank-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rank-label {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.25rem;
}

.rank-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.sync-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  margin-bottom: 2rem;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.sync-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.sync-button {
  background: none;
  border: none;
  color: var(--samsung-blue);
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.sync-button:hover:not(:disabled) {
  background: var(--samsung-blue-alpha);
}

.sync-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.fa-spin {
  animation: fa-spin 1s infinite linear;
}

@keyframes fa-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.sync-time {
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
}

/* 팀 섹션 */
.teams-section {
  border-top: 1px solid #e9ecef;
  padding-top: 1.5rem;
}

.teams-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1rem;
}

.teams-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.team-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.team-item:hover {
  background: #f8f9fa;
}

.team-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.team-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.team-info {
  flex: 1;
}

.team-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
}

/* 통계 섹션 */
.profile-stats {
  grid-area: profile-stats;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.stats-section,
.streak-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.stats-section h3,
.streak-section h3 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.1rem;
  font-weight: 600;
}

.solve-stats {
  display: flex;
  gap: 3rem;
  justify-content: center;
}

.solve-count,
.solve-rate {
  text-align: center;
}

.count-number,
.rate-number {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: var(--samsung-blue);
  line-height: 1;
}

.count-label,
.rate-label {
  display: block;
  font-size: 0.9rem;
  color: #666;
  margin-top: 0.25rem;
}

.streak-info {
  text-align: center;
}

.streak-count {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.streak-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #27e2a4;
  line-height: 1;
}

.streak-label {
  font-size: 1rem;
  color: #666;
  margin-top: 0.5rem;
}

/* 최근 해결한 문제 섹션 */
.recent-problems-section {
  grid-area: recent-problems;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.recent-problems-section h3 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.2rem;
  font-weight: 600;
}

.problems-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.problem-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.problem-item:hover {
  background: #f0f0f0;
}

.problem-tier-badge {
  min-width: 60px;
}

.tier-indicator {
  display: inline-block;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  text-align: center;
  min-width: 50px;
}

.problem-info {
  flex: 1;
}

.problem-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.25rem;
}

.problem-number {
  font-weight: 600;
  color: var(--samsung-blue);
  font-size: 0.9rem;
}

.problem-title {
  color: #333;
  font-size: 0.9rem;
}

.problem-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: #666;
}

.problem-experience {
  text-align: right;
  min-width: 120px;
}

.exp-gained,
.exp-total {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.exp-label,
.exp-total-label {
  font-size: 0.7rem;
  color: #666;
}

.exp-value,
.exp-total-value {
  font-size: 0.8rem;
  font-weight: 600;
  color: #333;
}

.more-problems {
  text-align: center;
}

.more-btn {
  padding: 0.75rem 2rem;
  background: var(--samsung-blue);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.more-btn:hover {
  background: var(--samsung-blue-dark);
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .profile-layout {
    grid-template-columns: 1fr;
    grid-template-areas:
      'profile-card'
      'profile-stats'
      'recent-problems';
  }

  .solve-stats {
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 1rem;
  }

  .problem-item {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .problem-experience {
    text-align: left;
    min-width: auto;
  }

  .exp-gained,
  .exp-total {
    flex-direction: row;
    justify-content: space-between;
  }

  .solve-stats {
    flex-direction: column;
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .problem-header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.5rem;
  }

  .problem-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
}
</style>
