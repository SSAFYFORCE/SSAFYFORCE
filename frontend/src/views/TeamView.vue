<!-- src/views/TeamView.vue -->
<template>
  <div class="team-view">
    <div class="container">
      <div class="page-header">
        <h1>팀</h1>
        <p>함께 성장할 팀을 찾아보세요</p>
      </div>

      <!-- 검색 및 필터 섹션 (필요 시 유지) -->
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
            <div class="team-header">
              <h3 class="team-name">{{ team.name }}</h3>
            </div>

            <p class="team-description">{{ team.description }}</p>

            <div class="team-stats">
              <div class="stat-item">
                <span class="stat-label">멤버</span>
                <span class="stat-value">{{ team.memberCount }}명</span>
              </div>
            </div>

            <div class="team-members-preview">
              <h4>멤버 미리보기</h4>
              <div class="members-list">
                <div
                  v-for="member in team.teamMembers.slice(0, 3)"
                  :key="member.id"
                  class="member-item"
                >
                  <span class="member-name">{{ member.nickname }}</span>
                </div>
                <div v-if="team.teamMembers.length > 3" class="more-members">
                  +{{ team.teamMembers.length - 3 }}명 더
                </div>
              </div>
            </div>

            <div class="team-actions">
              <button
                v-if="!team.isJoined"
                @click="handleJoinTeam(team)"
                :disabled="joiningTeamId === team.id"
                class="btn btn-primary"
              >
                <template v-if="joiningTeamId === team.id">
                  <font-awesome-icon :icon="['fas', 'spinner']" spin />
                  가입 중...
                </template>
                <template v-else>팀 가입하기</template>
              </button>
              <span v-else class="joined-badge">✓ 가입된 팀</span>
            </div>

            <div class="team-meta">
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

// 팀 목록 로드
const loadTeams = async () => {
  loading.value = true
  try {
    const res = await teamApi.fetchTeams()
    teams.value = res.data.teams
      .filter((t) => t.name.toLowerCase().includes(searchQuery.value.toLowerCase()))
      .map((t) => ({
        id: t.teamId,
        name: t.name,
        description: t.description,
        memberCount: t.memberCount,
        createdAt: t.createdAt,
        teamMembers: t.teamMembers,
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

// 팀 가입 처리
const handleJoinTeam = async (team) => {
  // 1) 로그인 여부 확인
  if (!auth.isLoggedIn) {
    // 로그인 되어 있지 않으면 로그인 페이지로 리다이렉트
    router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
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

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  font-size: 0.9rem;
  min-width: 150px;
}

.filter-select:focus {
  outline: none;
  border-color: var(--samsung-blue);
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 0.9rem;
}

.checkbox-container input {
  margin-right: 0.5rem;
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
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  border-left: 4px solid transparent;
}

.team-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.team-card.joined {
  border-left-color: #28a745;
  background: linear-gradient(135deg, rgba(40, 167, 69, 0.05) 0%, white 100%);
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.team-name {
  font-size: 1.3rem;
  color: #333;
  margin: 0;
}

.team-tier {
  font-weight: 600;
  font-size: 0.9rem;
}

.team-description {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.team-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
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

.team-tags {
  margin-bottom: 1rem;
}

.team-tag {
  display: inline-block;
  background: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  margin: 0.125rem;
  border-radius: 4px;
  font-size: 0.8rem;
  border: 1px solid #dee2e6;
}

.team-members-preview {
  margin-bottom: 1.5rem;
}

.team-members-preview h4 {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.members-list {
  background: #f8f9fa;
  padding: 0.75rem;
  border-radius: 6px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.member-item:last-child {
  margin-bottom: 0;
}

.member-name {
  font-weight: 500;
  flex: 1;
}

.member-tier {
  font-size: 0.8rem;
  font-weight: 600;
}

.leader-badge {
  font-size: 0.8rem;
}

.more-members {
  color: #666;
  font-size: 0.9rem;
  text-align: center;
  padding-top: 0.5rem;
  border-top: 1px solid #e9ecef;
}

.team-actions {
  margin-bottom: 1rem;
  text-align: center;
}

.btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
  position: relative;
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
  padding: 0.5rem 1rem;
  background: #d4edda;
  color: #155724;
  border-radius: 4px;
  font-weight: 500;
  font-size: 0.9rem;
}

.team-meta {
  text-align: center;
  color: #666;
  font-size: 0.8rem;
}

.create-team-section {
  text-align: center;
  margin-top: 2rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-select {
    min-width: 100%;
  }

  .teams-grid {
    grid-template-columns: 1fr;
  }

  .team-stats {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
