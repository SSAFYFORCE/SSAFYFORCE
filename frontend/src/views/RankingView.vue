<!-- src/views/RankingView.vue -->
<template>
  <div class="ranking-view">
    <div class="container">
      <div class="page-header">
        <h1>랭킹</h1>
        <p>전체 사용자 및 팀 랭킹을 확인하세요</p>
      </div>

      <!-- 랭킹 필터 -->
      <div class="ranking-filters">
        <RankingFilter @update:period="period = $event" @update:type="type = $event" />
      </div>

      <!-- 랭킹 테이블 -->
      <div class="ranking-content">
        <div class="ranking-header">
          <h2>{{ getPeriodText(period) }} {{ getTypeText(type) }} 랭킹</h2>
          <div class="ranking-info">총 {{ totalCount }}명 중 {{ getCurrentRankText() }}</div>
        </div>

        <div class="ranking-table-wrapper">
          <div v-if="loading" class="loading">
            <font-awesome-icon :icon="['fas', 'spinner']" spin />
            <span>랭킹을 불러오는 중...</span>
          </div>

          <table v-else class="ranking-table">
            <thead>
              <tr>
                <th class="rank-col">순위</th>
                <th class="user-col">{{ type === 'individual' ? '사용자' : '팀' }}</th>
                <th class="tier-col">티어</th>
                <th class="rating-col">레이팅</th>
                <th class="change-col">변동</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="item in rankings"
                :key="item.id"
                :class="[
                  'ranking-row',
                  { 'current-user': item.isCurrent },
                  { 'first-place': item.rank === 1 },
                  { 'top-three': item.rank <= 3 },
                ]"
              >
                <td class="rank-col">
                  <span v-if="item.rank === 1" class="first-place-crown">👑</span>
                  <span v-else-if="item.rank === 2" class="second-place-medal">🥈</span>
                  <span v-else-if="item.rank === 3" class="third-place-medal">🥉</span>
                  <span :class="{ 'first-place-text': item.rank === 1 }">
                    {{ item.rank }}
                  </span>
                </td>
                <td class="user-col">
                  <div class="user-info">
                    <div class="user-image-container">
                      <img
                        :src="type === 'individual' ? item.profileImage : item.teamImage"
                        :alt="type === 'individual' ? item.username : item.teamName"
                        :class="{ 'first-place-image': item.rank === 1 }"
                      />
                      <div v-if="item.rank === 1" class="first-place-glow"></div>
                    </div>
                    <span :class="{ 'first-place-name': item.rank === 1 }">
                      {{ type === 'individual' ? item.username : item.teamName }}
                    </span>
                  </div>
                </td>
                <td class="tier-col">
                  <span :style="getTierColor(item.tier)" class="tier-badge">
                    {{ item.tier }}
                  </span>
                </td>
                <td class="rating-col">
                  <span class="rating-value">{{ item.rating }}</span>
                </td>
                <td class="change-col">
                  <span class="rating-change" :class="getRatingChangeClass()">
                    {{ getRatingChange() }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 페이지네이션 -->
        <div v-if="!loading && totalPages > 1" class="pagination">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 1"
            class="pagination-btn"
          >
            이전
          </button>

          <div class="pagination-pages">
            <button
              v-for="page in displayPages"
              :key="page"
              @click="changePage(page)"
              :class="['pagination-page', { active: page === currentPage }]"
            >
              {{ page }}
            </button>
          </div>

          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage === totalPages"
            class="pagination-btn"
          >
            다음
          </button>
        </div>
      </div>

      <!-- 내 순위 정보 (로그인한 경우) -->
      <div v-if="isLoggedIn" class="my-ranking-section">
        <h3>내 랭킹 정보</h3>
        <div class="my-ranking-card">
          <div class="my-rank-info">
            <span class="my-rank">{{ myRank }}위</span>
            <span class="my-tier" :style="getTierColor(myTier)">{{ myTier }}</span>
            <span class="my-rating">{{ myRating }}점</span>
          </div>
          <div class="rank-progress">
            <div class="progress-text">상위 {{ Math.round((myRank / totalCount) * 100) }}%</div>
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: `${100 - (myRank / totalCount) * 100}%` }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { fetchFullRankings, checkAuthStatus } from '@/utils/datatransferutil'
import RankingFilter from '@/components/ranking/RankingFilter.vue'

// 반응성 데이터
const rankings = ref([])
const loading = ref(true)
const period = ref('daily')
const type = ref('individual')
const currentPage = ref(1)
const totalCount = ref(0)
const totalPages = ref(0)
const isLoggedIn = ref(false)

// 내 랭킹 정보 (시뮬레이트)
const myRank = ref(26)
const myTier = ref('Silver II')
const myRating = ref(1245)

// 계산된 속성
const displayPages = computed(() => {
  const pages = []
  const maxDisplay = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxDisplay / 2))
  let end = Math.min(totalPages.value, start + maxDisplay - 1)

  if (end - start + 1 < maxDisplay) {
    start = Math.max(1, end - maxDisplay + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// 메서드
const getTierColor = (tier) => {
  const tierName = tier.split(' ')[0].toLowerCase()
  const colors = {
    ruby: '#ff0062',
    diamond: '#00b4fc',
    platinum: '#27e2a4',
    gold: '#ec9a00',
    silver: '#435f7a',
    bronze: '#ad5600',
    unrated: '#2d2d2d',
  }
  return { color: colors[tierName] || '#2d2d2d' }
}

const getPeriodText = (period) => {
  const periodMap = {
    daily: '일간',
    weekly: '주간',
    monthly: '월간',
  }
  return periodMap[period] || '일간'
}

const getTypeText = (type) => {
  return type === 'individual' ? '개인' : '팀'
}

const getCurrentRankText = () => {
  const start = (currentPage.value - 1) * 20 + 1
  const end = Math.min(currentPage.value * 20, totalCount.value)
  return `${start}-${end}위`
}

const getRatingChange = () => {
  // 랜덤하게 변동 정보 생성 (실제로는 API에서 받아올 데이터)
  const changes = ['+15', '-8', '+23', '0', '-12', '+7']
  return changes[Math.floor(Math.random() * changes.length)]
}

const getRatingChangeClass = () => {
  const change = getRatingChange()
  if (change.startsWith('+')) return 'positive'
  if (change.startsWith('-')) return 'negative'
  return 'neutral'
}

const loadRankings = async () => {
  loading.value = true
  try {
    const result = await fetchFullRankings(period.value, type.value, currentPage.value, 20)
    rankings.value = result.data
    totalCount.value = result.totalCount
    totalPages.value = result.totalPages
    currentPage.value = result.currentPage
  } catch (error) {
    console.error('랭킹을 불러오는 중 오류 발생:', error)
    rankings.value = []
  } finally {
    loading.value = false
  }
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadRankings()
  }
}

const checkLogin = async () => {
  const user = await checkAuthStatus()
  isLoggedIn.value = !!user
}

// Watch
watch([period, type], () => {
  currentPage.value = 1
  loadRankings()
})

// 마운트 시 실행
onMounted(() => {
  loadRankings()
  checkLogin()
})
</script>

<style scoped>
.ranking-view {
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

.ranking-filters {
  margin-bottom: 2rem;
}

.ranking-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.ranking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.ranking-header h2 {
  color: #333;
  margin: 0;
}

.ranking-info {
  color: #666;
  font-size: 0.9rem;
}

.ranking-table-wrapper {
  overflow-x: auto;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem;
  color: #666;
  gap: 1rem;
}

.ranking-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.ranking-table th {
  background: #f8f9fa;
  padding: 1rem 0.75rem;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
}

.ranking-table td {
  padding: 1rem 0.75rem;
  border-bottom: 1px solid #dee2e6;
}

.rank-col {
  width: 10%;
  text-align: center;
}

.user-col {
  width: 40%;
}

.tier-col {
  width: 20%;
}

.rating-col {
  width: 15%;
  text-align: right;
}

.change-col {
  width: 15%;
  text-align: right;
}

.ranking-row {
  transition: background-color 0.2s;
}

.ranking-row:hover {
  background-color: #f8f9fa;
}

.ranking-row.current-user {
  background-color: var(--samsung-blue-alpha);
}

.ranking-row.first-place {
  background: linear-gradient(
    135deg,
    rgba(255, 215, 0, 0.25) 0%,
    rgba(255, 223, 0, 0.2) 50%,
    rgba(255, 215, 0, 0.15) 100%
  ) !important;
  position: relative;
}

.ranking-row.top-three {
  background-color: rgba(255, 248, 225, 0.5);
}

.first-place-crown,
.second-place-medal,
.third-place-medal {
  font-size: 1.2em;
  margin-right: 0.5rem;
}

.first-place-text {
  font-weight: 900;
  color: #daa520;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  font-size: 1.1em;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-image-container {
  position: relative;
  display: inline-block;
}

.user-info img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.first-place-image {
  border: 2px solid #ffd700;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.6);
}

.first-place-glow {
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  background: conic-gradient(from 0deg, #ffd700, #ffa500, #ff8c00, #ffd700);
  border-radius: 50%;
  z-index: -1;
  animation: spin 3s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.first-place-name {
  font-weight: 700;
  color: #b8860b;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.tier-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 600;
  font-size: 0.8rem;
  background-color: rgba(128, 128, 128, 0.1);
}

.rating-value {
  font-weight: 600;
  color: var(--samsung-blue);
}

.rating-change {
  font-weight: 500;
  font-size: 0.9rem;
}

.rating-change.positive {
  color: #28a745;
}

.rating-change.negative {
  color: #dc3545;
}

.rating-change.neutral {
  color: #6c757d;
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 2rem;
}

.pagination-btn,
.pagination-page {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  background: white;
  color: #495057;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled),
.pagination-page:hover {
  background: #f8f9fa;
  border-color: var(--samsung-blue);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-page.active {
  background: var(--samsung-blue);
  color: white;
  border-color: var(--samsung-blue);
}

.pagination-pages {
  display: flex;
  gap: 0.25rem;
}

/* 내 랭킹 정보 */
.my-ranking-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.my-ranking-section h3 {
  margin-bottom: 1rem;
  color: #333;
  text-align: center;
}

.my-ranking-card {
  text-align: center;
}

.my-rank-info {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 1rem;
}

.my-rank {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--samsung-blue);
}

.my-tier {
  font-size: 1.2rem;
  font-weight: 600;
}

.my-rating {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
}

.rank-progress {
  max-width: 300px;
  margin: 0 auto;
}

.progress-text {
  margin-bottom: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.progress-bar {
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--samsung-blue-light), var(--samsung-blue));
  transition: width 0.3s ease;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .ranking-header {
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
  }

  .ranking-table {
    font-size: 0.8rem;
  }

  .ranking-table th,
  .ranking-table td {
    padding: 0.75rem 0.5rem;
  }

  .my-rank-info {
    flex-direction: column;
    gap: 0.5rem;
  }

  .pagination {
    flex-wrap: wrap;
  }
}
</style>
