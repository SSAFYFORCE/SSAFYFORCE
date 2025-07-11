<!-- src/views/TeamView.vue -->
<template>
  <div class="team-view">
    <div class="container">
      <div class="page-header">
        <h1>팀</h1>
        <p>함께 성장할 팀을 찾아보세요</p>
      </div>

      <!-- 검색 및 필터 섹션 -->
      <div class="search-filter-section">
        <div class="search-container">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="팀 이름이나 설명으로 검색..."
            class="search-input"
            @input="applyFilters"
          />
        </div>
      </div>

      <!-- 팀 목록 -->
      <div class="teams-section">
        <div v-if="loading" class="loading">
          <font-awesome-icon :icon="['fas', 'spinner']" spin />
          <span>팀을 불러오는 중...</span>
        </div>

        <div v-else-if="teams.length === 0" class="no-teams">검색 조건에 맞는 팀이 없습니다.</div>

        <div v-else class="teams-grid">
          <div
            v-for="team in teams"
            :key="team.id"
            class="team-card"
            :class="{ joined: team.isJoined }"
          >
            <!-- 팀 카드 헤더 (클릭 가능) -->
            <div class="team-header" @click="goToTeamDetail(team.id)">
              <div class="team-avatar">
                <img
                  :src="team.profileImage || getDefaultTeamImage(team.name)"
                  :alt="team.name"
                  class="team-image"
                />
              </div>
              <div class="team-basic-info">
                <h3 class="team-name">{{ team.name }}</h3>
                <p class="team-description">{{ team.description }}</p>
              </div>
            </div>

            <!-- 팀 통계 정보 (클릭 가능) -->
            <div class="team-stats" @click="goToTeamDetail(team.id)">
              <div class="stat-item">
                <span class="stat-label">멤버</span>
                <span class="stat-value">{{ team.memberCount }}명</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">생성일</span>
                <span class="stat-value">{{ formatDate(team.createdAt) }}</span>
              </div>
            </div>

            <!-- 팀 멤버 미리보기 (클릭 가능) -->
            <div class="team-members-preview" @click="goToTeamDetail(team.id)">
              <h4>멤버 미리보기</h4>
              <div class="members-list">
                <div
                  v-for="member in team.teamMembers.slice(0, 3)"
                  :key="member.id"
                  class="member-item"
                >
                  <img
                    :src="member.profileImage || getDefaultProfileImage(member.name)"
                    :alt="member.name"
                    class="member-avatar"
                  />
                  <span class="member-name">{{ member.nickname }}</span>
                </div>
                <div v-if="team.teamMembers.length > 3" class="more-members">
                  +{{ team.teamMembers.length - 3 }}명 더
                </div>
                <div v-if="team.teamMembers.length === 0" class="no-members-preview">
                  아직 멤버가 없습니다
                </div>
              </div>
            </div>

            <!-- 팀 액션 버튼들 (클릭 이벤트 전파 방지) -->
            <div class="team-actions">
              <button @click.stop="goToTeamDetail(team.id)" class="btn btn-outline">
                <font-awesome-icon :icon="['fas', 'eye']" />
                상세보기
              </button>

              <button
                v-if="!team.isJoined"
                @click.stop="handleJoinTeam(team)"
                :disabled="joiningTeamId === team.id"
                class="btn btn-primary"
              >
                <template v-if="joiningTeamId === team.id">
                  <font-awesome-icon :icon="['fas', 'spinner']" spin />
                  가입 중...
                </template>
                <template v-else>
                  <font-awesome-icon :icon="['fas', 'plus']" />
                  팀 가입
                </template>
              </button>

              <span v-else class="joined-badge">
                <font-awesome-icon :icon="['fas', 'check-circle']" />
                가입된 팀
              </span>
            </div>

            <!-- 팀 메타 정보 -->
            <div class="team-meta" @click="goToTeamDetail(team.id)">
              <span class="created-date">{{ formatDate(team.createdAt) }} 생성</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 팀 생성 버튼 -->
      <div class="create-team-section">
        <router-link to="/teams/create" class="btn btn-secondary">
          <font-awesome-icon :icon="['fas', 'plus']" /> 새 팀 만들기
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { teamApi } from '@/api/teamApi'
import { useAuthStore } from '@/stores/auth'

const teams = ref([])
const loading = ref(true)
const searchQuery = ref('')
const joiningTeamId = ref(null)

// Pinia auth store
const auth = useAuthStore()
const router = useRouter()

// 날짜 포맷팅
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

// 기본 이미지 생성
const getDefaultTeamImage = (teamName) => {
  const firstChar = teamName?.charAt(0).toUpperCase() || 'T'
  return `https://via.placeholder.com/60x60/1428A0/ffffff?text=${firstChar}`
}

const getDefaultProfileImage = (memberName) => {
  const firstChar = memberName?.charAt(0).toUpperCase() || 'U'
  return `https://via.placeholder.com/32x32/6c757d/ffffff?text=${firstChar}`
}

// 팀 목록 로드
const loadTeams = async () => {
  loading.value = true
  try {
    const res = await teamApi.fetchTeams()
    // 백엔드 API 응답 구조에 맞게 수정
    const teamsData = res.data.teams || []

    teams.value = teamsData
      .filter((t) => t.name.toLowerCase().includes(searchQuery.value.toLowerCase()))
      .map((t) => ({
        id: t.teamId,
        name: t.name,
        description: t.description,
        memberCount: t.memberCount,
        createdAt: t.createdAt,
        teamMembers: t.teamMembers || [],
        profileImage: t.profileImage,
        isJoined: false, // 필요시 로직 수정
      }))
  } catch (error) {
    console.error('팀 로드 실패:', error)
    teams.value = []
  } finally {
    loading.value = false
  }
}

// 검색 필터 적용
const applyFilters = () => {
  loadTeams()
}

// 팀 상세 페이지로 이동
const goToTeamDetail = (teamId) => {
  router.push(`/teams/${teamId}`)
}

// 팀 가입 처리
const handleJoinTeam = async (team) => {
  // 1) 로그인 여부 확인
  if (!auth.isLoggedIn) {
    // 로그인 되어 있지 않으면 로그인 페이지로 리다이렉트
    router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }

  // 2) 가입 요청
  joiningTeamId.value = team.id
  try {
    // 백엔드: POST /api/v1/teams/{teamId}/join
    await teamApi.joinTeam(team.id)
    await loadTeams()
    alert('팀에 성공적으로 가입했습니다!')
  } catch (error) {
    console.error('팀 가입 실패:', error)
    alert(error.response?.data?.message || '팀 가입에 실패했습니다.')
  } finally {
    joiningTeamId.value = null
  }
}

onMounted(async () => {
  await auth.initialize()
  await loadTeams()
})
</script>

<style scoped>
.team-view {
  min-height: calc(100vh - 64px);
  background-color: #f9f9f9;
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

/* 검색 및 필터 섹션 */
.search-filter-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.search-container {
  margin-bottom: 1rem;
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-input:focus {
  outline: none;
  border-color: var(--samsung-blue);
  box-shadow: 0 0 0 2px var(--samsung-blue-alpha);
}

/* 팀 목록 */
.teams-section {
  margin-bottom: 3rem;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem;
  color: #666;
  gap: 1rem;
}

.no-teams {
  text-align: center;
  padding: 3rem;
  color: #666;
  font-size: 1.1rem;
}

.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.team-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  border-left: 4px solid transparent;
  overflow: hidden;
}

.team-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.team-card.joined {
  border-left-color: #28a745;
  background: linear-gradient(135deg, rgba(40, 167, 69, 0.05) 0%, white 100%);
}

/* 팀 헤더 */
.team-header {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.team-header:hover {
  background-color: #f8f9fa;
}

.team-avatar {
  flex-shrink: 0;
}

.team-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  border: 2px solid #eee;
}

.team-basic-info {
  flex: 1;
  min-width: 0;
}

.team-name {
  font-size: 1.3rem;
  color: #333;
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.team-description {
  color: #666;
  margin: 0;
  line-height: 1.5;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 팀 통계 */
.team-stats {
  display: flex;
  justify-content: space-around;
  padding: 1rem 1.5rem;
  background: #f8f9fa;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.team-stats:hover {
  background-color: #e9ecef;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.25rem;
}

.stat-value {
  display: block;
  font-weight: 600;
  color: var(--samsung-blue);
}

/* 팀 멤버 미리보기 */
.team-members-preview {
  padding: 1rem 1.5rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.team-members-preview:hover {
  background-color: #f8f9fa;
}

.team-members-preview h4 {
  font-size: 1rem;
  margin-bottom: 0.75rem;
  color: #333;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.member-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #eee;
}

.member-name {
  font-weight: 500;
  font-size: 0.9rem;
  color: #333;
}

.more-members,
.no-members-preview {
  color: #666;
  font-size: 0.9rem;
  text-align: center;
  padding: 0.5rem;
  background-color: #f1f3f5;
  border-radius: 4px;
  margin-top: 0.5rem;
}

/* 팀 액션 */
.team-actions {
  padding: 1rem 1.5rem;
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  border-top: 1px solid #eee;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  flex: 1;
  justify-content: center;
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
  transform: translateY(-1px);
}

.btn-outline {
  background-color: transparent;
  color: var(--samsung-blue);
  border: 1px solid var(--samsung-blue);
}

.btn-outline:hover {
  background-color: var(--samsung-blue-alpha);
}

.btn-secondary {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #dee2e6;
}

.btn-secondary:hover {
  background-color: #e9ecef;
}

.joined-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  background: #d4edda;
  color: #155724;
  border-radius: 4px;
  font-weight: 500;
  font-size: 0.9rem;
  flex: 1;
  justify-content: center;
}

/* 팀 메타 정보 */
.team-meta {
  padding: 0.75rem 1.5rem;
  text-align: center;
  color: #666;
  font-size: 0.8rem;
  background-color: #fafafa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.team-meta:hover {
  background-color: #f1f3f5;
}

.create-team-section {
  text-align: center;
  margin-top: 2rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .teams-grid {
    grid-template-columns: 1fr;
  }

  .team-header {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }

  .team-stats {
    flex-direction: column;
    gap: 0.75rem;
    text-align: center;
  }

  .team-actions {
    flex-direction: column;
    gap: 0.75rem;
  }
}
</style>
