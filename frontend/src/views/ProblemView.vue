<!-- src/views/ProblemView.vue -->
<template>
  <div class="problem-view">
    <div class="container">
      <div class="page-header">
        <h1>문제</h1>
        <p>다양한 알고리즘 문제를 풀어보세요</p>
      </div>

      <!-- 필터 섹션 -->
      <div class="filters-section">
        <div class="search-container">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="문제 제목이나 번호로 검색..."
            class="search-input"
            @input="handleSearch"
          />
        </div>

        <div class="filter-controls">
          <select v-model="selectedTier" @change="applyFilters" class="filter-select">
            <option value="">모든 티어</option>
            <option value="Bronze V">Bronze V</option>
            <option value="Bronze IV">Bronze IV</option>
            <option value="Bronze III">Bronze III</option>
            <option value="Bronze II">Bronze II</option>
            <option value="Bronze I">Bronze I</option>
            <option value="Silver V">Silver V</option>
            <option value="Silver IV">Silver IV</option>
            <option value="Silver III">Silver III</option>
            <option value="Silver II">Silver II</option>
            <option value="Silver I">Silver I</option>
            <option value="Gold V">Gold V</option>
            <option value="Gold IV">Gold IV</option>
            <option value="Gold III">Gold III</option>
            <option value="Gold II">Gold II</option>
            <option value="Gold I">Gold I</option>
            <option value="Platinum V">Platinum V</option>
            <option value="Platinum IV">Platinum IV</option>
            <option value="Platinum III">Platinum III</option>
            <option value="Platinum II">Platinum II</option>
            <option value="Platinum I">Platinum I</option>
            <option value="Diamond V">Diamond V</option>
            <option value="Diamond IV">Diamond IV</option>
            <option value="Diamond III">Diamond III</option>
            <option value="Diamond II">Diamond II</option>
            <option value="Diamond I">Diamond I</option>
            <option value="Ruby V">Ruby V</option>
            <option value="Ruby IV">Ruby IV</option>
            <option value="Ruby III">Ruby III</option>
            <option value="Ruby II">Ruby II</option>
            <option value="Ruby I">Ruby I</option>
          </select>

          <select v-model="selectedSolvedStatus" @change="applyFilters" class="filter-select">
            <option value="">모든 문제</option>
            <option :value="true">해결한 문제</option>
            <option :value="false">해결하지 않은 문제</option>
          </select>

          <select v-model="selectedAlgorithm" @change="applyFilters" class="filter-select">
            <option value="">모든 알고리즘</option>
            <option value="구현">구현</option>
            <option value="수학">수학</option>
            <option value="다이나믹 프로그래밍">다이나믹 프로그래밍</option>
            <option value="그래프 이론">그래프 이론</option>
            <option value="그래프 탐색">그래프 탐색</option>
            <option value="너비 우선 탐색">너비 우선 탐색</option>
            <option value="깊이 우선 탐색">깊이 우선 탐색</option>
            <option value="트리">트리</option>
            <option value="재귀">재귀</option>
          </select>
        </div>
      </div>

      <!-- 문제 목록 -->
      <div class="problems-section">
        <div v-if="loading" class="loading">
          <font-awesome-icon :icon="['fas', 'spinner']" spin />
          <span>문제를 불러오는 중...</span>
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
              <span v-for="algorithm in problem.algorithms" :key="algorithm" class="algorithm-tag">
                {{ algorithm }}
              </span>
            </div>

            <div class="problem-actions">
              <a :href="problem.url" target="_blank" class="btn btn-primary"> 문제 풀기 </a>
            </div>
          </div>
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
            <div class="stat-number">{{ Math.round((solvedCount / totalProblems) * 100) }}%</div>
            <div class="stat-label">해결률</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { fetchProblems } from '@/utils/datatransferutil'

// 반응성 데이터
const problems = ref([])
const loading = ref(true)
const searchQuery = ref('')
const selectedTier = ref('')
const selectedSolvedStatus = ref('')
const selectedAlgorithm = ref('')

// 계산된 속성
const solvedCount = computed(() => problems.value.filter((p) => p.isSolved).length)
const unsolvedCount = computed(() => problems.value.filter((p) => !p.isSolved).length)
const totalProblems = computed(() => problems.value.length)

// 티어별 색상을 반환하는 함수
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

// 문제 목록 로드
const loadProblems = async () => {
  loading.value = true
  try {
    const filters = {
      searchQuery: searchQuery.value,
      tier: selectedTier.value,
      solvedStatus: selectedSolvedStatus.value,
      algorithm: selectedAlgorithm.value,
    }
    const data = await fetchProblems(filters)
    problems.value = data
  } catch (error) {
    console.error('문제를 불러오는 중 오류 발생:', error)
    problems.value = []
  } finally {
    loading.value = false
  }
}

// 검색 처리
const handleSearch = () => {
  applyFilters()
}

// 필터 적용
const applyFilters = () => {
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
</style>
