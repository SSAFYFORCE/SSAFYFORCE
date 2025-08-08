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
              @error="onImgError"
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
              <!-- 로그인 X -->
              <router-link
                v-if="!isLoggedIn"
                to="/login"
                class="btn btn-primary">
                <font-awesome-icon :icon="['fas', 'user']" /> 로그인 후 가입
              </router-link>

              <!-- 가입 안했고 요청도 안 함 -->
              <button
                v-else-if="joinStatus === 'NONE'"
                @click="handleJoinTeam"
                :disabled="joiningTeam"
                class="btn btn-primary">
                <template v-if="joiningTeam">
                  <font-awesome-icon :icon="['fas', 'spinner']" spin /> 가입 중...
                </template>
                <template v-else>
                  <font-awesome-icon :icon="['fas', 'plus']" /> 팀 가입하기
                </template>
              </button>

              <button
                v-else-if="joinStatus === 'PENDING'"
                class="pending-badge"
                @click="cancelMyRequest"
                @mouseenter="pendingHover = true"
                @mouseleave="pendingHover = false">
            
                <template v-if="!pendingHover">
                  <font-awesome-icon :icon="['fas','hourglass-half']" /> 요청 대기 중
                </template>
                <template v-else>
                  <font-awesome-icon :icon="['fas','times']" /> 신청 취소
                </template>
              </button>

              <!-- 이미 멤버 -->
              <span v-else class="joined-badge">
                <font-awesome-icon :icon="['fas', 'check-circle']" /> 가입된 팀
              </span>
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
                <span
                  class="member-status"
                  :class="{ leader: member.id === team.leaderId }">
                    {{ member.id === team.leaderId ? '팀장' : '멤버' }}
                </span>

                  <!-- 팀장 전용 위임 버튼 -->
                <button
                  v-if="isLeader && member.id !== team.leaderId"
                  class="btn-mandate"
                  :disabled="mandatingId === member.id"
                  @click.stop="delegateLeader(member)"
                >
                  <template v-if="mandatingId === member.id">
                    <font-awesome-icon :icon="['fas','spinner']" spin />
                  </template>
                  <template v-else>
                    <font-awesome-icon :icon="['fas','crown']" /> 팀장 위임
                  </template>
                </button>

              </div>
            </div>
          </div>
        </div>

        <!-- 팀 멤버 목록 아래에 추가 -->
        <div v-if="isLeader && joinRequests.length" class="join-req-section">
          <h2 class="section-title">
            <font-awesome-icon :icon="['fas', 'paper-plane']" />
            가입 요청 ({{ joinRequests.length }}건)
          </h2>

          <div class="req-grid">
            <!-- 가입 요청 카드 (vertical) -->
            <div v-for="req in joinRequests" :key="req.requestId" class="req-card" @click="goToProfile(req.nickname)">
              <!-- 아바타 -->
              <img
                class="req-avatar"
                :src="req.profileImage || defaultProfileImage"
                :alt="req.name"
                @error="e => (e.target.src = defaultProfileImage)"
              />

              <!-- 이름 · 닉네임 -->
              <h3 class="req-name">{{ req.name }}</h3>
              <p  class="req-nickname">@{{ req.nickname }}</p>

              <!-- 버튼 두 개를 가로로 -->
              <div class="req-buttons">
                <!-- 승인 버튼 -->
                <button class="btn-approve" @click.stop="approve(req.requestId)">
                  <font-awesome-icon :icon="['fas','check']" /> 승인
                </button>

                <!-- 거절 버튼 -->
                <button class="btn-reject"  @click.stop="reject(req.requestId)">
                  <font-awesome-icon :icon="['fas','times']" /> 거절
                </button>
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

        <!-- 팀 탈퇴 / 삭제 버튼 -->
        <div v-if="isJoined" class="leave-team-section">
          <button
            class="btn btn-danger"
            :disabled="(isLeader && !isSoloLeader) || leavingTeam"
            @click="!(isLeader && !isSoloLeader) && confirmLeave()"
          >
            <!-- 진행 중 -->
            <template v-if="leavingTeam">
              <font-awesome-icon :icon="['fas','spinner']" spin /> 처리 중...
            </template>

            <!-- 리더 + 다른 멤버 존재 → 위임 안내 -->
            <template v-else-if="isLeader && !isSoloLeader">
              <font-awesome-icon :icon="['fas','crown']" />
              팀장 위임 후 탈퇴 가능
            </template>

            <!-- 리더 혼자 → 팀 삭제 -->
            <template v-else-if="isSoloLeader">
              <font-awesome-icon :icon="['fas','trash-alt']" />
              팀 삭제
            </template>

            <!-- 일반 팀원 → 팀 탈퇴 -->
            <template v-else>
              <font-awesome-icon :icon="['fas','sign-out-alt']" />
              팀 탈퇴
            </template>
          </button>
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

const onImgError = (e) => { e.target.src = defaultProfileImage }
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const pendingIds = ref(new Set())

// 라우트에서 teamId 가져오기
const teamId = computed(() => route.params.teamId)

// 반응성 데이터
const team = ref(null)
const loading = ref(true)
const error = ref('')
const joiningTeam = ref(false)

const pendingHover = ref(false)

const leavingTeam = ref(false)

const mandatingId = ref(null)

// 팀장 위임
const delegateLeader = async (member) => {
  if (!confirm(`'${member.name}' 님에게 팀장을 위임하시겠습니까?`)) return

  try {
    mandatingId.value = member.id
    await teamApi.mandateLeader(teamId.value, member.id)
    alert('팀장 위임이 완료되었습니다.')
    await loadTeamDetail()     // 새 리더 반영
  } catch (e) {
    console.error('[MANDATE] 실패', e)
    alert(e.response?.data?.message || '팀장 위임에 실패했습니다.')
  } finally {
    mandatingId.value = null
  }
}

// 팀 탈퇴
const confirmLeave = async () => {
  // 안전장치
  if (!confirm('정말로 이 팀을 탈퇴하시겠습니까?')) return

  try {
    leavingTeam.value = true
    await teamApi.withdraw(teamId.value, authStore.user.memberId)
    alert('팀에서 탈퇴했습니다.')

    // 내 팀 목록 캐시 갱신 등 필요하면 여기에
    router.push('/teams')          // 팀 목록 페이지로 이동
  } catch (e) {
    console.error('[LEAVE] 실패', e)
    alert(e.response?.data?.message || '팀 탈퇴에 실패했습니다.')
  } finally {
    leavingTeam.value = false
  }
}


// 내 가입 요청 취소
const cancelMyRequest = async () => {
  try {
    await teamApi.cancelJoinRequest(teamId.value)
    pendingIds.value.delete(+teamId.value)   // 컴퓨티드 joinStatus가 자동으로 'NONE' 으로
    await loadTeamDetail()                   // 멤버 수 등 새로고침
    alert('가입 요청을 취소했어요.')
  } catch (e) {
    console.error('[CANCEL] 실패', e)
    alert(e.response?.data?.message || '요청 취소에 실패했습니다.')
  }
}

const joinRequests = ref([])          // 대기 중 요청 목록
const isLeader = computed(() =>
  team.value?.leaderId === authStore.user?.memberId
)

// 계산된 속성
const isLoggedIn = computed(() => authStore.isLoggedIn)

// 현재 사용자가 이 팀에 가입되어 있는지 확인
const isJoined = computed(() => {
  if (!team.value || !authStore.user) return false

  // 백엔드 API 응답에서 nickname은 solvedAcId를 의미
  return team.value.teamMembers.some((member) => member.nickname === authStore.user.solvedAcId)
})

// ❶ 리더 혼자인지 판별
const hasOtherMembers = computed(() =>
  team.value ? team.value.memberCount > 1 : false
)
const isSoloLeader   = computed(() => isLeader.value && !hasOtherMembers.value)

// 팀 상세 정보 로드
const loadTeamDetail = async () => {
  loading.value = true
  error.value   = ''

  try {
    console.log('[LOAD] teamId =', teamId.value)

    // ── 1) 팀 정보 호출 ───────────────────────────
    const { data } = await teamApi.getTeamDetail(teamId.value)
    team.value = data
    console.log('[LOAD] team', data)

    // ── 2) 내가 팀장인지 즉석 판단 ────────────────
    const myId       = authStore.user?.memberId
    const leaderId   = data.leaderId
    const leaderMode = myId === leaderId

    console.log('[CHECK] myId =', myId,
                'leaderId =', leaderId,
                '→ leaderMode =', leaderMode)

    // ── 3) 팀장일 때 가입요청 목록 가져오기 ───────
    if (leaderMode) {
      const { data: reqList } =
        await teamApi.fetchJoinRequests(teamId.value)

      joinRequests.value = reqList
      console.log('[JOIN‑REQ] count =', reqList.length, reqList)
    }

    // ── 4) (선택) 내가 보낸 가입요청 목록 ─────────
    if (isLoggedIn.value) {
      const { data: myReq } = await teamApi.getmyTeamJoinRequestList()
      pendingIds.value = new Set(myReq.teamList)
      console.log('[MY‑REQ] teamList =', myReq.teamList)
    }
  } catch (err) {
    console.error('[ERROR] loadTeamDetail →', err)
    error.value =
      err.response?.data?.message || '팀 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}


const approve = async (reqId) => {
  await teamApi.approveJoinRequest(teamId.value, reqId)
  joinRequests.value = joinRequests.value.filter(r => r.requestId  !== reqId)
  // 멤버 수 증가 → 팀 정보 재조회
  await loadTeamDetail()
}

const reject = async (reqId) => {
  await teamApi.rejectJoinRequest(teamId.value, reqId)
  joinRequests.value = joinRequests.value.filter(r => r.requestId !== reqId)
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
    await teamApi.requestJoin(teamId.value)
    pendingIds.value.add(Number(teamId.value))
    console.log('팀 가입 성공')
    alert('가입 요청이 전송되었습니다. 리더 승인을 기다려주세요!')

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

const joinStatus = computed(() => {
  if (!isLoggedIn.value) return 'NONE'                      // 비로그인
  if (isJoined.value)     return 'JOINED'                   // 이미 팀 멤버
  if (pendingIds.value.has(+teamId.value)) return 'PENDING' // 승인 대기
  return 'NONE'                                             // 그 밖의 경우
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

.member-status.leader   { background:#ff9800; }   /* 팀장용 – 주황 */


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

.pending-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  background: #fff3cd;   /* 노란색 */
  color: #856404;
  border-radius: 8px;
  font-weight: 500;
  font-size: 1rem;
  border:none;
}

.pending-badge:hover{
  background:#f8d7da;color:#721c24;       
}
.pending-badge:disabled{opacity:.6;cursor:not-allowed}

.join-req-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0,0,0,.08);
  padding: 2rem;
  margin-bottom: 2rem;
}

.req-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill,minmax(250px,1fr));
  gap: 1rem;
}

.req-card {
  display: flex;
  flex-direction: column;   /* 세로 배치 */
  align-items: center;
  text-align: center;
  gap: 0.6rem;
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  transition: all .2s;
  cursor: default;
  border: 2px solid transparent;
}

.req-card:hover {
  background: #e9ecef;
  border-color: var(--samsung-blue);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,.1);
}

.req-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,.1);
}

.req-info {
  flex: 1;
  min-width: 0;
}

.req-name      { font-size: 1.05rem; font-weight: 600; color:#333; margin:0; }
.req-nickname  { font-size: .9rem;   color:#666;       margin:0; }

.req-actions {
  display: flex;
  gap: .5rem;
}

.req-buttons {
  display: flex;
  gap: .5rem;           /* 버튼 사이 간격 */
  margin-top: .8rem;    /* 아바타/텍스트와 간격 */
}

.btn-approve,
.btn-reject {
  flex: 1 1 80px;       /* 두 버튼이 같은 폭 */
  justify-content: center;
}

.btn-approve,
.btn-reject {
  display: inline-flex;
  align-items: center;
  gap: .25rem;
  padding: .4rem .7rem;
  border-radius: 6px;
  font-size: .85rem;
  font-weight: 500;
  border: none;
  cursor: pointer;
}

.btn-approve,
.btn-reject {
  display: inline-flex;       /* 아이콘+텍스트 한 줄 배치 */
  align-items: center;
  gap: .25rem;                /* 아이콘‑텍스트 사이 간격 */
  white-space: nowrap;        /* 줄바꿈 방지 → “X 거절” */
}

.btn-danger {
  background:#dc3545;
  color:#fff;
}
.btn-danger:hover:not(:disabled){
  background:#c82333;
  transform:translateY(-1px);
}
.leave-team-section{
  margin-top:2rem;
  text-align:center;
}


.btn-approve { background: #28a745; color: #fff; }
.btn-reject  { background: #dc3545; color: #fff; }

.btn-approve:hover { background: #218838; }
.btn-reject:hover  { background: #c82333; }
.btn-mandate{
  margin-left:.5rem;
  padding: 0.37rem 0.75rem;
  font-size: 0.8rem;
  font-weight: 500;
  border:none;
  border-radius: 20px;
  background:#ffc107;      /* 호박색 */
  color:#212529;
  cursor:pointer;
  display:inline-flex;
  align-items:center;
  gap:.25rem;
}


.btn-mandate:hover:not(:disabled){
  background:#e0a800;
  transform:translateY(-1px);
}
.btn-mandate:disabled{
  opacity:.6;
  cursor:not-allowed;
}


</style>
