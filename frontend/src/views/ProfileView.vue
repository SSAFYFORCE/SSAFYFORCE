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
          <div class="profile-image-section">
            <div class="profile-image-wrapper">
              <img :src="profile.profileImage" alt="프로필 이미지" class="profile-image" />

              <div v-if="isOwnProfile" class="image-upload-overlay" @click="triggerFileInput">
                <font-awesome-icon :icon="['fas', 'camera']" />
                <span>이미지 변경</span>
              </div>
              <div
                v-if="isOwnProfile && profile?.profileImage"
                class="image-delete-overlay"
                @click="handleImageDelete"
              >
                <font-awesome-icon :icon="['fas', 'trash']" />
                <span>이미지 삭제</span>
              </div>
            </div>
            <input
              v-if="isOwnProfile"
              type="file"
              ref="fileInput"
              @change="handleImageUpload"
              accept="image/*"
              class="hidden"
            />
          </div>

          <div class="profile-main-info">
            <h2 class="name">{{ profile.name }}</h2>
            <p class="solved-ac-id">@{{ profile.solvedAcId }}</p>

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
            <div class="external-links">
              <a
                :href="`https://solved.ac/profile/${profile.solvedAcId}`"
                target="_blank"
                rel="noopener noreferrer"
                class="external-link solved-ac-link"
              >
                <font-awesome-icon :icon="['fas', 'link']" />
                solved.ac
              </a>
              <a
                :href="`https://www.acmicpc.net/user/${profile.solvedAcId}`"
                target="_blank"
                rel="noopener noreferrer"
                class="external-link baekjoon-link"
              >
                <font-awesome-icon :icon="['fas', 'link']" />
                백준
              </a>
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
                  <font-awesome-icon :icon="['fas', 'external-link-alt']" />
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
import { uploadProfileImage, deleteProfileImage } from '@/api/memberApi'
import defaultProfileImage from '@/mockdata/default_profile.png'
const router = useRouter()
const authStore = useAuthStore()
const route = useRoute()
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
const DEFAULT_PROFILE_IMAGE = defaultProfileImage
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
  isLoggedIn.value = authStore.user !== null && authStore.user !== undefined
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
  router.push(`/teams/${teamId}`)
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

    profile.value = parseProfileData(profileResponse.data)
    userTeams.value = parseTeamsData(teamsResponse.data)

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
    profileImage: data.profileImage || DEFAULT_PROFILE_IMAGE,
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
      profileImage: team.profileImage || DEFAULT_PROFILE_IMAGE,
    })),
  }
}

// 프로필 동기화
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

    const targetName = isOwnProfile.value ? '내' : `${profile.value.name}님의`
    syncResult.value = `${targetName} 프로필을 동기화 중입니다... 시간이 오래 걸릴 수 있습니다.`
    syncResultClass.value = 'info'

    const result = await solvedProblemApi.syncSolvedProblems(solvedAcId)
    const data = result.data

    syncResult.value = `${profile.value.name}님의 ${data.resultCount}개의 새로운 문제가 동기화되었습니다.`
    syncResultClass.value = 'success'

    const profileResponse = await memberApi.getMemberProfile(solvedAcId)
    profile.value = parseProfileData(profileResponse.data)

    // 해결한 문제 다시 로드
    solvedProblems.value = []
    nextCursor.value = null
    hasMore.value = true
    await loadSolvedProblems(true)

    // 쿨타임 시작
    startCooldown()
  } catch (error) {
    console.error('프로필 동기화 실패:', error)

    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      syncResult.value = '동기화 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.'
    } else {
      syncResult.value = '동기화에 실패했습니다. 잠시 후 다시 시도해주세요.'
    }
    syncResultClass.value = 'error'
  } finally {
    isSyncing.value = false

    setTimeout(() => {
      syncResult.value = ''
      syncResultClass.value = ''
    }, 5000)
  }
}

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

// 프로필 이미지 업로드 관련 함수들
const fileInput = ref(null)

const triggerFileInput = () => {
  fileInput.value.click()
}

// 이미지 업로드 핸들러
async function handleImageUpload(event) {
  const file = event.target.files[0]
  if (!file) {
    return
  }

  try {
    console.log('프로필 이미지 업로드 시작...')
    // 1. 파일을 백엔드로 전송
    const response = await uploadProfileImage(file)
    console.log('이미지 업로드 및 프로필 업데이트 성공')

    // 2. 응답에서 새로운 이미지 URL 가져오기
    const newImageUrl = response.data.imageUrl
    console.log('새로운 이미지 URL:', newImageUrl)

    // 3. AuthStore 업데이트 (Nav바 즉시 반영)
    authStore.updateProfileImage(newImageUrl)

    // 4. 로컬 프로필 정보도 업데이트
    await loadProfile()
    console.log('다시 불러온 후의 프로필 정보:', profile.value)

    alert('프로필 이미지가 성공적으로 변경되었습니다.')
  } catch (error) {
    console.error('이미지 업로드 중 에러 발생:', error)
    if (error.response) {
      console.error('에러 응답 데이터:', error.response.data)
      alert(`이미지 업로드 실패: ${error.response.data.message || '서버 오류'}`)
    } else {
      alert('이미지 업로드에 실패했습니다.')
    }
  }
}

async function handleImageDelete() {
  if (!confirm('프로필 이미지를 삭제하시겠습니까?')) {
    return
  }

  try {
    console.log('프로필 이미지 삭제 시작...')
    await deleteProfileImage()
    console.log('이미지 삭제 성공')

    // AuthStore 업데이트 (Nav바 즉시 반영)
    authStore.updateProfileImage(null)

    // 최신 프로필 정보 다시 불러오기
    await loadProfile()
    alert('프로필 이미지가 성공적으로 삭제되었습니다.')
  } catch (error) {
    console.error('이미지 삭제 중 에러 발생:', error)
    if (error.response) {
      console.error('에러 응답 데이터:', error.response.data)
      alert(`이미지 삭제 실패: ${error.response.data.message || '서버 오류'}`)
    } else {
      alert('이미지 삭제에 실패했습니다.')
    }
  }
}
</script>

<style scoped>
/* CSS 변수 정의 */
:root {
  --samsung-blue: #1428a0;
  --samsung-blue-dark: #0f3a7c;
  --samsung-blue-light: #1e5bc6;
  --samsung-blue-alpha: rgba(20, 40, 160, 0.1);

  /* 티어 알파 색상 */
  --tier-ruby-alpha: rgba(255, 0, 98, 0.08);
  --tier-diamond-alpha: rgba(0, 180, 252, 0.08);
  --tier-platinum-alpha: rgba(39, 226, 164, 0.08);
  --tier-gold-alpha: rgba(236, 154, 0, 0.08);
  --tier-silver-alpha: rgba(67, 95, 122, 0.08);
  --tier-bronze-alpha: rgba(173, 86, 0, 0.08);
  --tier-unrated-alpha: rgba(45, 45, 45, 0.08);
}

/* 기본 레이아웃 */
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

.profile-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 2rem;
  grid-template-areas: 'profile-card recent-problems';
}

/* 로딩 상태 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem;
  color: #666;
  gap: 1rem;
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
  position: sticky;
  top: 2rem;
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

/* 동기화 섹션 */
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

/* 문제 아이템 기본 스타일 */
.problem-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background-color: #ffffff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  border-left: 4px solid #e9ecef;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.problem-item:hover {
  background-color: #f8f9fa;
  border-color: #dee2e6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
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
  font-size: 0.9rem;
  text-decoration: none;
  transition: background-color 0.2s;
}

.problem-link {
  color: var(--samsung-blue);
  background: var(--samsung-blue-alpha);
}

.problem-link:hover {
  background: rgba(20, 40, 160, 0.2);
}

.submit-link {
  background: rgba(40, 167, 69, 0.1);
  color: #007e1d;
}

.submit-link:hover {
  background: rgba(40, 167, 69, 0.2);
}

/* 무한스크롤 관련 */
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

/* 애니메이션 */
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

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .profile-layout {
    grid-template-columns: 1fr;
    grid-template-areas:
      'profile-card'
      'recent-problems';
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

  .problem-actions {
    flex-direction: row;
    justify-content: space-between;
    min-width: auto;
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

.profile-image-section {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto;
}

.profile-image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  overflow: hidden;
}

.profile-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-upload-overlay,
.image-delete-overlay {
  position: absolute;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 10px;
  text-align: center;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
  font-size: 0.8rem;
}

.image-upload-overlay {
  bottom: 0;
  left: 0;
  right: 0;
}

.image-delete-overlay {
  top: 0;
  left: 0;
  right: 0;
  background: rgba(220, 53, 69, 0.8);
}

.profile-image-wrapper:hover .image-upload-overlay,
.profile-image-wrapper:hover .image-delete-overlay {
  opacity: 1;
}
.is-own-profile .profile-image-wrapper {
  cursor: pointer;
}
.is-own-profile .profile-image-wrapper:hover .image-upload-overlay,
.is-own-profile .profile-image-wrapper:hover .image-delete-overlay {
  opacity: 1;
}
.hidden {
  display: none;
}
/* 외부 링크 버튼 스타일 */
.external-links {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
  justify-content: center;
}

.external-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  min-width: 80px;
  justify-content: center;
}

.solved-ac-link {
  background-color: #17ce3a;
  color: white;
  border-color: #17ce3a;
}

.solved-ac-link:hover {
  background-color: #14b532;
  border-color: #14b532;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(23, 206, 58, 0.3);
}

.baekjoon-link {
  background-color: #0076c0;
  color: white;
  border-color: #0076c0;
}

.baekjoon-link:hover {
  background-color: #005a91;
  border-color: #005a91;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 118, 192, 0.3);
}

.external-link:active {
  transform: translateY(0);
}

/* 반응형 디자인 */
@media (max-width: 480px) {
  .external-links {
    flex-direction: column;
    gap: 0.5rem;
  }

  .external-link {
    width: 100%;
  }
}
</style>
