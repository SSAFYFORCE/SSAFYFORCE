<template>
  <div class="ranking-page">
    <h1 class="page-title">전체 랭킹</h1>

    <div class="filter-controls">
      <RankingFilter :show-type-filter="false" @update:period="handlePeriodChange" />
    </div>

    <div class="rankings-grid">
      <RankingTable
        :period="selectedPeriod"
        type="member"
        :rankings="memberRankings"
        :loading="loadingMember"
        :limit="null"
        title="개인"
      />

      <RankingTable
        :period="selectedPeriod"
        type="team"
        :rankings="teamRankings"
        :loading="loadingTeam"
        :limit="null"
        title="팀"
        :show-full="true"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { rankingApi } from '@/api/rankingApi.js'
import RankingFilter from '@/components/ranking/RankingFilter.vue'
import RankingTable from '@/components/ranking/RankingTable.vue'

// 상태 정의 (초기값을 MONTHLY로 변경)
const selectedPeriod = ref('MONTHLY')
const selectedDate = ref(null)
const memberRankings = ref([])
const teamRankings = ref([])
const loadingMember = ref(false)
const loadingTeam = ref(false)
const error = ref('')

// 기간 필터 변경 핸들러
const handlePeriodChange = (period) => {
  const periodMap = {
    daily: 'DAILY',
    weekly: 'WEEKLY',
    monthly: 'MONTHLY',
  }
  selectedPeriod.value = periodMap[period] || 'MONTHLY'
  loadAllRankings()
}

// 개인 랭킹과 팀 랭킹 데이터를 모두 로드하는 함수
const loadAllRankings = async () => {
  loadingMember.value = true
  loadingTeam.value = true
  error.value = ''

  try {
    const memberResponse = await rankingApi.getRanking(
      'member',
      selectedPeriod.value,
      selectedDate.value,
    )
    if (memberResponse.data && Array.isArray(memberResponse.data.rankings)) {
      memberRankings.value = memberResponse.data.rankings
    } else {
      memberRankings.value = []
    }
  } catch (err) {
    console.error('개인 랭킹 로드 오류:', err)
    error.value = '개인 랭킹 데이터를 불러오는 중 오류가 발생했습니다.'
    memberRankings.value = []
  } finally {
    loadingMember.value = false
  }

  try {
    const teamResponse = await rankingApi.getRanking(
      'team',
      selectedPeriod.value,
      selectedDate.value,
    )
    if (teamResponse.data && Array.isArray(teamResponse.data.rankings)) {
      teamRankings.value = teamResponse.data.rankings
    } else {
      teamRankings.value = []
    }
  } catch (err) {
    console.error('팀 랭킹 로드 오류:', err)
    error.value =
      (error.value ? error.value + '\n' : '') + '팀 랭킹 데이터를 불러오는 중 오류가 발생했습니다.'
    teamRankings.value = []
  } finally {
    loadingTeam.value = false
  }
}

// 컴포넌트 마운트 시 초기 데이터 로드
onMounted(() => {
  loadAllRankings()
})

// 기간 변경 감지하여 랭킹 다시 로드
watch(selectedPeriod, () => {
  loadAllRankings()
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

.rankings-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
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
  .rankings-grid {
    grid-template-columns: 1fr;
  }
}
</style>
