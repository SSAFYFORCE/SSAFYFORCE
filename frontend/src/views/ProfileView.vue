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

              <!-- 동기화 버튼 (로그인 상태에 따라 다른 동작) -->
              <div class="sync-info">
                <div class="sync-header">
                  <span class="sync-label">마지막 동기화</span>
                  <button
                    @click="syncProfile"
                    class="sync-button"
                    :class="{ 'login-required': !isLoggedIn }"
                    :disabled="!canSync"
                    :title="
                      !isLoggedIn
                        ? '로그인이 필요합니다'
                        : isInCooldown
                          ? `${cooldownTime}초 후 다시 시도 가능`
                          : ''
                    "
                  >
                    <font-awesome-icon
                      :icon="!isLoggedIn ? ['fas', 'user'] : ['fas', 'sync']"
                      :class="{ 'fa-spin': isSyncing }"
                    />
                    {{ getSyncButtonText() }}
                  </button>
                </div>
                <span class="sync-time">{{ getRelativeTime(profile.lastProblemSyncTime) }}</span>
                <div v-if="syncResult" class="sync-result" :class="syncResultClass">
                  <span class="sync-result-text">{{ syncResult }}</span>
                </div>
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
        </div>

        <!-- 중앙/오른쪽: 빈 공간 또는 다른 컨텐츠 -->
        <div class="profile-stats">
          <!-- 통계 섹션들 제거됨 -->
        </div>

        <!-- 하단: 최근 해결한 문제 (무한스크롤) -->
        <div class="recent-problems-section">
          <h3>최근 해결한 문제</h3>

          <div v-if="loadingProblems && solvedProblems.length === 0" class="loading-problems">
            <font-awesome-icon :icon="['fas', 'spinner']" spin />
            <span>문제를 불러오는 중...</span>
          </div>

          <div v-else-if="solvedProblems.length === 0" class="no-problems">
            해결한 문제가 없습니다.
          </div>

          <div v-else class="problems-list">
            <div
              v-for="(problem, index) in solvedProblems"
              :key="`${problem.id}-${index}`"
              class="problem-item"
              :style="{ backgroundColor: getTierColor(problem.problemTier).backgroundColor }"
            >
              <div class="problem-tier-badge">
                <span
                  class="tier-indicator"
                  :style="{ backgroundColor: getTierColor(problem.problemTier).badgeColor }"
                >
                  {{ getTierShortName(problem.problemTier) }}
                </span>
              </div>

              <div class="problem-info">
                <div class="problem-header">
                  <span class="problem-number">{{ problem.problemNumber }}</span>
                  <span class="problem-title">{{ problem.problemTitle }}</span>
                </div>
                <div class="problem-meta">
                  <span class="solve-language">{{ problem.language }}</span>
                  <span class="solve-time">{{ getRelativeTime(problem.solvedDate) }}</span>
                  <span class="time-complexity" v-if="problem.timeComplexity">
                    시간: {{ problem.timeComplexity }}ms
                  </span>
                  <span class="space-complexity" v-if="problem.spaceComplexity">
                    메모리: {{ problem.spaceComplexity }}KB
                  </span>
                </div>
              </div>

              <div class="problem-actions">
                <a
                  v-if="problem.problemUrl"
                  :href="problem.problemUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="problem-link"
                >
                  <font-awesome-icon :icon="['fas', 'link']" />
                  문제 보기
                </a>
                <a
                  v-if="problem.submitUrl"
                  :href="problem.submitUrl"
                  target="_blank"
                  rel="noopener noreferrer"
                  class="submit-link"
                >
                  <font-awesome-icon :icon="['fas', 'code']" />
                  제출 보기
                </a>
              </div>
            </div>

            <!-- 무한스크롤 로딩 인디케이터 -->
            <div v-if="loadingProblems" class="loading-more">
              <font-awesome-icon :icon="['fas', 'spinner']" spin />
              <span>더 많은 문제를 불러오는 중...</span>
            </div>

            <!-- 더 이상 로드할 문제가 없는 경우 -->
            <div v-else-if="!hasMore && solvedProblems.length > 0" class="no-more-problems">
              모든 문제를 불러왔습니다.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { memberApi } from '@/api/memberApi'
import { solvedProblemApi } from '@/api/solvedProblemApi'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 반응성 데이터
const profile = ref(null)
const userTeams = ref(null)
const loading = ref(true)

// 해결한 문제 관련 상태
const solvedProblems = ref([])
const loadingProblems = ref(false)
const hasMore = ref(true)
const nextCursor = ref(null)
const isFirstLoad = ref(true)

// 동기화 상태
const isSyncing = ref(false)
const syncResult = ref('')
const syncResultClass = ref('')

// 로그인 상태 확인
const isLoggedIn = ref(false)

// 동기화 버튼 활성화 여부 체크
const canSync = computed(() => {
  return isLoggedIn.value && !isSyncing.value && !isInCooldown.value
})

// 쿨타임 관리
const isInCooldown = ref(false)
const cooldownTime = ref(0)
let cooldownInterval = null

// 현재 프로필을 보고 있는 사용자가 본인인지 확인
const isOwnProfile = ref(false)

// 무한스크롤 관련 메서드
const loadSolvedProblems = async (isInitial = false) => {
  if (loadingProblems.value || (!hasMore.value && !isInitial)) return

  try {
    loadingProblems.value = true
    const solvedAcId = getCurrentSolvedAcId()

    if (!solvedAcId) {
      throw new Error('사용자 정보를 찾을 수 없습니다.')
    }

    const cursor = isInitial ? null : nextCursor.value
    const response = await solvedProblemApi.getRecentSolvedProblems(solvedAcId, cursor)
    const data = response.data

    console.log('해결한 문제 응답:', data)

    if (isInitial) {
      solvedProblems.value = data.content || []
      isFirstLoad.value = data.isFirst
    } else {
      solvedProblems.value = [...solvedProblems.value, ...(data.content || [])]
    }

    hasMore.value = data.hasNext || false
    nextCursor.value = data.nextCursor || null

    console.log('로드된 문제 수:', solvedProblems.value.length, '더 있음:', hasMore.value)
  } catch (error) {
    console.error('해결한 문제 로드 실패:', error)
  } finally {
    loadingProblems.value = false
  }
}

// 현재 보고 있는 사용자의 solvedAcId를 가져오는 함수
const getCurrentSolvedAcId = () => {
  // URL에서 solvedAcId 파라미터가 있으면 그것을 사용, 없으면 로그인한 사용자의 ID 사용
  return route.params.solvedAcId || authStore.user?.solvedAcId
}

// 본인 프로필인지 확인하는 함수
const checkIsOwnProfile = () => {
  const currentSolvedAcId = getCurrentSolvedAcId()
  isOwnProfile.value = currentSolvedAcId === authStore.user?.solvedAcId
}

// 로그인 상태 확인 함수
const checkLoginStatus = () => {
  isLoggedIn.value = !!authStore.user
}

// 스크롤 이벤트 핸들러
const handleScroll = () => {
  if (loadingProblems.value || !hasMore.value) return

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  // 페이지 하단에서 200px 전에 도달하면 다음 페이지 로드
  if (scrollTop + windowHeight >= documentHeight - 200) {
    loadSolvedProblems(false)
  }
}

// 쿨타임 관리 메서드
const startCooldown = () => {
  isInCooldown.value = true
  cooldownTime.value = 60 // 60초

  cooldownInterval = setInterval(() => {
    cooldownTime.value--
    if (cooldownTime.value <= 0) {
      isInCooldown.value = false
      clearInterval(cooldownInterval)
      cooldownInterval = null
    }
  }, 1000)
}

const getSyncButtonText = () => {
  if (!isLoggedIn.value) return '로그인 필요'
  if (isSyncing.value) return '동기화 중...'
  if (isInCooldown.value) return `${cooldownTime.value}초`
  return isOwnProfile.value ? '동기화' : '동기화하기'
}

// getTierColor 함수를 개선하여 배경색과 기본 색상을 모두 반환
const getTierColor = (tier) => {
  if (!tier)
    return {
      backgroundColor: 'var(--tier-unrated-bg)',
      badgeColor: 'var(--tier-unrated)',
      borderColor: '#2d2d2d',
      backgroundAlpha: 'rgba(45, 45, 45, 0.05)',
    }

  // API에서 "G4", "G3", "S1" 등의 형태로 오는 경우 처리
  const tierPrefix = tier.charAt(0).toLowerCase()

  const colorMap = {
    r: {
      // Ruby
      backgroundColor: 'var(--tier-ruby-bg)',
      badgeColor: 'var(--tier-ruby)',
      borderColor: '#ff0062',
      backgroundAlpha: 'rgba(255, 0, 98, 0.05)',
    },
    d: {
      // Diamond
      backgroundColor: 'var(--tier-diamond-bg)',
      badgeColor: 'var(--tier-diamond)',
      borderColor: '#00b4fc',
      backgroundAlpha: 'rgba(0, 180, 252, 0.05)',
    },
    p: {
      // Platinum
      backgroundColor: 'var(--tier-platinum-bg)',
      badgeColor: 'var(--tier-platinum)',
      borderColor: '#27e2a4',
      backgroundAlpha: 'rgba(39, 226, 164, 0.05)',
    },
    g: {
      // Gold
      backgroundColor: 'var(--tier-gold-bg)',
      badgeColor: 'var(--tier-gold)',
      borderColor: '#ec9a00',
      backgroundAlpha: 'rgba(236, 154, 0, 0.05)',
    },
    s: {
      // Silver
      backgroundColor: 'var(--tier-silver-bg)',
      badgeColor: 'var(--tier-silver)',
      borderColor: '#435f7a',
      backgroundAlpha: 'rgba(67, 95, 122, 0.05)',
    },
    b: {
      // Bronze
      backgroundColor: 'var(--tier-bronze-bg)',
      badgeColor: 'var(--tier-bronze)',
      borderColor: '#ad5600',
      backgroundAlpha: 'rgba(173, 86, 0, 0.05)',
    },
    u: {
      // Unrated
      backgroundColor: 'var(--tier-unrated-bg)',
      badgeColor: 'var(--tier-unrated)',
      borderColor: '#2d2d2d',
      backgroundAlpha: 'rgba(45, 45, 45, 0.05)',
    },
  }

  return colorMap[tierPrefix] || colorMap.u
}

const getTierShortName = (tier) => {
  if (!tier) return 'NR'

  // 이미 짧은 형태인 경우 그대로 반환
  if (tier.length <= 3) return tier

  // 전체 이름인 경우 짧은 형태로 변환
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
  if (!dateString) return '알 수 없음'

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
  return '오래전' // 1년 이상인 경우
}

const goToTeam = (teamId) => {
  router.push(`/team/${teamId}`)
}

const loadProfile = async () => {
  loading.value = true
  try {
    const solvedAcId = getCurrentSolvedAcId()
    console.log('현재 조회할 solvedAcId:', solvedAcId)

    if (!solvedAcId) {
      throw new Error('사용자 정보를 찾을 수 없습니다.')
    }

    // 본인 프로필인지 확인
    checkIsOwnProfile()

    // 로그인 상태 확인
    checkLoginStatus()

    // 사용자 존재 여부 먼저 확인 (선택사항)
    try {
      const existsResponse = await memberApi.checkUserExists(solvedAcId)
      if (!existsResponse.exists) {
        throw new Error('사용자를 찾을 수 없습니다.')
      }
    } catch (error) {
      console.log(error)
      // 존재 확인 API가 없는 경우 무시하고 계속 진행
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

    // 해결한 문제 초기 로드
    await loadSolvedProblems(true)
  } catch (error) {
    console.error('프로필을 불러오는 중 오류 발생:', error)
    // 에러 발생 시 404 페이지로 이동 또는 에러 메시지 표시
    if (error.response?.status === 404 || error.message.includes('찾을 수 없습니다')) {
      // 404 페이지로 이동하거나 에러 상태 설정
      router.push('/404')
    }
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

// 프로필 동기화 (로그인한 사용자만 가능)
const syncProfile = async () => {
  if (!isLoggedIn.value) {
    alert('로그인이 필요합니다.')
    router.push('/login')
    return
  }

  if (isSyncing.value || isInCooldown.value) return

  isSyncing.value = true
  syncResult.value = ''
  syncResultClass.value = ''

  try {
    const solvedAcId = getCurrentSolvedAcId()
    if (!solvedAcId) {
      throw new Error('사용자 정보를 찾을 수 없습니다.')
    }

    // 사용자에게 시간이 오래 걸릴 수 있음을 알림
    const targetName = isOwnProfile.value ? '내' : `${profile.value.name}님의`
    syncResult.value = `${targetName} 프로필을 동기화 중입니다... 시간이 오래 걸릴 수 있습니다.`
    syncResultClass.value = 'info'

    // 동기화 API 호출 (2분 타임아웃)
    const result = await solvedProblemApi.syncSolvedProblems(solvedAcId)
    const data = result.data

    syncResult.value = `${profile.value.name}님의 ${data.resultCount}개의 새로운 문제가 동기화되었습니다.`
    syncResultClass.value = 'success'

    // 해결한 문제 다시 로드
    solvedProblems.value = []
    nextCursor.value = null
    hasMore.value = true
    await loadSolvedProblems(true)

    // 쿨타임 시작
    startCooldown()
  } catch (error) {
    console.error('프로필 동기화 실패:', error)

    // 타임아웃 에러인지 확인
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      syncResult.value = '동기화 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.'
    } else {
      syncResult.value = '동기화에 실패했습니다. 잠시 후 다시 시도해주세요.'
    }
    syncResultClass.value = 'error'
  } finally {
    isSyncing.value = false

    // 5초 후 결과 메시지 숨기기 (좀 더 길게)
    setTimeout(() => {
      syncResult.value = ''
      syncResultClass.value = ''
    }, 5000)
  }
}

// 라우트 파라미터 변경 감지
watch(
  () => route.params.solvedAcId,
  (newSolvedAcId, oldSolvedAcId) => {
    if (newSolvedAcId !== oldSolvedAcId) {
      // 파라미터가 변경되면 프로필 다시 로드
      loadProfile()
    }
  },
)

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  await authStore.initialize()
  checkLoginStatus() // 로그인 상태 확인
  await loadProfile()

  // 스크롤 이벤트 리스너 등록
  window.addEventListener('scroll', handleScroll)
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)

  // 쿨타임 인터벌 정리
  if (cooldownInterval) {
    clearInterval(cooldownInterval)
  }
})
</script>

<style scoped>
/* 기존 CSS 변수에 추가할 부분 */
:root {
  --samsung-blue: #1428a0;
  --samsung-blue-dark: #0f3a7c;
  --samsung-blue-light: #1e5bc6;
  --samsung-blue-alpha: rgba(20, 40, 160, 0.1);

  /* 티어 색상 변수 */
  --tier-ruby: #e91e63;
  --tier-diamond: #2196f3;
  --tier-platinum: #00bcd4;
  --tier-gold: #ffc107;
  --tier-silver: #9e9e9e;
  --tier-bronze: #8d6e63;
  --tier-unrated: #757575;

  /* 백그라운드용 티어 색상 (좀 더 진한 색상) */
  --tier-ruby-bg: #ff0062;
  --tier-diamond-bg: #00b4fc;
  --tier-platinum-bg: #27e2a4;
  --tier-gold-bg: #ec9a00;
  --tier-silver-bg: #435f7a;
  --tier-bronze-bg: #ad5600;
  --tier-unrated-bg: #2d2d2d;

  /* 문제 아이템 배경용 알파 색상 */
  --tier-ruby-alpha: rgba(255, 0, 98, 0.08);
  --tier-diamond-alpha: rgba(0, 180, 252, 0.08);
  --tier-platinum-alpha: rgba(39, 226, 164, 0.08);
  --tier-gold-alpha: rgba(236, 154, 0, 0.08);
  --tier-silver-alpha: rgba(67, 95, 122, 0.08);
  --tier-bronze-alpha: rgba(173, 86, 0, 0.08);
  --tier-unrated-alpha: rgba(45, 45, 45, 0.08);
}

/* 문제 아이템 기본 스타일 개선 */
.problem-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background-color: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  border-left: 4px solid #e9ecef; /* 기본 왼쪽 테두리 */
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.problem-item:hover {
  background-color: #f8f9fa;
  border-color: #dee2e6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

/* 티어별 문제 아이템 스타일 */
.tier-ruby-item {
  background: linear-gradient(135deg, var(--tier-ruby-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-ruby-bg);
}

.tier-ruby-item:hover {
  background: linear-gradient(135deg, rgba(255, 0, 98, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-ruby);
}

.tier-diamond-item {
  background: linear-gradient(135deg, var(--tier-diamond-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-diamond-bg);
}

.tier-diamond-item:hover {
  background: linear-gradient(135deg, rgba(0, 180, 252, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-diamond);
}

.tier-platinum-item {
  background: linear-gradient(135deg, var(--tier-platinum-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-platinum-bg);
}

.tier-platinum-item:hover {
  background: linear-gradient(135deg, rgba(39, 226, 164, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-platinum);
}

.tier-gold-item {
  background: linear-gradient(135deg, var(--tier-gold-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-gold-bg);
}

.tier-gold-item:hover {
  background: linear-gradient(135deg, rgba(236, 154, 0, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-gold);
}

.tier-silver-item {
  background: linear-gradient(135deg, var(--tier-silver-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-silver-bg);
}

.tier-silver-item:hover {
  background: linear-gradient(135deg, rgba(67, 95, 122, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-silver);
}

.tier-bronze-item {
  background: linear-gradient(135deg, var(--tier-bronze-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-bronze-bg);
}

.tier-bronze-item:hover {
  background: linear-gradient(135deg, rgba(173, 86, 0, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-bronze);
}

.tier-unrated-item {
  background: linear-gradient(135deg, var(--tier-unrated-alpha) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-left-color: var(--tier-unrated-bg);
}

.tier-unrated-item:hover {
  background: linear-gradient(135deg, rgba(45, 45, 45, 0.12) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-left-color: var(--tier-unrated);
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
  gap: 2rem;
  grid-template-areas: 'profile-card recent-problems';
}

/* 프로필 카드 컨테이너 (sticky) */
.profile-card-container {
  grid-area: profile-card;
  position: sticky;
  top: 2rem;
  height: fit-content;
}

/* 기존 .profile-card 스타일 유지 */
.profile-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  text-align: center;
}

@media (max-width: 1024px) {
  .profile-layout {
    grid-template-columns: 1fr;
    grid-template-areas:
      'profile-card'
      'recent-problems';
  }

  .profile-card-container {
    position: static; /* sticky 제거 */
  }
}
.profile-card-container {
  grid-area: profile-card;
  position: sticky;
  top: 2rem;
  height: fit-content;
}

/* 데스크톱에서만 sticky 적용 */
@media (min-width: 1025px) {
  .profile-card-container {
    position: sticky;
    top: 2rem;
  }
}

@media (max-width: 1024px) {
  .profile-card-container {
    position: static;
  }
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
  min-width: 60px;
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

.sync-result {
  margin-top: 0.5rem;
  padding: 0.5rem;
  border-radius: 4px;
  width: 100%;
}

.sync-result.success {
  background: #d4edda;
  border: 1px solid #c3e6cb;
}

.sync-result.error {
  background: #f8d7da;
  border: 1px solid #f5c6cb;
}

.sync-result.info {
  background: #d1ecf1;
  border: 1px solid #bee5eb;
}

.sync-result-text {
  font-size: 0.8rem;
  font-weight: 500;
}

.sync-result.success .sync-result-text {
  color: #155724;
}

.sync-result.error .sync-result-text {
  color: #721c24;
}

.sync-result.info .sync-result-text {
  color: #0c5460;
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

/* 통계 섹션 - 제거됨 */
.profile-stats {
  grid-area: profile-stats;
  /* 빈 공간 */
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

.loading-problems,
.no-problems {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  color: #666;
  gap: 0.5rem;
}

.problems-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.problem-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background-color: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.problem-item:hover {
  background-color: #f8f9fa;
  border-color: #dee2e6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.problem-tier-badge {
  min-width: 60px;
}

.tier-indicator {
  display: inline-block;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 1rem;
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
  font-weight: 600;
  color: #333;
  font-size: 1rem;
}

.problem-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: #666;
  flex-wrap: wrap;
}

.problem-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  min-width: 120px;
}

.problem-link,
.submit-link {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0rem;
  text-decoration: none;
  transition: background-color 0.2s;
}

.problem-link {
  color: var(--samsung-blue);
  /* background: var(--samsung-blue-alpha); */
}

.problem-link:hover {
  background: rgba(20, 40, 160, 0.2);
}

.submit-link {
  color: #007e1d;
  /* background: rgba(40, 167, 69, 0.1); */
}

.submit-link:hover {
  background: rgba(40, 167, 69, 0.2);
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 2rem;
  color: #666;
  font-size: 0.9rem;
}

.no-more-problems {
  text-align: center;
  padding: 2rem;
  color: #999;
  font-size: 0.9rem;
  border-top: 1px solid #eee;
  margin-top: 1rem;
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

  .problem-actions {
    flex-direction: row;
    justify-content: space-between;
    min-width: auto;
  }

  .solve-stats {
    flex-direction: column;
    gap: 1rem;
  }

  .problem-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
}

@media (max-width: 480px) {
  .problem-header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.5rem;
  }

  .problem-actions {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
