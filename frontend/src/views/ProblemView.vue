<!-- src/views/ProblemView.vue -->
<template>
  <div class="problem-view">
    <div class="container">
      <div class="page-header">
        <h1>문제</h1>
        <p>다양한 알고리즘 문제를 풀어보세요</p>
      </div>

      <!-- 문제 목록 -->
      <div class="problems-section">
        <div v-if="loading" class="loading">
          <font-awesome-icon :icon="['fas', 'spinner']" spin />
          <span>문제를 불러오는 중...</span>
        </div>

        <div v-else-if="error" class="error-message">
          <p>{{ error }}</p>
          <button class="btn btn-primary" @click="loadProblems">다시 시도</button>
        </div>

        <div v-else-if="problems.length === 0" class="no-problems">
          검색 조건에 맞는 문제가 없습니다.
        </div>

        <div v-else class="problems-grid">
          <div
            v-for="problem in problems"
            :key="problem.id"
            class="problem-card"
            :class="{ solved: problem.isSolved }"
          >
            <div class="problem-header">
              <div class="problem-number">{{ problem.problemNumber }}</div>
              <div class="problem-status">
                <span v-if="problem.isSolved" class="solved-badge">✓</span>
              </div>
            </div>

            <h3 class="problem-title">{{ problem.title }}</h3>

            <div class="problem-meta">
              <span class="problem-tier" :style="getTierColor(problem.tier)">
                {{ problem.tier }}
              </span>
            </div>

            <div class="problem-algorithms">
              <span
                v-for="algorithm in problem.algorithms || []"
                :key="algorithm.name || algorithm"
                class="algorithm-tag"
              >
                {{ algorithm.name || algorithm }}
              </span>
            </div>

            <div class="problem-actions">
              <a :href="problem.url" target="_blank" class="btn btn-primary"> 문제 풀기 </a>
            </div>
          </div>
        </div>

        <!-- 페이지네이션 -->
        <div v-if="totalPages > 1" class="pagination">
          <button class="pagination-btn" :disabled="currentPage === 0" @click="changePage(0)">
            &lt;&lt;
          </button>

          <button
            v-for="page in displayPages"
            :key="page"
            class="pagination-btn"
            :class="{ active: page - 1 === currentPage }"
            @click="changePage(page - 1)"
          >
            {{ page }}
          </button>

          <button
            class="pagination-btn"
            :disabled="currentPage + 10 >= totalPages"
            @click="changePage(currentPage + 10)"
          >
            +10
          </button>

          <button
            class="pagination-btn"
            :disabled="currentPage + 100 >= totalPages"
            @click="changePage(currentPage + 100)"
          >
            +100
          </button>

          <button
            class="pagination-btn"
            :disabled="currentPage + 500 >= totalPages"
            @click="changePage(currentPage + 500)"
          >
            +500
          </button>

          <button
            class="pagination-btn"
            :disabled="currentPage === totalPages - 1"
            @click="changePage(totalPages - 1)"
          >
            &gt;&gt;
          </button>
        </div>
      </div>

      <!-- 통계 섹션 -->
      <div class="stats-section">
        <h2>해결 현황</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-number">{{ solvedCount }}</div>
            <div class="stat-label">해결한 문제</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ unsolvedCount }}</div>
            <div class="stat-label">미해결 문제</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">
              {{ totalProblems > 0 ? Math.round((solvedCount / totalProblems) * 100) : 0 }}%
            </div>
            <div class="stat-label">해결률</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { problemApi } from '@/api/problemApi'

// 반응성 데이터
const problems = ref([])
const loading = ref(true)
const error = ref('')
const searchQuery = ref('')
const selectedTier = ref('')
const selectedSolvedStatus = ref('')
const selectedAlgorithm = ref('')
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 15

// 계산된 속성
const solvedCount = computed(() => problems.value.filter((p) => p.isSolved).length)
const unsolvedCount = computed(() => problems.value.filter((p) => !p.isSolved).length)
const totalProblems = computed(() => totalElements.value)

// 페이지네이션 표시 페이지 계산
const displayPages = computed(() => {
  const pages = []
  const maxDisplay = 5
  let start = Math.max(1, currentPage.value + 1 - Math.floor(maxDisplay / 2))
  let end = Math.min(totalPages.value, start + maxDisplay - 1)

  if (end - start + 1 < maxDisplay) {
    start = Math.max(1, end - maxDisplay + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// 티어별 색상을 반환하는 함수
const getTierColor = (tier) => {
  if (!tier) return { color: '#2d2d2d' }

  const tierName = tier.split(' ')[0][0]
  const colors = {
    R: '#ff0062',
    D: '#00b4fc',
    P: '#27e2a4',
    G: '#ec9a00',
    S: '#435f7a',
    B: '#ad5600',
    unrated: '#2d2d2d',
  }
  return { color: colors[tierName] || '#2d2d2d' }
}

// 쿼리 파라미터 생성 함수
const buildQueryParams = () => {
  const params = new URLSearchParams()

  // 페이지네이션 파라미터
  params.append('page', currentPage.value.toString())
  params.append('size', pageSize.toString())

  // 필터 파라미터
  if (selectedTier.value) params.append('tier', selectedTier.value)
  if (selectedAlgorithm.value) params.append('algorithm', selectedAlgorithm.value)
  if (searchQuery.value) params.append('search', searchQuery.value)
  if (selectedSolvedStatus.value !== '')
    params.append('solved', selectedSolvedStatus.value.toString())

  return params.toString()
}

// 문제 목록 로드
const loadProblems = async () => {
  loading.value = true
  error.value = ''

  try {
    const queryParams = buildQueryParams()
    const response = await problemApi.getAllProblems(queryParams)

    // 응답 데이터 구조에 따라 조정이 필요할 수 있습니다
    const data = response.data

    if (data) {
      problems.value = data.content || data.data || data
      totalPages.value =
        data.totalPages || Math.ceil((data.totalElements || data.total || 0) / pageSize)
      totalElements.value =
        data.totalElements ||
        data.total ||
        (Array.isArray(problems.value) ? problems.value.length : 0)
    } else {
      problems.value = []
      totalPages.value = 0
      totalElements.value = 0
    }
  } catch (err) {
    console.error('문제를 불러오는 중 오류 발생:', err)
    error.value = err.response?.data?.message || err.message || '문제를 불러오는데 실패했습니다.'
    problems.value = []
    totalPages.value = 0
    totalElements.value = 0
  } finally {
    loading.value = false
  }
}

// 페이지 변경
const changePage = (page) => {
  currentPage.value = page
  loadProblems()
}

// 컴포넌트 마운트 시 문제 목록 로드
onMounted(() => {
  loadProblems()
})
</script>

<style scoped>
.problem-view {
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

/* 필터 섹션 */
.filters-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.search-container {
  margin-bottom: 1rem;
  display: flex;
  gap: 0.5rem;
}

.search-input {
  flex: 1;
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

.search-btn {
  padding: 0.75rem 1.5rem;
  background: var(--samsung-blue);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.search-btn:hover {
  background: var(--samsung-blue-dark);
}

.filter-controls {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
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

/* 문제 목록 */
.problems-section {
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

.error-message {
  text-align: center;
  padding: 3rem;
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

.no-problems {
  text-align: center;
  padding: 3rem;
  color: #666;
  font-size: 1.1rem;
}

.problems-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.problem-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  border-left: 4px solid transparent;
}

.problem-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.problem-card.solved {
  border-left-color: #28a745;
  background: linear-gradient(135deg, rgba(40, 167, 69, 0.05) 0%, white 100%);
}

.problem-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.problem-number {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--samsung-blue);
}

.solved-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #28a745;
  color: white;
  border-radius: 50%;
  font-size: 0.8rem;
  font-weight: bold;
}

.problem-title {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #333;
  line-height: 1.4;
}

.problem-meta {
  margin-bottom: 1rem;
}

.problem-tier {
  font-weight: 600;
  font-size: 0.9rem;
}

.problem-algorithms {
  margin-bottom: 1.5rem;
}

.algorithm-tag {
  display: inline-block;
  background: #f8f9fa;
  color: #495057;
  padding: 0.25rem 0.5rem;
  margin: 0.125rem;
  border-radius: 4px;
  font-size: 0.8rem;
  border: 1px solid #e9ecef;
}

.problem-actions {
  text-align: center;
}

.btn {
  display: inline-block;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  border: none;
  cursor: pointer;
}

.btn-primary {
  background-color: var(--samsung-blue);
  color: white;
}

.btn-primary:hover {
  background-color: var(--samsung-blue-dark);
  transform: translateY(-1px);
}

/* 통계 섹션 */
.stats-section {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stats-section h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  text-align: center;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: var(--samsung-blue);
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .filter-controls {
    flex-direction: column;
  }

  .filter-select {
    min-width: 100%;
  }

  .problems-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}

/* 페이지네이션 스타일 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 2rem;
}

.pagination-btn {
  min-width: 2.5rem;
  height: 2.5rem;
  padding: 0 0.75rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  font-size: 0.9rem;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled) {
  background: #f0f0f0;
  border-color: #ccc;
}

.pagination-btn.active {
  background: var(--samsung-blue);
  color: white;
  border-color: var(--samsung-blue);
}

.pagination-btn:disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}
</style>
