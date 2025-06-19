<template>
  <div class="ranking-page">
    <h1 class="page-title">전체 랭킹</h1>

    <div class="filter-controls">
      <RankingFilter
        :show-type-filter="true"
        @update:period="handlePeriodChange"
        @update:type="handleTypeChange"
      />
    </div>

    <div class="ranking-content">
      <RankingTable
        :period="selectedPeriod"
        :type="selectedType"
        :rankings="rankings"
        :loading="loading"
        :title="tableTitle"
        :limit="null"
        :show-full="selectedType === 'team'"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { rankingApi } from '@/api/rankingApi.js'
import RankingFilter from '@/components/ranking/RankingFilter.vue'
import RankingTable from '@/components/ranking/RankingTable.vue'

// 상태 정의
const selectedPeriod = ref('DAILY')
const selectedType = ref('member') // 기본값은 'member' (개인 랭킹)
const selectedDate = ref(null)
const rankings = ref([])
const loading = ref(false)
const error = ref('')

// 테이블 제목 계산
const tableTitle = computed(() => {
  return selectedType.value === 'member' ? '개인 랭킹' : '팀 랭킹'
})

// 기간 필터 변경 핸들러
const handlePeriodChange = (period) => {
  const periodMap = {
    daily: 'DAILY',
    weekly: 'WEEKLY',
    monthly: 'MONTHLY',
  }
  selectedPeriod.value = periodMap[period] || 'DAILY'
  loadRankings() // 필터 변경 시 랭킹 다시 로드
}

// 타입 필터 변경 핸들러
const handleTypeChange = (type) => {
  const typeMap = {
    individual: 'member',
    team: 'team',
  }
  selectedType.value = typeMap[type] || 'member'
  loadRankings() // 필터 변경 시 랭킹 다시 로드
}

// 랭킹 데이터 로드 함수
const loadRankings = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await rankingApi.getRanking(
      selectedType.value,
      selectedPeriod.value,
      selectedDate.value,
    )
    if (response.data && Array.isArray(response.data.rankings)) {
      rankings.value = response.data.rankings
    } else {
      rankings.value = []
    }
  } catch (err) {
    console.error('랭킹 데이터 로드 오류:', err)
    error.value = '랭킹 데이터를 불러오는 중 오류가 발생했습니다.'
    rankings.value = []
  } finally {
    loading.value = false
  }
}

// 컴포넌트 마운트 시 초기 데이터 로드
onMounted(() => {
  loadRankings()
})

// 기간 또는 타입 변경 감지하여 랭킹 다시 로드
watch([selectedPeriod, selectedType], () => {
  loadRankings()
})
</script>

<style scoped>
.ranking-page {
  max-width: 60%;
  margin: 0 auto;
  padding: 2rem;
  box-sizing: border-box;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.filter-controls {
  margin-bottom: 2rem;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 1200px) {
  .ranking-page {
    max-width: 80%;
  }
}

@media (max-width: 768px) {
  .ranking-page {
    max-width: 95%;
    padding: 1rem;
  }
  .page-title {
    font-size: 2rem;
  }
}
</style>
